package com.example.tenantmanagementsystem.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tenantmanagementsystem.R
import com.example.tenantmanagementsystem.database.AppDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var tvTotalTenants: TextView
    private lateinit var tvTotalWaterBills: TextView
    private lateinit var tvTotalElectricityBills: TextView
    private lateinit var btnGoToRent: MaterialButton
    private lateinit var btnGoToWater: MaterialButton
    private lateinit var btnGoToElectricity: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTotalTenants = view.findViewById(R.id.tvTotalTenants)
        tvTotalWaterBills = view.findViewById(R.id.tvTotalWaterBills)
        tvTotalElectricityBills = view.findViewById(R.id.tvTotalElectricityBills)
        btnGoToRent = view.findViewById(R.id.btnGoToRent)
        btnGoToWater = view.findViewById(R.id.btnGoToWater)
        btnGoToElectricity = view.findViewById(R.id.btnGoToElectricity)

        loadDashboardStats()

        btnGoToRent.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RentFragment())
                .commit()
        }

        btnGoToWater.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, WaterFragment())
                .commit()
        }

        btnGoToElectricity.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ElectricityFragment())
                .commit()
        }
    }

    private fun loadDashboardStats() {
        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val tenantCount = db.tenantDao().getTenantCount()
            val waterCount = db.waterBillDao().getAllWaterBills().size
            val electricityCount = db.electricityBillDao().getAllElectricityBills().size

            withContext(Dispatchers.Main) {
                tvTotalTenants.text = tenantCount.toString()
                tvTotalWaterBills.text = waterCount.toString()
                tvTotalElectricityBills.text = electricityCount.toString()
            }
        }
    }
}
