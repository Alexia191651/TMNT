package com.example.tenantmanagementsystem

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Summary(
    val count: Int,
    val expected: Double,
    val collected: Double,
    val pendingMaintenanceCount: Int = 0
) {
    val outstanding get() = (expected - collected).coerceAtLeast(0.0)
}

class TenantViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.get(app)
    private val tenantDao = db.tenantDao()
    private val paymentDao = db.paymentDao()
    private val maintenanceDao = db.maintenanceDao()
    val query = MutableStateFlow("")

    private val email get() = SessionManager.currentUserEmail

    @OptIn(ExperimentalCoroutinesApi::class)
    val tenants: StateFlow<List<Tenant>> = query
        .flatMapLatest { tenantDao.search(email, it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _maintenanceType = MutableStateFlow("All")

    @OptIn(ExperimentalCoroutinesApi::class)
    val maintenanceList: StateFlow<List<MaintenanceWithTenant>> = _maintenanceType
        .flatMapLatest { maintenanceDao.observeByType(email, it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: Flow<Summary> = combine(
        tenantDao.count(email),
        tenantDao.totalRent(email),
        paymentDao.collectedSince(email, monthStart()),
        maintenanceDao.pendingCount(email)
    ) { c, e, p, m -> Summary(c, e, p, m) }

    fun setQuery(q: String) { query.value = q }
    suspend fun getTenant(id: Long) = tenantDao.getById(id)
    fun observeTenant(id: Long): Flow<Tenant?> = tenantDao.observeById(id)
    fun paymentsFor(id: Long) = paymentDao.forTenant(id)

    fun save(t: Tenant) = viewModelScope.launch {
        val tenantToSave = t.copy(ownerEmail = email)
        if (tenantToSave.id == 0L) tenantDao.insert(tenantToSave) else tenantDao.update(tenantToSave)
    }
    fun delete(t: Tenant) = viewModelScope.launch { tenantDao.delete(t) } // payments cascade
    fun addPayment(p: Payment) = viewModelScope.launch { paymentDao.insert(p) }
    fun deletePayment(id: Long) = viewModelScope.launch { paymentDao.deleteById(id) }

    // Maintenance methods
    fun setMaintenanceType(type: String) { _maintenanceType.value = type }
    fun maintenancesFor(id: Long): Flow<List<Maintenance>> = maintenanceDao.forTenant(id)
    fun addMaintenance(m: Maintenance) = viewModelScope.launch { maintenanceDao.insert(m) }
    fun updateMaintenance(m: Maintenance) = viewModelScope.launch { maintenanceDao.update(m) }
    fun deleteMaintenance(id: Long) = viewModelScope.launch { maintenanceDao.deleteById(id) }
}
