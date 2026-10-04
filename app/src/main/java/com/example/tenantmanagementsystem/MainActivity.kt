package com.example.tenantmanagementsystem
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tenantmanagementsystem.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SAVE button logic
        binding.saveButton.setOnClickListener {
            val name = binding.tenantNameEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()
            val rent = binding.rentEditText.text.toString()

            binding.tenantResultTextView.text =
                "Tenant:$name\nPhone:$phone\nRent:Ksh$rent"
            // Pass object to layout via Data Binding
            val tenant = Tenant(name, phone, rent)
            binding.tenant = tenant
        }
    }
}
