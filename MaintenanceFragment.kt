package com.example.tenantmanagementsystem

import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tenantmanagementsystem.databinding.FragmentMaintenanceBinding

class MaintenanceFragment : Fragment() {

    private var _binding: FragmentMaintenanceBinding? = null
    private val binding get() = _binding!!
    private val vm: TenantViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaintenanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RowAdapter(
            onClick = { row ->
                showUpdateStatusDialog(row.id)
            },
            onDeleteClick = { row ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Issue?")
                    .setMessage("Are you sure you want to delete this maintenance record?")
                    .setPositiveButton("Delete") { _, _ -> vm.deleteMaintenance(row.id) }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onLongClick = { row ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Issue?")
                    .setMessage("Are you sure you want to delete this maintenance record?")
                    .setPositiveButton("Delete") { _, _ -> vm.deleteMaintenance(row.id) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.maintenanceRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.maintenanceRecycler.adapter = adapter

        // Handle Chip filtering
        binding.typeChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.contains(R.id.chipWater)) {
                vm.setMaintenanceType("Water")
            } else if (checkedIds.contains(R.id.chipElectricity)) {
                vm.setMaintenanceType("Electricity")
            } else if (checkedIds.contains(R.id.chipGeneral)) {
                vm.setMaintenanceType("General")
            } else {
                vm.setMaintenanceType("All")
            }
        }

        // Collect maintenance list flow
        collect(vm.maintenanceList) { list ->
            adapter.submitList(list.map { m ->
                val icon = when (m.type) {
                    "Water" -> "💧 Water"
                    "Electricity" -> "⚡ Electricity"
                    "General" -> "🛠️ General"
                    else -> "⚙️ ${m.type}"
                }
                val title = "$icon • ${m.tenantName} (Unit ${m.unit})"
                val subtitle = "${m.description} • Cost: ${money(m.cost)} • Status: [${m.status}]"
                Row(m.id, title, subtitle)
            })
            binding.emptyMaintenanceText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.logIssueButton.setOnClickListener {
            showLogIssueDialog()
        }
    }

    private fun showLogIssueDialog() {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val infoText = TextView(requireContext()).apply {
            text = "General Maintenance covers:\n• Everyday upkeep: Light bulbs, minor sink/shower drains, cleanliness.\n• Tenant damage: Walls, broken glass, guest/negligence damage."
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setPadding(0, 0, 0, 16)
        }
        layout.addView(infoText)

        val tenantSpinner = Spinner(requireContext())
        val tenantsList = vm.tenants.value
        if (tenantsList.isEmpty()) {
            Toast.makeText(requireContext(), "Please add a tenant first", Toast.LENGTH_SHORT).show()
            return
        }

        val tenantNames = tenantsList.map { "${it.name} (Unit ${it.unit})" }
        tenantSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tenantNames)
        layout.addView(tenantSpinner)

        val typeSpinner = Spinner(requireContext())
        val types = arrayOf("Water", "Electricity", "General") // Plumbing removed
        typeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, types)
        layout.addView(typeSpinner)

        val descInput = EditText(requireContext()).apply {
            hint = "Description (e.g. Blown light bulb / Wall damage)"
        }
        layout.addView(descInput)

        val costInput = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Estimated Cost (KSh)"
        }
        layout.addView(costInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Log Maintenance Issue")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val selectedTenantIndex = tenantSpinner.selectedItemPosition
                val tenantId = tenantsList.getOrNull(selectedTenantIndex)?.id ?: 0L
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
                    Toast.makeText(requireContext(), "Issue logged successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUpdateStatusDialog(maintenanceId: Long) {
        val statuses = arrayOf("Pending", "In Progress", "Resolved")
        AlertDialog.Builder(requireContext())
            .setTitle("Update Status")
            .setItems(statuses) { _, which ->
                val newStatus = statuses[which]
                val currentList = vm.maintenanceList.value
                val item = currentList.find { it.id == maintenanceId }
                if (item != null && item.status != newStatus) {
                    vm.updateMaintenance(
                        Maintenance(
                            id = item.id,
                            tenantId = item.tenantId,
                            type = item.type,
                            description = item.description,
                            cost = item.cost,
                            status = newStatus,
                            reportedOn = item.reportedOn
                        )
                    )
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
