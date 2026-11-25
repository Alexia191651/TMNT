package com.example.tenantmanagementsystem.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tenantmanagementsystem.R
import com.example.tenantmanagementsystem.database.AppDatabase
import com.example.tenantmanagementsystem.entity.WaterEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WaterFragment : Fragment(R.layout.fragment_water) {

    private lateinit var etTenantId: TextInputEditText
    private lateinit var etBillDate: TextInputEditText
    private lateinit var etDueDate: TextInputEditText
    private lateinit var etAmount: TextInputEditText
    private lateinit var btnSaveWaterBill: MaterialButton
    private lateinit var tvWaterBillList: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTenantId = view.findViewById(R.id.etTenantId)
        etBillDate = view.findViewById(R.id.etBillDate)
        etDueDate = view.findViewById(R.id.etDueDate)
        etAmount = view.findViewById(R.id.etAmount)
        btnSaveWaterBill = view.findViewById(R.id.btnSaveWaterBill)
        tvWaterBillList = view.findViewById(R.id.tvWaterBillList)

        loadWaterBills()

        btnSaveWaterBill.setOnClickListener {
            val tenantIdStr = etTenantId.text.toString().trim()
            val billDate = etBillDate.text.toString().trim()
            val dueDate = etDueDate.text.toString().trim()
            val amountStr = etAmount.text.toString().trim()

            if (tenantIdStr.isEmpty() || billDate.isEmpty() || dueDate.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tenantId = tenantIdStr.toLongOrNull() ?: 0L
            val amount = amountStr.toFloatOrNull() ?: 0f

            val waterEntity = WaterEntity(
                tenantId = tenantId,
                billDate = billDate,
                dueDate = dueDate,
                amount = amount,
                status = "pending"
            )

            val db = AppDatabase.getInstance(requireContext())
            lifecycleScope.launch(Dispatchers.IO) {
                db.waterBillDao().insertWaterBill(waterEntity)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Water bill saved successfully", Toast.LENGTH_SHORT).show()
                    etTenantId.text?.clear()
                    etBillDate.text?.clear()
                    etDueDate.text?.clear()
                    etAmount.text?.clear()
                    loadWaterBills()
                }
            }
        }
    }

    private fun loadWaterBills() {
        val db = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val bills = db.waterBillDao().getAllWaterBills()
            val sb = StringBuilder()
            if (bills.isEmpty()) {
                sb.append("No water bills recorded yet.")
            } else {
                for (b in bills) {
                    sb.append("Bill ID: ${b.id} | Tenant ID: ${b.tenantId}\n")
                    sb.append("Date: ${b.billDate} (Due: ${b.dueDate})\n")
                    sb.append("Amount: KSh ${b.amount} | Status: ${b.status}\n")
                    sb.append("------------------------\n")
                }
            }
            withContext(Dispatchers.Main) {
                tvWaterBillList.text = sb.toString()
            }
        }
    }
}
