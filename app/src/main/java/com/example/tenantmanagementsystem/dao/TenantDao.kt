package com.example.tenantmanagementsystem.dao

import androidx.room.*
import com.example.tenantmanagementsystem.entity.ElectricityEntity
import com.example.tenantmanagementsystem.entity.TenantEntity
import com.example.tenantmanagementsystem.entity.WaterEntity

/**
 * Data Access Object for Tenant, Water Bill, and Electricity Bill operations using Room.
 * Defines SQL CRUD and query operations for all tenant-related entities.
 */
@Dao
interface TenantDao {

    // ==========================================
    // TENANT OPERATIONS
    // ==========================================

    /**
     * Insert a new tenant into the database.
     * Returns the generated ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTenant(tenant: TenantEntity): Long

    /**
     * Retrieve all tenants from the database, sorted by ID descending (newest first).
     */
    @Query("SELECT * FROM tenants ORDER BY id DESC")
    suspend fun getAllTenants(): List<TenantEntity>

    /**
     * Find a tenant by their ID.
     */
    @Query("SELECT * FROM tenants WHERE id = :id")
    suspend fun getTenantById(id: Long): TenantEntity?

    /**
     * Update an existing tenant record.
     * Returns number of updated rows.
     */
    @Update
    suspend fun updateTenant(tenant: TenantEntity): Int

    /**
     * Delete a tenant record from the database.
     * Returns number of deleted rows.
     */
    @Delete
    suspend fun deleteTenant(tenant: TenantEntity): Int

    /**
     * Delete a tenant record by ID.
     * Returns number of deleted rows.
     */
    @Query("DELETE FROM tenants WHERE id = :id")
    suspend fun deleteTenantById(id: Long): Int

    /**
     * Get total count of tenants.
     */
    @Query("SELECT COUNT(*) FROM tenants")
    suspend fun getTenantCount(): Int


    // ==========================================
    // WATER BILL OPERATIONS
    // ==========================================

    /**
     * Insert a new water bill record into the database.
     * Returns the generated ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterBill(waterBill: WaterEntity): Long

    /**
     * Retrieve all water bills from the database, sorted by ID descending.
     */
    @Query("SELECT * FROM water_bills ORDER BY id DESC")
    suspend fun getAllWaterBills(): List<WaterEntity>

    /**
     * Find a water bill by its ID.
     */
    @Query("SELECT * FROM water_bills WHERE id = :id")
    suspend fun getWaterBillById(id: Long): WaterEntity?

    /**
     * Retrieve all water bills for a specific tenant by tenant ID.
     */
    @Query("SELECT * FROM water_bills WHERE tenantId = :tenantId ORDER BY id DESC")
    suspend fun getWaterBillsForTenant(tenantId: Long): List<WaterEntity>

    /**
     * Retrieve water bills by payment status ("paid", "pending", "overdue").
     */
    @Query("SELECT * FROM water_bills WHERE status = :status ORDER BY id DESC")
    suspend fun getWaterBillsByStatus(status: String): List<WaterEntity>

    /**
     * Update an existing water bill record.
     * Returns number of updated rows.
     */
    @Update
    suspend fun updateWaterBill(waterBill: WaterEntity): Int

    /**
     * Delete a water bill record.
     * Returns number of deleted rows.
     */
    @Delete
    suspend fun deleteWaterBill(waterBill: WaterEntity): Int

    /**
     * Delete a water bill record by ID.
     * Returns number of deleted rows.
     */
    @Query("DELETE FROM water_bills WHERE id = :id")
    suspend fun deleteWaterBillById(id: Long): Int

    /**
     * Get total count of water bills.
     */
    @Query("SELECT COUNT(*) FROM water_bills")
    suspend fun getWaterBillCount(): Int


    // ==========================================
    // ELECTRICITY BILL OPERATIONS
    // ==========================================

    /**
     * Insert a new electricity bill record into the database.
     * Returns the generated ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElectricityBill(electricityBill: ElectricityEntity): Long

    /**
     * Retrieve all electricity bills from the database, sorted by ID descending.
     */
    @Query("SELECT * FROM electricity_bills ORDER BY id DESC")
    suspend fun getAllElectricityBills(): List<ElectricityEntity>

    /**
     * Find an electricity bill by its ID.
     */
    @Query("SELECT * FROM electricity_bills WHERE id = :id")
    suspend fun getElectricityBillById(id: Long): ElectricityEntity?

    /**
     * Retrieve all electricity bills for a specific tenant by tenant ID.
     */
    @Query("SELECT * FROM electricity_bills WHERE tenantId = :tenantId ORDER BY id DESC")
    suspend fun getElectricityBillsForTenant(tenantId: Long): List<ElectricityEntity>

    /**
     * Retrieve electricity bills by payment status ("paid", "pending", "overdue").
     */
    @Query("SELECT * FROM electricity_bills WHERE status = :status ORDER BY id DESC")
    suspend fun getElectricityBillsByStatus(status: String): List<ElectricityEntity>

    /**
     * Update an existing electricity bill record.
     * Returns number of updated rows.
     */
    @Update
    suspend fun updateElectricityBill(electricityBill: ElectricityEntity): Int

    /**
     * Delete an electricity bill record.
     * Returns number of deleted rows.
     */
    @Delete
    suspend fun deleteElectricityBill(electricityBill: ElectricityEntity): Int

    /**
     * Delete an electricity bill record by ID.
     * Returns number of deleted rows.
     */
    @Query("DELETE FROM electricity_bills WHERE id = :id")
    suspend fun deleteElectricityBillById(id: Long): Int

    /**
     * Get total count of electricity bills.
     */
    @Query("SELECT COUNT(*) FROM electricity_bills")
    suspend fun getElectricityBillCount(): Int
}
