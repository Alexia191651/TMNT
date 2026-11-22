package com.example.tenantmanagementsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tenantmanagementsystem.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val vm: TenantViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView for recent tenants on the Dashboard
        val adapter = RowAdapter(
            onClick = { row ->
                if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
                    findNavController().navigate(
                        R.id.tenantDetailFragment,
                        bundleOf("tenantId" to row.id)
                    )
                }
            },
            onEditClick = { row ->
                if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
                    findNavController().navigate(
                        R.id.addEditTenantFragment,
                        bundleOf("tenantId" to row.id)
                    )
                }
            },
            onDeleteClick = { row ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val tenant = vm.getTenant(row.id)
                    if (tenant != null) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Delete ${tenant.name}?")
                            .setMessage("Are you sure you want to delete this tenant from the database?")
                            .setPositiveButton("Delete") { _, _ ->
                                vm.delete(tenant)
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
            }
        )

        binding.recentTenantsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recentTenantsRecyclerView.adapter = adapter

        // Live Room stats summary & Maintenance Pending count
        collect(vm.summary) { summary ->
            binding.tenantCountTextView.text = "Tenants\n${summary.count}"
            binding.totalRentTextView.text = "Total rent\n${money(summary.expected)}"
            binding.pendingMaintenanceTextView.text = "🛠 Water & Electricity Issues: ${summary.pendingMaintenanceCount} Pending"
        }

        // Live Room tenant list on Dashboard
        collect(vm.tenants) { list ->
            adapter.submitList(list.map { Row(it.id, it.name, "Unit ${it.unit} • ${money(it.monthlyRent)}") })
            binding.emptyDashboardTextView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // Click listener on Water & Electricity pending stats badge to navigate to MaintenanceFragment
        binding.pendingMaintenanceTextView.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
                findNavController().navigate(R.id.maintenanceFragment)
            }
        }

        // Quick Create
        binding.addTenantButton.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
                findNavController().navigate(R.id.addEditTenantFragment)
            }
        }

        // View all
        binding.viewTenantsButton.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.dashboardFragment) {
                findNavController().navigate(R.id.tenantListFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
