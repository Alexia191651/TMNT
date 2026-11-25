package com.example.tenantmanagementsystem.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing an electricity bill record in the database.
 * This is the Room entity mapping that will be used by DAO operations.
 */
@Entity(
    tableName = "electricity_bills",
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
data class ElectricityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tenantId: Long,
    val billDate: String, // YYYY-MM-DD format
    val dueDate: String, // YYYY-MM-DD format
    val amount: Float,
    val status: String = "pending" // "paid", "pending", "overdue"
) {
    fun toElectricityBill(): ElectricityBill {
        return ElectricityBill(
            id = id,
            tenantId = tenantId,
            billDate = billDate,
            dueDate = dueDate,
            amount = amount,
            status = status
        )
    }

    companion object {
        fun fromElectricityBill(electricityBill: ElectricityBill): ElectricityEntity {
            return ElectricityEntity(
                id = electricityBill.id ?: 0L,
                tenantId = electricityBill.tenantId,
                billDate = electricityBill.billDate,
                dueDate = electricityBill.dueDate,
                amount = electricityBill.amount,
                status = when (electricityBill.status) {
                    "paid" -> "paid"
                    "pending" -> "pending"
                    "overdue" -> "overdue"
                    else -> "pending"
                }
            )
        }
    }
}
