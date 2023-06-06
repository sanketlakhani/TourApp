package com.example.masterprojectapp

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.masterprojectapp.databinding.ActivityMainBinding
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var pDialog: ProgressDialog
    lateinit var googleApiClient: GoogleApiClient
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var authStateListener: FirebaseAuth.AuthStateListener
    var callbackManager: CallbackManager? = null
   // lateinit var sp: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*sp = this.getSharedPreferences("UserInfo", MODE_PRIVATE)
        val v = sp.getInt("Entry", 0)
        if (v == 1) {

            var intent = Intent(this, ActivityDashboard::class.java)
            startActivity(intent)
            finish()

        } else {

            var edit = sp.edit()
            edit.putInt("Entry", 1)
            edit.commit()
        }*/

        internetCheck()

        initView()

        googleLogin()

        binding.linearFacebook.setOnClickListener {

            binding.loginButtonfb.performClick()
        }

        facebookLogin()
    }

    private fun internetCheck() {

        var connected = false

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.state == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI
            )?.state == NetworkInfo.State.CONNECTED) {
            true

        }else{

            val builder = AlertDialog.Builder(this)
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setTitle("Alert")
            builder.setMessage("Turn on internet and restart the app")
            builder.setCancelable(false)
            builder.setPositiveButton(
                "Ok "
            ) { dialog, which -> finish() }
            builder.setNegativeButton(
                "No"
            ) { dialog, which -> dialog!!.dismiss() }
            builder.show()
        }
    }

    private fun facebookLogin() {

        FacebookSdk.sdkInitialize(getApplicationContext());

       var  callbackManager = CallbackManager.Factory.create();

        val EMAIL = "email"

        binding.loginButtonfb.setReadPermissions(Arrays.asList(EMAIL))

        binding.loginButtonfb.registerCallback(
            callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {
                }

                override fun onSuccess(result: LoginResult) {

                    Toast.makeText(
                        this@MainActivity,
                        "Facebook login successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    var intent = Intent(this@MainActivity, ActivityDashboard::class.java)
                    startActivity(intent)

                    var sp: SharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE)
                    var s = sp.edit()
                    s.putString("Username","Admin")
                    s.commit()
                }
            })
    }


    private fun googleLogin() {

        firebaseAuth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->

            val user = firebaseAuth.currentUser

            if (user != null) {

                Log.d(
                    "TAG", "onAuthStateChanged:sign" +
                            "ed_in:" + user.uid
                )
            } else {

                Log.d("TAG", "onAuthStateChanged:signed_out")
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.example.masterprojectapp.R.string.web_client_id)) //you can also use R.string.default_web_client_id
            .requestEmail()
            .build()

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, object : GoogleApiClient.OnConnectionFailedListener {
                override fun onConnectionFailed(p0: ConnectionResult) {

                }
            })
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        binding.linearGoogle.setOnClickListener {

            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(intent, 101)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) {

            val result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            handleSignInResult(result)

            Toast.makeText(this, "Google login successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult?) {

        if (result!!.isSuccess) {
            val account = result!!.signInAccount
            var idToken = account!!.idToken
            var name = account!!.displayName
            var email = account!!.email

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuthWithGoogle(credential)

        } else {

            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(credential: AuthCredential) {

        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful)
                if (task.isSuccessful) {

                    val intent = Intent(this, ActivityDashboard::class.java)
                   // intent.putExtra("Username", "Admin")
                    startActivity(intent)

                    var sp: SharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE)
                    var s = sp.edit()
                    s.putString("Username","Admin")
                    s.commit()

                } else {
                    Log.w("TAG", "signInWithCredential" + task.exception!!.message)
                    task.exception!!.printStackTrace()
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun initView() {

        auth = Firebase.auth

        binding.btnLogin.setOnClickListener {

            var email: String = binding.edtEmail.text.toString()
            var password: String = binding.edtPassword.text.toString()
            var username: String = binding.edtUsername.text.toString()

            pDialog = ProgressDialog(this)
            pDialog.setMessage("Loading....")
            pDialog.show()

            if (TextUtils.isEmpty(username)) {

                pDialog.hide()
                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show()

            } else if (TextUtils.isEmpty(email)) {

                pDialog.hide()

                //showToast("Please enter email")
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()

            } else if (TextUtils.isEmpty(password)) {

                pDialog.hide()
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()

            } else {

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {

                    if (it.isSuccessful) {

                        val intent = Intent(this, ActivityDashboard::class.java)
                         intent.putExtra("Username", username)
                        startActivity(intent)

                        Toast.makeText(this, "account login successfully", Toast.LENGTH_SHORT)
                            .show()

                        pDialog.hide()

                    } else {

                        pDialog.hide()
                        Toast.makeText(this, "error:" + it.exception?.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        binding.txtSignup.setOnClickListener {

            var intent = Intent(this, ActivitySignUp::class.java)
            startActivity(intent)
        }
    }

    /* fun showToast(s: String) {

         val toast = Toast.makeText(this, s, Toast.LENGTH_SHORT)
         val viewGroup = toast.view as ViewGroup?
         val textView = viewGroup!!.getChildAt(0) as TextView
         textView.textSize = 12f
         viewGroup!!.setBackgroundResource(R.drawable.button_2)
         toast.show()
     }*/
}