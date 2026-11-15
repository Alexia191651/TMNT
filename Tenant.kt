package com.example.tenantmanagementsystem

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tenants")
data class Tenant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val unit: String,
    val monthlyRent: Double
) {
    fun summary() = "Tenant: $name\nPhone: $phone\nUnit: $unit\nRent: KSh %,.0f".format(monthlyRent)
}