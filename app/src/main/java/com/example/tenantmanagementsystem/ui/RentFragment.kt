package com.example.tenantmanagementsystem.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tenantmanagementsystem.R
import com.example.tenantmanagementsystem.Tenant
import com.example.tenantmanagementsystem.database.AppDatabase
import com.example.tenantmanagementsystem.entity.toEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RentFragment : Fragment(R.layout.fragment_rent) {

    private lateinit var etTenantName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etRent: TextInputEditText
    private lateinit var btnSaveTenant: MaterialButton
    private lateinit var tvTenantList: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTenantName = view.findViewById(R.id.etTenantName)
        etPhone = view.findViewById(R.id.etPhone)
        etRent = view.findViewById(R.id.etRent)
        btnSaveTenant = view.findViewById(R.id.btnSaveTenant)
        tvTenantList = view.findViewById(R.id.tvTenantList)

        loadTenants()

        btnSaveTenant.setOnClickListener {
            val name = etTenantName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val rent = etRent.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || rent.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tenant = Tenant(name = name, phone = phone, rent = rent)
            val db = AppDatabase.getInstance(requireContext())

            lifecycleScope.launch(Dispatchers.IO) {
                db.tenantDao().insertTenant(tenant.toEntity())
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Tenant saved successfully", Toast.LENGTH_SHORT).show()
                    etTenantName.text?.clear()
                    etPhone.text?.clear()
                    etRent.text?.clear()
                    loadTenants()
                }
            }
        }
    }

    private fun loadTenants() {
        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val tenants = db.tenantDao().getAllTenants()
            val sb = StringBuilder()
            if (tenants.isEmpty()) {
                sb.append("No tenants registered yet.")
            } else {
                for (t in tenants) {
                    sb.append("ID: ${t.id}\n")
                    sb.append("Name: ${t.name}\n")
                    sb.append("Phone: ${t.phone}\n")
                    sb.append("Rent: KSh ${t.rent}\n")
                    sb.append("------------------------\n")
                }
            }
            withContext(Dispatchers.Main) {
                tvTenantList.text = sb.toString()
            }
        }
    }
}
