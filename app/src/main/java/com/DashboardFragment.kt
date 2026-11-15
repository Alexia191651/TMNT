package com.example.tenantmanagementsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.tenantmanagementsystem.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val vm: TenantViewModel by activityViewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(i, c, false); return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        collect(vm.summary) { s ->
            binding.tenantCountTextView.text = "Tenants: ${s.count}"
            binding.expectedRentTextView.text = "Expected monthly rent: ${money(s.expected)}"
            binding.collectedTextView.text = "Collected this month: ${money(s.collected)}"
            binding.outstandingTextView.text = "Outstanding: ${money(s.outstanding)}"
        }
        binding.addTenantButton.setOnClickListener {
            findNavController().navigate(R.id.addEditTenantFragment)
        }
        binding.viewTenantsButton.setOnClickListener {
            findNavController().navigate(R.id.tenantListFragment)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}