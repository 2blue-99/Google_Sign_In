package com.example.googlelogin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.googlelogin.databinding.FragmentSecondBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentSecond : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSecondBinding.inflate(inflater)

        val sign= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient= GoogleSignIn.getClient(requireActivity(),sign)

        binding.signOut.setOnClickListener{
            firebaseAuth.signOut()
            googleSignInClient.signOut()
            parentFragmentManager.beginTransaction().replace(R.id.myFragment,FragmentFirst()).commit()
        }
        binding.invoke.setOnClickListener{
            firebaseAuth.currentUser?.delete()
            googleSignInClient.revokeAccess()
            parentFragmentManager.beginTransaction().replace(R.id.myFragment,FragmentFirst()).commit()
        }

        binding.email.text = firebaseAuth.currentUser?.uid
        binding.password.text = firebaseAuth.currentUser?.email


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentSecond().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}