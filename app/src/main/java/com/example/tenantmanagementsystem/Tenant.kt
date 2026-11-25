package com.example.tenantmanagementsystem

data class Tenant(
    val id: Long = 0L,
    val name: String,
    val phone: String,
    val rent: String
) {
    constructor(name: String, phone: String, rent: String) : this(0L, name, phone, rent)

    fun summary(): String {
        return "Tenant: $name\nPhone: $phone\nRent: KSh $rent"
    }
}
