package com.example.masterprojectapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.masterprojectapp.databinding.ActivityViewMoreBinding
import com.squareup.picasso.Picasso

//, PaymentResultListener

class ActivityViewMore : AppCompatActivity() {

    private lateinit var binding: ActivityViewMoreBinding
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
//        image()
    }

//    private fun image() {
//        val imageUri = intent.getStringExtra("IMAGEURI")
//        Picasso.get()
//            .load(imageUri)
//            .error(R.drawable.google)
//            .into(binding.imgPlace)
//    }

    private fun initView() {

        binding.imgBackk.setOnClickListener {

            finish()
        }

        val packageNamee: String = intent.getStringExtra("PACKAGE NAME").toString()
        var descriptionn: String = intent.getStringExtra("DESCRIPTION").toString()
        var days: String = intent.getStringExtra("DAYS").toString()
        var price: String = intent.getStringExtra("PRICE").toString()
        var destination: String = intent.getStringExtra("LOCATION").toString()


        binding.txtGetPackageName.text = packageNamee
        binding.txtGetDescription.text = descriptionn
        binding.txtGetDays.text = days
        binding.txtGetPrice.text = price
        binding.txtDestination.text = destination

        if (packageNamee.equals("Kedarnath ")) {

            binding.imgPlace.setImageResource(R.drawable.kedarnath)

        } else if (packageNamee.equals("Goa ")) {

            binding.imgPlace.setImageResource(R.drawable.img_8)

        } else if (packageNamee.equals("Kerala")) {

            binding.imgPlace.setImageResource(R.drawable.img_9)

        } else if (packageNamee.equals("Masoori")) {

            binding.imgPlace.setImageResource(R.drawable.masori)

        } else if (packageNamee.equals("Ladakh")) {

            binding.imgPlace.setImageResource(R.drawable.img_18)

        } else if (packageNamee.equals("Mumbai ")) {

            binding.imgPlace.setImageResource(R.drawable.img_15)

        } else if (packageNamee.equals("Kashmir ")) {

            binding.imgPlace.setImageResource(R.drawable.img_19)

        }

        binding.btnBook.setOnClickListener {

            //paymentGateway()

            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Loading....")
            progressDialog.show()

            val intent = Intent(this, ActivityUserDetail::class.java)
            startActivity(intent)

            progressDialog.hide()

        }

        binding.mapLinear.setOnClickListener {

            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Loading....")
            progressDialog.show()

            val intent = Intent(this, ActivityMapLoading::class.java)
            intent.putExtra("DESTINATION", destination)
            startActivity(intent)

            progressDialog.hide()
        }
    }

    fun onClick(v: View) {

        binding.btnBook.setText("Booked")
    }
}
