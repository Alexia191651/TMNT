package com.example.tenantmanagementsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tenantmanagementsystem.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val session = SessionManager(this)
        if (session.isLoggedIn()) { openMain(); return }     // already signed in

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra("EMAIL")?.let { binding.emailEditText.setText(it) }
        val userDao = AppDatabase.get(this).userDao()

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim().lowercase()
            val password = binding.passwordEditText.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val user = userDao.findByEmail(email)
                val ok = user != null && withContext(Dispatchers.Default) {
                    PasswordHasher.hash(password, user.salt) == user.passwordHash
                }
                if (ok && user != null) { session.login(user.email, user.fullName); openMain() }
                else Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
        binding.registerTextView.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        binding.helpTextView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.strathmore.edu")))
        }
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}