package com.example.tenantmanagementsystem

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insert(user: User): Long
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1") suspend fun findByEmail(email: String): User?
}

@Dao
interface TenantDao {
    @Insert suspend fun insert(t: Tenant): Long
    @Update suspend fun update(t: Tenant)
    @Delete suspend fun delete(t: Tenant)
    @Query("SELECT * FROM tenants WHERE id = :id") suspend fun getById(id: Long): Tenant?
    @Query("SELECT * FROM tenants WHERE name LIKE '%' || :q || '%' OR unit LIKE '%' || :q || '%' ORDER BY name")
    fun search(q: String): Flow<List<Tenant>>
    @Query("SELECT COUNT(*) FROM tenants") fun count(): Flow<Int>
    @Query("SELECT COALESCE(SUM(monthlyRent), 0.0) FROM tenants") fun totalRent(): Flow<Double>
}

@Dao
interface PaymentDao {
    @Insert suspend fun insert(p: Payment): Long
    @Query("DELETE FROM payments WHERE id = :id") suspend fun deleteById(id: Long)
    @Query("SELECT * FROM payments WHERE tenantId = :tenantId ORDER BY paidOn DESC")
    fun forTenant(tenantId: Long): Flow<List<Payment>>
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM payments WHERE paidOn >= :since")
    fun collectedSince(since: Long): Flow<Double>
}