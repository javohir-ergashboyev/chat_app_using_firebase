package com.example.firebasetutorial.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.firebasetutorial.R
import com.example.firebasetutorial.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        //check user logged in
        val userLoggedInState =
            getSharedPreferences("GET_USER", MODE_PRIVATE).getBoolean("isLoggedIn", false)
        if (!userLoggedInState) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
        //setup profile image
        val userImage = getSharedPreferences("GET_USER", MODE_PRIVATE).getString(
            "userImage",
            "android.resource://$packageName/${R.drawable.ic_person}"
        )
        Glide.with(this).load(userImage?.toUri()).into(binding.profileImage)


        auth = FirebaseAuth.getInstance()
        binding.btnSignOut.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Warning!")
                .setMessage("Do you really want to sign out?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes") { _, _ ->
                    signOut()
                }
                .create().show()
        }

    }

    private fun signOut() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signOut()
                getSharedPreferences("GET_USER", MODE_PRIVATE).edit().clear().apply()
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                finish()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}