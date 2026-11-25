package com.example.tenantmanagementsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tenantmanagementsystem.Tenant
import com.example.tenantmanagementsystem.database.AppDatabase
import com.example.tenantmanagementsystem.entity.TenantEntity
import com.example.tenantmanagementsystem.entity.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing tenant data and UI state.
 * Uses LiveData to provide reactive updates to the UI.
 */
class TenantViewModel(application: Application) : AndroidViewModel(application) {

    private val _tenantDao = AppDatabase.getInstance(application).tenantDao()

    private val _allTenants = MutableLiveData<List<TenantEntity>>()
    val allTenants: LiveData<List<TenantEntity>> get() = _allTenants

    private val _tenantCount = MutableLiveData(0)
    val tenantCount: LiveData<Int> get() = _tenantCount

    private val _currentTenant = MutableLiveData<TenantEntity?>()
    val currentTenant: LiveData<TenantEntity?> get() = _currentTenant

    init {
        loadAllTenants()
    }

    /**
     * Create a new tenant record.
     */
    suspend fun createTenant(tenant: Tenant): Boolean {
        return withContext(Dispatchers.IO) {
            val tenantEntity = tenant.toEntity()
            val id = _tenantDao.insertTenant(tenantEntity)
            if (id > 0L) {
                updateCurrentTenant(tenantEntity)
                loadAllTenants()
                true
            } else {
                false
            }
        }
    }

    /**
     * Update an existing tenant record.
     */
    suspend fun updateTenant(tenant: Tenant): Boolean {
        return withContext(Dispatchers.IO) {
            val tenantEntity = tenant.toEntity()
            val updated = _tenantDao.updateTenant(tenantEntity)
            if (updated > 0) {
                updateCurrentTenant(tenantEntity)
                loadAllTenants()
                true
            } else {
                false
            }
        }
    }

    /**
     * Delete a tenant by ID.
     */
    suspend fun deleteTenant(id: Long): Boolean {
        return withContext(Dispatchers.IO) {
            val deleted = _tenantDao.deleteTenantById(id)
            if (deleted > 0) {
                updateCurrentTenant(null)
                loadAllTenants()
                true
            } else {
                false
            }
        }
    }

    // Helper methods

    private fun updateCurrentTenant(tenantEntity: TenantEntity?) {
        _currentTenant.postValue(tenantEntity)
    }

    private fun loadAllTenants() {
        viewModelScope.launch(Dispatchers.IO) {
            val tenants = _tenantDao.getAllTenants()
            val count = _tenantDao.getTenantCount()
            _allTenants.postValue(tenants)
            _tenantCount.postValue(count)
        }
    }

    /**
     * Save current tenant to database (for CREATE or UPDATE).
     */
    fun saveCurrentTenant() {
        _currentTenant.value?.let { entity ->
            viewModelScope.launch(Dispatchers.IO) {
                val tenant = Tenant(entity.id, entity.name, entity.phone, entity.rent)
                if (entity.id > 0L) {
                    updateTenant(tenant)
                } else {
                    createTenant(tenant)
                }
            }
        }
    }

    /**
     * Clear all tenant data (for testing purposes).
     */
    suspend fun clearAllTenants() {
        withContext(Dispatchers.IO) {
            val all = _tenantDao.getAllTenants()
            all.forEach { _tenantDao.deleteTenant(it) }
            _allTenants.postValue(emptyList())
            _tenantCount.postValue(0)
            updateCurrentTenant(null)
        }
    }
}
