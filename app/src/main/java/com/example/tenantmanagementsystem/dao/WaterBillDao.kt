package com.example.tenantmanagementsystem.dao

import androidx.room.*
import com.example.tenantmanagementsystem.entity.WaterEntity

/**
 * Data Access Object for managing water bills.
 */
@Dao
interface WaterBillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterBill(waterBill: WaterEntity): Long

    @Update
    suspend fun updateWaterBill(waterBill: WaterEntity): Int

    @Delete
    suspend fun deleteWaterBill(waterBill: WaterEntity): Int

    @Query("SELECT * FROM water_bills ORDER BY id DESC")
    suspend fun getAllWaterBills(): List<WaterEntity>

    @Query("SELECT * FROM water_bills WHERE tenantId = :tenantId ORDER BY billDate DESC")
    suspend fun getWaterBillsByTenant(tenantId: Long): List<WaterEntity>

    @Query("SELECT * FROM water_bills WHERE tenantId = :tenantId AND status = :status ORDER BY billDate DESC")
    suspend fun getWaterBillsByTenantAndStatus(tenantId: Long, status: String): List<WaterEntity>

    @Query("SELECT COUNT(*) FROM water_bills WHERE tenantId = :tenantId AND status = 'pending'")
    suspend fun getPendingCount(tenantId: Long): Int

    @Query("SELECT SUM(amount) FROM water_bills WHERE tenantId = :tenantId AND status = 'pending'")
    suspend fun getTotalPendingAmount(tenantId: Long): Float?

    @Query("SELECT * FROM water_bills WHERE tenantId = :tenantId ORDER BY billDate DESC LIMIT 1")
    suspend fun getLatestWaterBill(tenantId: Long): WaterEntity?

    @Query("SELECT * FROM water_bills WHERE id = :id")
    suspend fun getWaterBillById(id: Long): WaterEntity?

    @Query("DELETE FROM water_bills WHERE tenantId = :tenantId")
    suspend fun deleteByTenant(tenantId: Long): Int

    @Query("SELECT DISTINCT tenantId FROM water_bills ORDER BY tenantId")
    suspend fun getAllTenantsWithWaterBills(): List<Long>

    @Query("UPDATE water_bills SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM water_bills WHERE tenantId = :tenantId AND status = 'paid' AND amount = :amount)")
    suspend fun isAmountPaid(tenantId: Long, amount: Float): Boolean
}
