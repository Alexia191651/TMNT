package com.example.tenantmanagementsystem.dao

import androidx.room.*
import com.example.tenantmanagementsystem.entity.ElectricityEntity

/**
 * Data Access Object for managing electricity bills.
 */
@Dao
interface ElectricityBillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElectricityBill(electricityBill: ElectricityEntity): Long

    @Update
    suspend fun updateElectricityBill(electricityBill: ElectricityEntity): Int

    @Delete
    suspend fun deleteElectricityBill(electricityBill: ElectricityEntity): Int

    @Query("SELECT * FROM electricity_bills ORDER BY id DESC")
    suspend fun getAllElectricityBills(): List<ElectricityEntity>

    @Query("SELECT * FROM electricity_bills WHERE tenantId = :tenantId ORDER BY billDate DESC")
    suspend fun getElectricityBillsByTenant(tenantId: Long): List<ElectricityEntity>

    @Query("SELECT * FROM electricity_bills WHERE tenantId = :tenantId AND status = :status ORDER BY billDate DESC")
    suspend fun getElectricityBillsByTenantAndStatus(tenantId: Long, status: String): List<ElectricityEntity>

    @Query("SELECT COUNT(*) FROM electricity_bills WHERE tenantId = :tenantId AND status = 'pending'")
    suspend fun getPendingCount(tenantId: Long): Int

    @Query("SELECT SUM(amount) FROM electricity_bills WHERE tenantId = :tenantId AND status = 'pending'")
    suspend fun getTotalPendingAmount(tenantId: Long): Float?

    @Query("SELECT * FROM electricity_bills WHERE tenantId = :tenantId ORDER BY billDate DESC LIMIT 1")
    suspend fun getLatestElectricityBill(tenantId: Long): ElectricityEntity?

    @Query("SELECT * FROM electricity_bills WHERE id = :id")
    suspend fun getElectricityBillById(id: Long): ElectricityEntity?

    @Query("DELETE FROM electricity_bills WHERE tenantId = :tenantId")
    suspend fun deleteByTenant(tenantId: Long): Int

    @Query("SELECT DISTINCT tenantId FROM electricity_bills ORDER BY tenantId")
    suspend fun getAllTenantsWithElectricityBills(): List<Long>

    @Query("UPDATE electricity_bills SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM electricity_bills WHERE tenantId = :tenantId AND status = 'paid' AND amount = :amount)")
    suspend fun isAmountPaid(tenantId: Long, amount: Float): Boolean
}
