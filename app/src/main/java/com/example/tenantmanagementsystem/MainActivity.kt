package com.example.tenantmanagementsystem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tenantmanagementsystem.databinding.ActivityMainBinding
import com.example.tenantmanagementsystem.ui.ElectricityFragment
import com.example.tenantmanagementsystem.ui.HomeFragment
import com.example.tenantmanagementsystem.ui.RentFragment
import com.example.tenantmanagementsystem.ui.TenantSummaryFragment
import com.example.tenantmanagementsystem.ui.WaterFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_rent -> {
                    loadFragment(RentFragment())
                    true
                }
                R.id.nav_water -> {
                    loadFragment(WaterFragment())
                    true
                }
                R.id.nav_electricity -> {
                    loadFragment(ElectricityFragment())
                    true
                }
                R.id.nav_summary -> {
                    loadFragment(TenantSummaryFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
