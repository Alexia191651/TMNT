package com.example.tenantmanagementsystem.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tenantmanagementsystem.dao.ElectricityBillDao
import com.example.tenantmanagementsystem.dao.TenantDao
import com.example.tenantmanagementsystem.dao.WaterBillDao
import com.example.tenantmanagementsystem.entity.ElectricityEntity
import com.example.tenantmanagementsystem.entity.TenantEntity
import com.example.tenantmanagementsystem.entity.WaterEntity

/**
 * Room Database class that defines the database schema.
 * Registered entities include TenantEntity, WaterEntity, and ElectricityEntity.
 * This is the entry point for all database operations in the app.
 */
@Database(
    entities = [
        TenantEntity::class,
        WaterEntity::class,
        ElectricityEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tenantDao(): TenantDao
    abstract fun waterBillDao(): WaterBillDao
    abstract fun electricityBillDao(): ElectricityBillDao

    companion object {
        // Singleton instance for the database
        @Volatile
        private var appDatabase: AppDatabase? = null

        /**
         * Get the singleton instance of the database.
         */
        fun getInstance(context: Context): AppDatabase {
            return appDatabase ?: synchronized(this) {
                appDatabase ?: buildDatabase(context).also {
                    appDatabase = it
                }
            }
        }

        /**
         * Build the database instance using Room.
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "tenant_management_database" // Database name
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}
