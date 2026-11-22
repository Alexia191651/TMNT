package com.example.tenantmanagementsystem

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "maintenances",
    foreignKeys = [
        ForeignKey(
            entity = Tenant::class,
            parentColumns = ["id"],
            childColumns = ["tenantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tenantId")]
)
data class Maintenance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tenantId: Long,
    val type: String,               // "Water", "Electricity", "General"
    val description: String,
    val cost: Double = 0.0,
    val status: String = "Pending",  // "Pending", "In Progress", "Resolved"
    val reportedOn: Long = System.currentTimeMillis()
) : Serializable
