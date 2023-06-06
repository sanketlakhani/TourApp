package com.example.masterprojectapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.masterprojectapp.databinding.ActivityAddDetailsBinding
import com.example.masterprojectapp.models.MyModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.*

class ActivityAddDetails : AppCompatActivity() {

    private lateinit var binding: ActivityAddDetailsBinding
    private lateinit var refrence: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }


    private fun initView() {

        binding.imgBack.setOnClickListener {

            finish()
        }

        binding.imgAddImage.setOnClickListener {

            uploadImage()
        }

        binding.btnAdd.setOnClickListener {

            val packageName: String = binding.edtPackName.text.toString()
            val price: String = binding.edtPrice.text.toString()
            val days: String = binding.edtDays.text.toString()
            val description: String = binding.edtDescription.text.toString()
            val location: String = binding.edtLocation.text.toString()

            if (TextUtils.isEmpty(packageName)) {

                Toast.makeText(this, "Please enter package name", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(price)) {

                Toast.makeText(this, "Please enter price", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(days)) {

                Toast.makeText(this, "Please enter days", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(description)) {

                Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()

            } else if (TextUtils.isEmpty(location)) {

                Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show()
            } else {

                progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Loading....")
                progressDialog.show()

                Toast.makeText(this, "Package Added", Toast.LENGTH_SHORT).show()

                refrence = FirebaseDatabase.getInstance().reference

                val key = refrence.root.child("InfoTb").push().key ?: ""

                val myModel = MyModel(
                    packageName,
                    description,
                    days,
                    location,
                    price,
                    key
                )

                refrence.root.child("InfoTb").child(key).setValue(myModel).addOnCompleteListener {

                    if (it.isSuccessful) {

                        progressDialog.hide()

                    } else {

                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                val intent = Intent(this, ActivityDashboard::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun uploadImage() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {

            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Please wait....")
            progressDialog.setMessage("")
            progressDialog.show()

            uri = data?.data!!

            var storageReference = FirebaseStorage.getInstance().getReference()

            storageReference.child("myImage").child(UUID.randomUUID().toString()).putFile(uri)
                .addOnProgressListener {

                    val progress: Double = (100.0
                            * it.bytesTransferred
                            / it.totalByteCount
                            )
                }.addOnSuccessListener {

                    Picasso.get().load(uri).into(binding.imgBackground)

                    Toast.makeText(this, "image uploaded successfully", Toast.LENGTH_SHORT).show()

                    progressDialog.hide()

                }.addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(p0: Exception) {

                        Log.e("TAG", "onFailure: " + p0.message)
                    }
                })
        }

    }
}

