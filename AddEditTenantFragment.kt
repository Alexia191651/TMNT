package com.example.tenantmanagementsystem

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tenantmanagementsystem.databinding.FragmentAddEditTenantBinding
import kotlinx.coroutines.launch

class AddEditTenantFragment : Fragment() {
    private var _binding: FragmentAddEditTenantBinding? = null
    private val binding get() = _binding!!
    private val vm: TenantViewModel by activityViewModels()
    private var editingId = 0L

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentAddEditTenantBinding.inflate(i, c, false); return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editingId = arguments?.getLong("tenantId", 0L) ?: 0L
        if (editingId != 0L) {
            viewLifecycleOwner.lifecycleScope.launch {
                vm.getTenant(editingId)?.let {
                    binding.nameEditText.setText(it.name)
                    binding.phoneEditText.setText(it.phone)
                    binding.unitEditText.setText(it.unit)
                    binding.rentEditText.setText(it.monthlyRent.toString())
                }
            }
        }
        binding.saveButton.setOnClickListener { save() }
    }

    private fun save() {
        val name = binding.nameEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()
        val unit = binding.unitEditText.text.toString().trim()
        val rent = binding.rentEditText.text.toString().trim().toDoubleOrNull()

        var ok = true
        if (name.isEmpty()) { binding.nameEditText.error = "Required"; ok = false }
        if (phone.length < 9 || !Patterns.PHONE.matcher(phone).matches()) {
            binding.phoneEditText.error = "Enter a valid phone number"; ok = false
        }
        if (unit.isEmpty()) { binding.unitEditText.error = "Required"; ok = false }
        if (rent == null || rent <= 0) { binding.rentEditText.error = "Enter a rent above 0"; ok = false }
        if (!ok) return

        vm.save(Tenant(id = editingId, name = name, phone = phone, unit = unit, monthlyRent = rent!!))
        Toast.makeText(requireContext(), "Tenant saved", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}