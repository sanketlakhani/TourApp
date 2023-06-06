package com.example.masterprojectapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.masterprojectapp.databinding.ActivityUserDetailBinding
import com.example.masterprojectapp.models.UserModel
import com.google.firebase.database.*
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ActivityUserDetail : AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var binding: ActivityUserDetailBinding
    var gender: String = ""
    private lateinit var refrence: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    lateinit var d: Dialog
    lateinit var namee: String
    lateinit var numberr: String
    lateinit var emaill: String
    lateinit var addresss: String
    lateinit var personNumber: String
    lateinit var paymentId: String
    lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var currentDateAndTime: String
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        currentDateAndTime = simpleDateFormat.format(Date())
        binding.txtTourDate.text = currentDateAndTime

        bookPackage()

        binding.txtTourDate.setOnClickListener {

            datePicker()

        }

        binding.imgBackkk.setOnClickListener {

            finish()
        }
    }

    private fun datePicker() {

        @SuppressLint("SetTextI18n")
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val datePicker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                binding.txtTourDate.setText("" + dayOfMonth + " ," + monthOfYear + ", " + year)
            },
            year,
            month,
            day
        )
        datePicker.show()
    }

    private fun bookPackage() {

        binding.btnBookk.setOnClickListener {

            namee = binding.edtPersonName.text.toString()
            emaill = binding.edtEmailll.text.toString()
            numberr = binding.edtMobNumber.text.toString()
            addresss = binding.edtAddress.text.toString()
            personNumber = binding.edtNumPerson.text.toString()

            if (TextUtils.isEmpty(namee)) {

                Toast.makeText(this, "Please enter name ", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(numberr)) {

                Toast.makeText(this, "Please enter number ", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(emaill)) {

                Toast.makeText(this, "Please enter email ", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(addresss)) {

                Toast.makeText(this, "Please enter address ", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(gender)) {

                Toast.makeText(this, "Please select your gender ", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(personNumber)) {

                Toast.makeText(this, "Please enter number of person ", Toast.LENGTH_SHORT).show()
            } else {

                alertDialog()

                //requestSmsPermission()
            }
        }

        binding.radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            val id = radioGroup.checkedRadioButtonId

            if (id != -1) {

                if (id == R.id.rBtnMale) {

                    gender = "male"
                } else if (id == R.id.rBtnFemale
                ) {

                    gender = "female"
                } else if (id == R.id.rBtnOther) {

                    gender = "other"
                }
            } else {

            }
        })
    }

    private fun alertDialog() {

        d = Dialog(this@ActivityUserDetail)
        d.setContentView(R.layout.alert_dialog_box)
        d.setCancelable(false)

        var txtYes = d.findViewById<TextView>(R.id.txtYes)

        var txtPayment = d.findViewById<TextView>(R.id.txtPayment)

        txtYes.setOnClickListener {

            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Loading....")
            progressDialog.show()

            d.dismiss()

            progressDialog.hide()

            onClick(View(this))

        }

        txtPayment.setOnClickListener {

            val activity: Activity = this
            val co = Checkout()

            try {
                val options = JSONObject()
                options.put("name", "Razorpay Corp")
                options.put("description", "Tour package booking")
                //You can omit the image option to fetch the image from the dashboard
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
                options.put("theme.color", "#3399cc");
                options.put("currency", "INR");
                options.put("amount", "50000")//pass amount in currency subunits

                val retryObj = JSONObject();
                retryObj.put("enabled", true);
                retryObj.put("max_count", 4);
                options.put("retry", retryObj);

                val prefill = JSONObject()
                prefill.put("email", "gaurav.kumar@example.com")
                prefill.put("contact", "9876543210")

                options.put("prefill", prefill)
                co.open(activity, options)
            } catch (e: Exception) {
                Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()

                Log.e("TAG", "alertDialog: " + e)
                e.printStackTrace()
            }
        }

        d.show()
    }

    fun onClick(v: View) {

        binding.btnBookk.setText("Booked")
    }

    override fun onPaymentSuccess(p0: String?, paymentdata: PaymentData?) {
        try {

            Toast.makeText(this, "Payment success", Toast.LENGTH_SHORT).show()
            paymentId = paymentdata?.paymentId.toString()

            d.dismiss()

            saveData()
            //sms()

            notification()

            finish()

            val intent=Intent(this,ActivityDashboard::class.java)
            startActivity(intent)

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this, "Payment Failed" + p0, Toast.LENGTH_SHORT).show()

    }

    private fun notification() {

        //val intent = Intent(this, DashBoardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContentTitle("Congratulations!!")
                .setContentText("Dear customer your package has been booked")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.mipmap.ic_launcher_foreground
                    )
                )
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.mipmap.ic_launcher_foreground
                    )
                ).setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())

    }

    private fun saveData() {

        refrence = FirebaseDatabase.getInstance().reference

        val key = refrence.root.child("UserTb").push().key ?: ""

        val userModel = UserModel(
            namee,
            emaill,
            numberr,
            addresss,
            paymentId,
            personNumber,
            currentDateAndTime,
            gender,
            key
        )

        refrence.root.child("UserTb").child(key).setValue(userModel).addOnCompleteListener {

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Please Wait...")
            progressDialog.show()

            if (it.isSuccessful) {
               /* binding.edtPersonName.text.clear()
                binding.edtEmailll.text.clear()
                binding.edtMobNumber.text.clear()
                binding.edtPersonName.text.clear()
                binding.edtAddress.text.clear()
                *//*    binding.radioGroup.clearCheck()
                    binding.edtExtraNote.text.clear()*/

                progressDialog.hide()

            } else {

                Toast.makeText(this, " " + it.exception?.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }.addOnFailureListener {

            Toast.makeText(
                this@ActivityUserDetail,
                " something went wrong",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    /*fun sms() {
        try {
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT >= 23) {
                smsManager = this.getSystemService(SmsManager::class.java)
            } else {
                smsManager = SmsManager.getDefault()
            }
            smsManager.sendTextMessage(
                numberr,
                "RazorPay",
                "Dear" + numberr + " Your Package Booking Completed\nWe Call You Soon",
                null,
                null
            )
//            Toast.makeText(applicationContext, "Message Sent", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Please enter all the data.." + e.message.toString(),
                Toast.LENGTH_LONG
            )
                .show()
            Log.e("TAG", "sms:  " + e.message)
        }
    }

    private fun requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.SEND_SMS),
                101
            )
        } else {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                sms()
            } else {
                Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show()
            }
        }
    }*/
}