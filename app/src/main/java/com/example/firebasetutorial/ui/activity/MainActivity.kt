package com.example.firebasetutorial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetutorial.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val userLoggedInState =
            getSharedPreferences("GET_USER", MODE_PRIVATE).getBoolean("isLoggedIn", false)

        if (!userLoggedInState) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

    }
}