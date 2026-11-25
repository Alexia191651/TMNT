package com.example.tenantmanagementsystem.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tenantmanagementsystem.R
import com.example.tenantmanagementsystem.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TenantSummaryFragment : Fragment(R.layout.fragment_tenant_summary) {

    private lateinit var tvTenantSummaryRows: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTenantSummaryRows = view.findViewById(R.id.tvTenantSummaryRows)
        loadTenantSummary()
    }

    private fun loadTenantSummary() {
        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val tenants = db.tenantDao().getAllTenants()
            val sb = StringBuilder()

            if (tenants.isEmpty()) {
                sb.append("No tenant information available.")
            } else {
                for ((index, tenant) in tenants.withIndex()) {
                    val waterBills = db.waterBillDao().getWaterBillsByTenant(tenant.id)
                    val electricityBills = db.electricityBillDao().getElectricityBillsByTenant(tenant.id)

                    val totalWater = waterBills.sumOf { it.amount.toDouble() }
                    val totalElectricity = electricityBills.sumOf { it.amount.toDouble() }

                    sb.append("Tenant #${index + 1}\n")
                    sb.append("• Name: ${tenant.name}\n")
                    sb.append("• ID: ${tenant.id}\n")
                    sb.append("• Rent: KSh ${tenant.rent}\n")
                    sb.append("• Water Bill: KSh $totalWater (${waterBills.size} bill(s))\n")
                    sb.append("• Electricity Bill: KSh $totalElectricity (${electricityBills.size} bill(s))\n")
                    sb.append("===============================\n\n")
                }
            }

            withContext(Dispatchers.Main) {
                tvTenantSummaryRows.text = sb.toString()
            }
        }
    }
}
