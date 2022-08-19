package com.udacity.project4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.databinding.FragmentLoginBinding

class Login : Fragment() {

    val binding = FragmentLoginBinding.inflate(layoutInflater)
    val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val email = binding.emailText.text
        val password = binding.passwordText.text
        binding.loginButton.setOnClickListener {
            auth.createUserWithEmailAndPassword(email.toString(), password.toString())
        }
        return inflater.inflate(R.layout.fragment_login, container, false)

    }

}