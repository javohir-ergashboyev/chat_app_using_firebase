package com.example.firebasetutorial.ui.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.firebasetutorial.databinding.FragmentSignUpBinding
import com.example.firebasetutorial.ui.activity.AuthActivity
import com.example.firebasetutorial.ui.activity.MainActivity
import com.example.firebasetutorial.util.Constants.PICK_IMAGE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private var imgUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding.signUpBtn.setOnClickListener {
            registerUser()
        }
        binding.signUpImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            requireActivity().startActivityForResult(
                Intent(gallery), PICK_IMAGE
            )
        }

    }


    private fun registerUser() {
        val email = binding.edtSignUpEmail.text.toString().trim()
        val password = binding.edtSignUpPassword.text.toString().trim()
        val username = binding.edtSignUpNick.text.toString().trim()
        val progressBar = ProgressDialog(requireContext())
        val keepSignIn =
            requireActivity().getSharedPreferences("GET_USER", AppCompatActivity.MODE_PRIVATE)
                .edit()


        if (email.isNotEmpty() and password.isNotEmpty() and username.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    withContext(Dispatchers.Main) {
                        progressBar.setTitle("Logging...")
                        progressBar.setCancelable(false)
                        progressBar.show()
                    }
                    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                        it.user?.let { user ->
                            val profileUpdates = UserProfileChangeRequest.Builder().apply {
                                displayName = username
                                photoUri = imgUri
                            }.build()
                            user.updateProfile(profileUpdates).addOnSuccessListener {
                                progressBar.dismiss()
                                keepSignIn.putBoolean("isLoggedIn", true).apply()
                                keepSignIn.putString("userImage",user.photoUrl.toString()).apply()
                                checkLoggedIn()
                            }
                        }

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
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    MainActivity::class.java
                )
            )
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        imgUri = (activity as AuthActivity).imageUri
        Log.d("IMAGE", imgUri.toString())
        imgUri?.let {
            Glide.with(requireContext()).load(it).into(binding.signUpImage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}