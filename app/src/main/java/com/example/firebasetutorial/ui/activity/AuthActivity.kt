package com.example.firebasetutorial.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.firebasetutorial.R
import com.example.firebasetutorial.util.Constants.PICK_IMAGE
import com.example.firebasetutorial.util.Constants.SIGN_IN_REQUEST
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {
    var imageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        //Setup Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.auth_bottom)
        val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        //google sign in
        auth = FirebaseAuth.getInstance()
        val googleSignIn = findViewById<TextView>(R.id.google_sign_in)
        googleSignIn.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .requestProfile()
                .build()
            val signInClient = GoogleSignIn.getClient(this, options)
            signInClient.signInIntent.also {
                startActivityForResult(it, SIGN_IN_REQUEST)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data
        }
        if (requestCode == SIGN_IN_REQUEST) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthFirebase(it)
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun googleAuthFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        val keepSignIn = getSharedPreferences("GET_USER", MODE_PRIVATE).edit()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).addOnSuccessListener {
                    keepSignIn.putBoolean("isLoggedIn", true).apply()
                    keepSignIn.putString("userImage", it.user?.photoUrl.toString()).apply()
                    checkLoggedIn()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AuthActivity, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkLoggedIn() {
        if (auth.currentUser != null) {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
            finish()
        }
    }
}