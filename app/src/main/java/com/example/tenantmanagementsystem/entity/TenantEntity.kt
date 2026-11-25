package com.example.tenantmanagementsystem.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tenantmanagementsystem.Tenant

/**
 * Entity representing a tenant record in the database.
 * This is the Room entity mapping that will be used by DAO operations.
 */
@Entity(tableName = "tenants")
data class TenantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val phone: String,
    val rent: String
) {
    companion object {
        fun fromEntity(entity: TenantEntity): Tenant {
            return Tenant(
                id = entity.id,
                name = entity.name,
                phone = entity.phone,
                rent = entity.rent
            )
        }
    }
}

fun Tenant.toEntity(): TenantEntity {
    return TenantEntity(
        id = this.id,
        name = this.name,
        phone = this.phone,
        rent = this.rent
    )
}

// Type converter for Room to properly handle String <-> Int conversion
object TypeConverter {
    fun convert(value: Int): String {
        return value.toString()
    }

    fun convert(value: String): Int {
        return value.toIntOrNull() ?: 0
    }
}
