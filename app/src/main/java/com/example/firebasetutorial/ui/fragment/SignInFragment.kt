package com.example.firebasetutorial.ui.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.firebasetutorial.databinding.FragmentSignInBinding
import com.example.firebasetutorial.ui.activity.AuthActivity
import com.example.firebasetutorial.ui.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding.signInBtn.setOnClickListener {
            signIn()

        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun signIn() {
        val email = binding.edtSignInEmail.text.toString().trim()
        val password = binding.edtSignInPassword.text.toString().trim()
        val progressBar = ProgressDialog(requireContext())

        val keepSignIn = requireActivity().getSharedPreferences("GET_USER", AppCompatActivity.MODE_PRIVATE).edit()
        if (email.isNotEmpty() and password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withContext(Dispatchers.Main) {
                        progressBar.setTitle("Logging...")
                        progressBar.setCancelable(false)
                        progressBar.show()
                    }
                    auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                        progressBar.dismiss()
                        keepSignIn.putBoolean("isLoggedIn", true).apply()
                        keepSignIn.putString("userPhoto",it.user?.photoUrl.toString()).apply()
                        checkLoggedIn()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showToast(e.toString())
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkLoggedIn() {
        if (auth.currentUser != null) {
            (activity as AuthActivity).startActivity(
                Intent(
                    requireActivity(),
                    MainActivity::class.java
                )
            )
            (activity as AuthActivity).finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}