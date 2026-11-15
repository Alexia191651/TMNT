package com.example.tenantmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tenantmanagementsystem.databinding.ActivityRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userDao = AppDatabase.get(this).userDao()

        binding.registerButton.setOnClickListener {
            val name = binding.fullNameEditText.text.toString().trim()
            val email = binding.registerEmailEditText.text.toString().trim().lowercase()
            val password = binding.registerPasswordEditText.text.toString()

            if (name.isEmpty()) { binding.fullNameEditText.error = "Required"; return@setOnClickListener }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.registerEmailEditText.error = "Enter a valid email"; return@setOnClickListener
            }
            if (password.length < 6) {
                binding.registerPasswordEditText.error = "At least 6 characters"; return@setOnClickListener
            }

            lifecycleScope.launch {
                if (userDao.findByEmail(email) != null) {
                    binding.registerEmailEditText.error = "Email already registered"
                    return@launch
                }
                val salt = PasswordHasher.newSalt()
                val hash = withContext(Dispatchers.Default) { PasswordHasher.hash(password, salt) }
                userDao.insert(User(fullName = name, email = email, passwordHash = hash, salt = salt))
                Toast.makeText(this@RegisterActivity, "Account created. Please log in.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                    putExtra("EMAIL", email); flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                })
                finish()
            }
        }
        binding.loginLinkTextView.setOnClickListener { finish() }
    }
}