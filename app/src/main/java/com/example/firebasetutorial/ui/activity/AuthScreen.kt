package com.example.firebasetutorial.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.firebasetutorial.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AuthScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_screen)
        //Setup Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.auth_bottom)
        val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }
}