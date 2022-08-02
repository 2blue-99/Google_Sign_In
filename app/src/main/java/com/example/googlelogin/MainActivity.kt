package com.example.googlelogin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.googlelogin.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 프레그먼트 생성 add
        val transaction = supportFragmentManager.beginTransaction()
            .add(R.id.myFragment, FragmentFirst())
        transaction.commit()
    }

    // 앱이 다시 시작되면 이쪽으로 들어옴
    override fun onStart() {
        super.onStart()
        GoogleSignIn.getLastSignedInAccount(this)?.let {
            val transaction = supportFragmentManager.beginTransaction()
                .replace(R.id.myFragment, FragmentSecond())
            transaction.commit()
        }
    }
}