package com.example.tenantmanagementsystem.entity

/**
 * Data class representing a water bill for a tenant.
 */
data class WaterBill(
    val id: Long? = null,
    val tenantId: Long,
    val billDate: String, // YYYY-MM-DD format
    val dueDate: String, // YYYY-MM-DD format
    val amount: Float,
    val status: String = "pending" // "paid", "pending", "overdue"
) {
    companion object {
        fun create(tenantId: Long, billDate: String, dueDate: String, amount: Float, status: String = "pending"): WaterBill {
            return WaterBill(
                tenantId = tenantId,
                billDate = billDate,
                dueDate = dueDate,
                amount = amount,
                status = status
            )
        }
    }
}
