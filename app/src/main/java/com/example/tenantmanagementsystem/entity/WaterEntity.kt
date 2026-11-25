package com.example.tenantmanagementsystem.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a water bill record in the database.
 * This is the Room entity mapping that will be used by DAO operations.
 */
@Entity(
    tableName = "water_bills",
    foreignKeys = [
        ForeignKey(
            entity = TenantEntity::class,
            parentColumns = ["id"],
            childColumns = ["tenantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tenantId"])]
)
data class WaterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tenantId: Long,
    val billDate: String, // YYYY-MM-DD format
    val dueDate: String, // YYYY-MM-DD format
    val amount: Float,
    val status: String = "pending" // "paid", "pending", "overdue"
) {
    fun toWaterBill(): WaterBill {
        return WaterBill(
            id = id,
            tenantId = tenantId,
            billDate = billDate,
            dueDate = dueDate,
            amount = amount,
            status = status
        )
    }

    companion object {
        fun fromWaterBill(waterBill: WaterBill): WaterEntity {
            return WaterEntity(
                id = waterBill.id ?: 0L,
                tenantId = waterBill.tenantId,
                billDate = waterBill.billDate,
                dueDate = waterBill.dueDate,
                amount = waterBill.amount,
                status = when (waterBill.status) {
                    "paid" -> "paid"
                    "pending" -> "pending"
                    "overdue" -> "overdue"
                    else -> "pending"
                }
            )
        }
    }
}
