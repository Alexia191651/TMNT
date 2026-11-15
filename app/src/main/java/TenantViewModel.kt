package com.example.tenantmanagementsystem

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Summary(val count: Int, val expected: Double, val collected: Double) {
    val outstanding get() = (expected - collected).coerceAtLeast(0.0)
}

class TenantViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.get(app)
    private val tenantDao = db.tenantDao()
    private val paymentDao = db.paymentDao()
    private val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val tenants: StateFlow<List<Tenant>> = query
        .flatMapLatest { tenantDao.search(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val summary: Flow<Summary> = combine(
        tenantDao.count(), tenantDao.totalRent(), paymentDao.collectedSince(monthStart())
    ) { c, e, p -> Summary(c, e, p) }

    fun setQuery(q: String) { query.value = q }
    suspend fun getTenant(id: Long) = tenantDao.getById(id)
    fun paymentsFor(id: Long) = paymentDao.forTenant(id)

    fun save(t: Tenant) = viewModelScope.launch {
        if (t.id == 0L) tenantDao.insert(t) else tenantDao.update(t)
    }
    fun delete(t: Tenant) = viewModelScope.launch { tenantDao.delete(t) }   // payments cascade
    fun addPayment(p: Payment) = viewModelScope.launch { paymentDao.insert(p) }
    fun deletePayment(id: Long) = viewModelScope.launch { paymentDao.deleteById(id) }
}