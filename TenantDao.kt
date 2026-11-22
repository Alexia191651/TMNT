package com.example.tenantmanagementsystem

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): User?
}

@Dao
interface TenantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(t: Tenant): Long

    @Update
    suspend fun update(t: Tenant)

    @Delete
    suspend fun delete(t: Tenant)

    @Query("SELECT * FROM tenants WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Tenant?

    @Query("SELECT * FROM tenants WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<Tenant?>

    @Query("SELECT * FROM tenants WHERE ownerEmail = :email AND (name LIKE '%' || :q || '%' OR unit LIKE '%' || :q || '%') ORDER BY name")
    fun search(email: String, q: String): Flow<List<Tenant>>

    @Query("SELECT * FROM tenants WHERE ownerEmail = :email ORDER BY name ASC")
    fun observeAllTenants(email: String): Flow<List<Tenant>>

    @Query("SELECT COUNT(*) FROM tenants WHERE ownerEmail = :email")
    fun count(email: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(monthlyRent), 0.0) FROM tenants WHERE ownerEmail = :email")
    fun totalRent(email: String): Flow<Double>
}

@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(p: Payment): Long

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM payments WHERE tenantId = :tenantId ORDER BY paidOn DESC")
    fun forTenant(tenantId: Long): Flow<List<Payment>>

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0.0) FROM payments p 
        INNER JOIN tenants t ON p.tenantId = t.id 
        WHERE t.ownerEmail = :email AND p.paidOn >= :since
    """)
    fun collectedSince(email: String, since: Long): Flow<Double>
}
