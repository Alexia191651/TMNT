package com.example.tenantmanagementsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tenantmanagementsystem.databinding.FragmentTenantListBinding

class TenantListFragment : Fragment() {
    private var _binding: FragmentTenantListBinding? = null
    private val binding get() = _binding!!
    private val vm: TenantViewModel by activityViewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentTenantListBinding.inflate(i, c, false); return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = RowAdapter(onClick = { row ->
            findNavController().navigate(R.id.tenantDetailFragment, bundleOf("tenantId" to row.id))
        })
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        vm.setQuery("")
        binding.searchEditText.doOnTextChanged { t, _, _, _ -> vm.setQuery(t.toString().trim()) }
        binding.addFab.setOnClickListener { findNavController().navigate(R.id.addEditTenantFragment) }

        collect(vm.tenants) { list ->
            adapter.submitList(list.map { Row(it.id, it.name, "Unit ${it.unit} • ${money(it.monthlyRent)}") })
            binding.emptyTextView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}