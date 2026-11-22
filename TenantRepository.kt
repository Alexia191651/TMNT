package com.example.tenantmanagementsystem

import kotlinx.coroutines.flow.Flow

class TenantRepository(private val tenantDao: TenantDao) {

    val allTenants: Flow<List<Tenant>> = tenantDao.observeAllTenants(SessionManager.currentUserEmail)
    val totalRent: Flow<Double> = tenantDao.totalRent(SessionManager.currentUserEmail)

    suspend fun addTenant(tenant: Tenant): Long = tenantDao.insert(tenant.copy(ownerEmail = SessionManager.currentUserEmail))
    suspend fun getTenantById(id: Long): Tenant? = tenantDao.getById(id)
    suspend fun updateTenant(tenant: Tenant) = tenantDao.update(tenant)
    suspend fun deleteTenant(tenant: Tenant) = tenantDao.delete(tenant)

    companion object {
        @Volatile
        private var INSTANCE: TenantRepository? = null

        fun getInstance(database: AppDatabase): TenantRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TenantRepository(database.tenantDao()).also { INSTANCE = it }
            }
        }
    }
}
