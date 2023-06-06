package com.example.masterprojectapp

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.masterprojectapp.databinding.ActivityMainBinding
import com.example.masterprojectapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ActivitySignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var pDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {

        auth = Firebase.auth

        binding.btnSignup.setOnClickListener {

            var emaill: String = binding.edtEmaill.text.toString()
            var passwordd: String = binding.edtPasswordd.text.toString()
            var usernamee: String = binding.edtUsernamee.text.toString()

            pDialog = ProgressDialog(this)
            pDialog.setMessage("Loading....")
            pDialog.show()

            if (TextUtils.isEmpty(usernamee)) {


                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show()
                pDialog.hide()

            } else if (TextUtils.isEmpty(emaill)) {


                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                pDialog.hide()

            } else if (TextUtils.isEmpty(passwordd)) {


                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                pDialog.hide()

            }else{

                auth.createUserWithEmailAndPassword(emaill, passwordd).addOnCompleteListener {

                    if (it.isSuccessful) {

                        val intent = Intent(this, ActivityDashboard::class.java)
                        startActivity(intent)


                        var sp: SharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE)
                        var s = sp.edit()
                        s.putString("Username",usernamee)
                        s.commit()


                        pDialog.hide()

                        Toast.makeText(this, "account create successfully", Toast.LENGTH_SHORT).show()
                    } else {

                        Toast.makeText(this, "error:" + it.exception?.message, Toast.LENGTH_SHORT)
                            .show()

                        pDialog.hide()
                    }
                }
            }
        }
    }
    /*fun showToast(s: String) {

        val toast = Toast.makeText(this, s, Toast.LENGTH_SHORT)
        val viewGroup = toast.view as ViewGroup?
        val textView = viewGroup!!.getChildAt(0) as TextView
        textView.textSize = 12f
        viewGroup!!.setBackgroundResource(R.drawable.button_2)
        toast.show()
    }*/
}