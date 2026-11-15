package com.example.tenantmanagementsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tenantmanagementsystem.databinding.FragmentTenantDetailBinding
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
        _binding = FragmentTenantDetailBinding.inflate(i, c, false); return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tenantId = requireArguments().getLong("tenantId")

        val adapter = RowAdapter(onLongClick = { row ->
            AlertDialog.Builder(requireContext()).setTitle("Delete this payment?")
                .setPositiveButton("Delete") { _, _ -> vm.deletePayment(row.id) }
                .setNegativeButton("Cancel", null).show()
        })
        binding.paymentsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.paymentsRecycler.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch { tenant = vm.getTenant(tenantId); render() }
        collect(vm.paymentsFor(tenantId)) { list ->
            adapter.submitList(list.map { Row(it.id, money(it.amount), dateFmt.format(Date(it.paidOn))) })
            paidThisMonth = list.filter { it.paidOn >= monthStart() }.sumOf { it.amount }
            render()
        }

        binding.callButton.setOnClickListener {
            tenant?.let { startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.phone}"))) }
        }
        binding.shareButton.setOnClickListener {
            tenant?.let {
                val send = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, it.summary()) }
                startActivity(Intent.createChooser(send, "Share tenant"))
            }
        }
        binding.payButton.setOnClickListener { showPaymentDialog() }
        binding.editButton.setOnClickListener {
            findNavController().navigate(R.id.addEditTenantFragment, bundleOf("tenantId" to tenantId))
        }
        binding.deleteButton.setOnClickListener {
            val t = tenant ?: return@setOnClickListener
            AlertDialog.Builder(requireContext()).setTitle("Delete ${t.name}?")
                .setMessage("Their payment history will be deleted too.")
                .setPositiveButton("Delete") { _, _ -> vm.delete(t); findNavController().popBackStack() }
                .setNegativeButton("Cancel", null).show()
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
                    Toast.makeText(requireContext(), "Enter a valid amount", Toast.LENGTH_SHORT).show()
                } else vm.addPayment(Payment(tenantId = tenantId, amount = amt, paidOn = System.currentTimeMillis()))
            }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}