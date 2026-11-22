package com.example.tenantmanagementsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tenantmanagementsystem.databinding.FragmentTenantDetailBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

class TenantDetailFragment : Fragment() {
    private var _binding: FragmentTenantDetailBinding? = null
    private val binding get() = _binding!!
    private val vm: TenantViewModel by activityViewModels()
    private var tenantId = 0L
    private var tenant: Tenant? = null
    private var paidThisMonth = 0.0

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentTenantDetailBinding.inflate(i, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tenantId = arguments?.getLong("tenantId", 0L) ?: 0L
        if (tenantId == 0L) {
            Toast.makeText(requireContext(), "Tenant not found", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        // Payment History Adapter
        val paymentAdapter = RowAdapter(onLongClick = { row ->
            AlertDialog.Builder(requireContext()).setTitle("Delete this payment?")
                .setPositiveButton("Delete") { _, _ -> vm.deletePayment(row.id) }
                .setNegativeButton("Cancel", null).show()
        })
        binding.paymentsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.paymentsRecycler.adapter = paymentAdapter

        // Maintenance Adapter (Water & Electricity)
        val maintenanceAdapter = RowAdapter(
            onClick = { row ->
                showUpdateMaintenanceDialog(row.id)
            },
            onLongClick = { row ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete maintenance issue?")
                    .setMessage("Are you sure you want to delete this maintenance record?")
                    .setPositiveButton("Delete") { _, _ -> vm.deleteMaintenance(row.id) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        binding.maintenancesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.maintenancesRecycler.adapter = maintenanceAdapter

        // Collect single tenant reactively so edits in AddEditTenantFragment re-render automatically
        collect(vm.observeTenant(tenantId)) { t ->
            if (t == null) return@collect
            tenant = t
            render()
        }

        // Collect payments
        collect(vm.paymentsFor(tenantId)) { list ->
            paymentAdapter.submitList(list.map { Row(it.id, money(it.amount), dateFmt.format(Date(it.paidOn))) })
            paidThisMonth = list.filter { it.paidOn >= monthStart() }.sumOf { it.amount }
            render()
        }

        // Collect Water & Electricity maintenances
        collect(vm.maintenancesFor(tenantId)) { list ->
            maintenanceAdapter.submitList(list.map { m ->
                val icon = when (m.type) {
                    "Water" -> "💧 Water Issue"
                    "Electricity" -> "⚡ Electricity Issue"
                    "General" -> "🛠️ General Issue"
                    else -> "⚙️ ${m.type}"
                }
                val subtitle = "${m.description} • Cost: ${money(m.cost)} • Status: [${m.status}]"
                Row(m.id, icon, subtitle)
            })
            binding.emptyMaintenanceTextView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.callButton.setOnClickListener {
            tenant?.let { startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.phone}"))) }
        }
        binding.shareButton.setOnClickListener {
            tenant?.let {
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, it.summary())
                }
                startActivity(Intent.createChooser(send, "Share tenant"))
            }
        }
        binding.payButton.setOnClickListener { showPaymentDialog() }
        binding.reportMaintenanceButton.setOnClickListener { showReportMaintenanceDialog() }
        binding.editButton.setOnClickListener {
            findNavController().navigate(R.id.addEditTenantFragment, bundleOf("tenantId" to tenantId))
        }
        binding.deleteButton.setOnClickListener {
            val t = tenant ?: return@setOnClickListener
            AlertDialog.Builder(requireContext())
                .setTitle("Delete ${t.name}?")
                .setMessage("Their payment and maintenance history will be deleted too.")
                .setPositiveButton("Delete") { _, _ ->
                    vm.delete(t)
                    findNavController().popBackStack()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun render() {
        val t = tenant ?: return
        binding.detailTextView.text = t.summary()
        binding.statusTextView.text = "Paid this month: ${money(paidThisMonth)}\n" +
                "Balance: ${money((t.monthlyRent - paidThisMonth).coerceAtLeast(0.0))}"
    }

    private fun showPaymentDialog() {
        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Amount (KSh)"
        }
        AlertDialog.Builder(requireContext()).setTitle("Record payment").setView(input)
            .setPositiveButton("Save") { _, _ ->
                val amt = input.text.toString().toDoubleOrNull()
                if (amt == null || amt <= 0) {
                    Toast.makeText(requireContext(), "Enter a valid amount above 0", Toast.LENGTH_SHORT).show()
                } else {
                    vm.addPayment(Payment(tenantId = tenantId, amount = amt, paidOn = System.currentTimeMillis()))
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun showReportMaintenanceDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val typeSpinner = Spinner(requireContext())
        val types = arrayOf("Water", "Electricity", "General")
        typeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, types)
        layout.addView(typeSpinner)

        val descInput = EditText(requireContext()).apply {
            hint = "Issue description (e.g. Leaking pipe / Power outage)"
        }
        layout.addView(descInput)

        val costInput = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Estimated cost (KSh)"
        }
        layout.addView(costInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Report Maintenance Issue")
            .setView(layout)
            .setPositiveButton("Submit Issue") { _, _ ->
                val type = typeSpinner.selectedItem.toString()
                val desc = descInput.text.toString().trim()
                val cost = costInput.text.toString().toDoubleOrNull() ?: 0.0

                if (desc.isEmpty()) {
                    Toast.makeText(requireContext(), "Description is required", Toast.LENGTH_SHORT).show()
                } else {
                    vm.addMaintenance(
                        Maintenance(
                            tenantId = tenantId,
                            type = type,
                            description = desc,
                            cost = cost,
                            status = "Pending"
                        )
                    )
                    Toast.makeText(requireContext(), "Maintenance issue reported", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUpdateMaintenanceDialog(maintenanceId: Long) {
        val statuses = arrayOf("Pending", "In Progress", "Resolved")
        AlertDialog.Builder(requireContext())
            .setTitle("Update Issue Status")
            .setItems(statuses) { _, which ->
                val newStatus = statuses[which]
                viewLifecycleOwner.lifecycleScope.launch {
                    val list = vm.maintenancesFor(tenantId).firstOrNull()
                    val item = list?.find { it.id == maintenanceId }
                    if (item != null && item.status != newStatus) {
                        vm.updateMaintenance(item.copy(status = newStatus))
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
