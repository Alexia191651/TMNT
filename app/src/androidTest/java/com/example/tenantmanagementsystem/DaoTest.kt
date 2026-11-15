package com.example.tenantmanagementsystem

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TenantDaoTest {
    private lateinit var db: AppDatabase

    @Before fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
    }
    @After fun tearDown() = db.close()

    @Test fun insertAndRead() = runBlocking {
        val id = db.tenantDao().insert(Tenant(name = "Jane", phone = "0712345678", unit = "A1", monthlyRent = 15000.0))
        assertEquals("Jane", db.tenantDao().getById(id)?.name)
    }

    @Test fun deletingTenantRemovesPayments() = runBlocking {
        val t = Tenant(name = "Jo", phone = "0700000000", unit = "B2", monthlyRent = 9000.0)
        val id = db.tenantDao().insert(t)
        db.paymentDao().insert(Payment(tenantId = id, amount = 5000.0, paidOn = 1L))
        db.tenantDao().delete(t.copy(id = id))
        assertTrue(db.paymentDao().forTenant(id).first().isEmpty())
    }
}