package com.example.tenantmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tenantmanagementsystem.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val fullName = binding.fullNameEditText.text.toString().trim()
            val email = binding.registerEmailEditText.text.toString().trim()
            val password = binding.registerPasswordEditText.text.toString()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Account created. Please log in.", Toast.LENGTH_SHORT).show()

            // 1. Create intent targeted at LoginActivity
            val intent = Intent(this, LoginActivity::class.java)

            // 2. Attach the user's email with key "EMAIL"
            intent.putExtra("EMAIL", email)

            // 3. Clear existing activity stack so duplicate screens aren't created
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            // 4. Start LoginActivity
            startActivity(intent)
        }

        binding.loginLinkTextView.setOnClickListener {
            // Closes RegisterActivity to expose the existing LoginActivity underneath
            finish()
        }
    }
}