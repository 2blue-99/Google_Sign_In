package com.example.googlelogin

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.googlelogin.databinding.ActivityMainBinding
import com.example.googlelogin.databinding.FragmentFirstBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentFirst : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentFirstBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(inflater)

        //앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정.
        //유저 아이디와 기본 프로필 정보 요청
        val sign = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //
        googleSignInClient = GoogleSignIn.getClient(requireActivity(),sign)
        binding.SignInBtn.setOnClickListener{
            signInGoogle()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    fun signInGoogle(){
        val signIntent= googleSignInClient.signInIntent
        launcher.launch(signIntent)
    }

    private var launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult() ){
            result ->
        Log.e(javaClass.simpleName, "result.resultCode : ${result.resultCode}!!!")
        Log.e(javaClass.simpleName, "RESULT_OK : $RESULT_OK", )

        if (result.resultCode == RESULT_OK)
        {
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            Log.e(javaClass.simpleName, "result.data : ${result.data}")
            Log.e(javaClass.simpleName, "task : $task")
            handleResult(task)
        }
        else{
            Log.e(javaClass.simpleName, "handleResult : launcher!!!", )
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        Log.e(javaClass.simpleName, "handleResult : 들어옴!!!", )
        Log.e(javaClass.simpleName, "task : $task!!!", )

        if(task.isSuccessful){
            val account=task.result
            if(account!=null){
                updateUI(account)
            }
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        Log.e(javaClass.simpleName, "updateUI : 들어옴!!!", )
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener{
            if(it.isSuccessful){
                Log.e(javaClass.simpleName, "updateUI : 성공!!!", )
                parentFragmentManager.beginTransaction().replace(R.id.myFragment,FragmentSecond()).commit()
            }
            else{
                Toast.makeText(context,it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentFirst().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}