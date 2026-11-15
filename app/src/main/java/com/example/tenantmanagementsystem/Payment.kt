package com.example.tenantmanagementsystem

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [ForeignKey(Tenant::class, ["id"], ["tenantId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("tenantId")]
)
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tenantId: Long,
    val amount: Double,
    val paidOn: Long,          // epoch millis
    val note: String = ""
)