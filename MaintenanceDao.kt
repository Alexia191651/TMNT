package com.example.tenantmanagementsystem

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class MaintenanceWithTenant(
    val id: Long,
    val tenantId: Long,
    val tenantName: String,
    val unit: String,
    val type: String,
    val description: String,
    val cost: Double,
    val status: String,
    val reportedOn: Long
)

@Dao
interface MaintenanceDao {
    @Query(
        """
        SELECT m.id, m.tenantId, t.name AS tenantName, t.unit, m.type,
               m.description, m.cost, m.status, m.reportedOn
        FROM maintenances m
        INNER JOIN tenants t ON m.tenantId = t.id
        WHERE t.ownerEmail = :email AND (:type = 'All' OR m.type = :type)
        ORDER BY m.reportedOn DESC
        """
    )
    fun observeByType(email: String, type: String): Flow<List<MaintenanceWithTenant>>

    @Query("""
        SELECT COUNT(*) FROM maintenances m 
        INNER JOIN tenants t ON m.tenantId = t.id 
        WHERE t.ownerEmail = :email AND m.status = 'Pending'
    """)
    fun pendingCount(email: String): Flow<Int>

    @Query("SELECT * FROM maintenances WHERE tenantId = :tenantId ORDER BY reportedOn DESC")
    fun forTenant(tenantId: Long): Flow<List<Maintenance>>

    @Insert
    suspend fun insert(maintenance: Maintenance)

    @Update
    suspend fun update(maintenance: Maintenance)

    @Query("DELETE FROM maintenances WHERE id = :id")
    suspend fun deleteById(id: Long)
}
