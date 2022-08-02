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
//    var auth : FirebaseAuth? = null
    private lateinit var auth:FirebaseAuth

    private var googleSignInClient : GoogleSignInClient? = null
    private var GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //auth 객체 초기화
        auth = Firebase.auth

        //GoogleSignInClient 객체 초기화
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //기본 로그인 방식 사용
            .requestIdToken(getString(R.string.default_web_client_id))
            //requestIdToken :필수사항이다. 사용자의 식별값(token)을 사용하겠다.
            //(App이 구글에게 요청)
            .requestEmail()
            // 사용자의 이메일을 사용하겠다.(App이 구글에게 요청)
            .build()


        // 내 앱에서 구글의 계정을 가져다 쓸거니 알고 있어라!
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        binding.singInBtn.setOnClickListener { signIn() }



    }

    private fun signIn(){
        val signInIntent = googleSignInClient?.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        println("result.resultCode : ${result.resultCode}")
        if (result.resultCode == 0) {
            val data: Intent? = result.data
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
            println("@@@ data : $data, task : $task")
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account?.email.toString()
            val familyName = account?.familyName.toString()
            println("account : $account, email : $email, familyName : $familyName ")


        } catch (e: ApiException){
            Log.w("failed", "signInResult:failed code=" + e.statusCode)
        }
    }














    fun googleLogin(){
        println("@@@ googleLogin")
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,1004)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println("@@@ onActivityResult")
        println("@@@ requestCode : $requestCode, resultCode : $resultCode, data:$data")
        //Activity.Result_OK : 정상완료
        //Activity.Result_CANCEL : 중간에 취소 되었음(실패)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1004) {

            //결과 Intent(data 매개변수) 에서 구글로그인 결과 꺼내오기
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)!!
            println("result : $result")


            //정상적으로 로그인되었다면
            if (result.isSuccess) {
                println("result.isSuccess")
                //우리의 Firebase 서버에 사용자 이메일정보보내기
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }

        }
    }


    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        println("@@@ firebaseAuthWithGoogle")
        //구글로부터 로그인된 사용자의 정보(Credentail)을 얻어온다.
        val credential = GoogleAuthProvider.getCredential(account?.idToken!!, null)
        //그 정보를 사용하여 Firebase의 auth를 실행한다.
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {  //통신 완료가 된 후 무슨일을 할지
                    task ->
                if (task.isSuccessful) {
                    // 로그인 처리를 해주면 됨!
                    println("로그인 성공")
//                    goMainActivity(task.result?.user)
                } else {
                    // 오류가 난 경우!
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
//                progressBar.visibility = View.GONE
            }
    }
//
//    private fun initSignupButton() {
//        auth.createUserWithEmailAndPassword(binding.textView1.text.toString(),binding.textView2.text.toString())
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "회원가입에 성공했습니다!", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "이미 존재하는 계정이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//
//    fun signinAndSignup(){
//
//        auth.signInWithEmailAndPassword(binding.textView1.text.toString(),binding.textView2.text.toString())
//            .addOnCompleteListener{task->
//                if(task.isSuccessful){
//                    Toast.makeText(this,"로그인 성공",Toast.LENGTH_LONG).show()
//                }
//            }
////        auth?.createUserWithEmailAndPassword(binding.textView1.text.toString(),binding.textView2.text.toString())
////            ?.addOnCompleteListener{
////                task -> if(task.isSuccessful){
////                    println("계정 만들기 성공")
////                }else if(task.exception?.message.isNullOrEmpty()){
////                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
////                }else{
////                    println("계정이 있을때.")
////                    signinEmail()
////                }
////            }
//    }
//
//    fun signinEmail(){
//        auth?.signInWithEmailAndPassword(binding.textView1.text.toString(),binding.textView2.text.toString())
//            ?.addOnCompleteListener{
//                task ->
//                if(task.isSuccessful){
//                    println("아이디 페스워드 맞았을때")
//                }else{
//                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
//                }
//            }
//    }
}