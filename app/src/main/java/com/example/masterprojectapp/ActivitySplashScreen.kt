package com.example.masterprojectapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth

class ActivitySplashScreen : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        //Normal Handler is deprecated , so we have to change the code little bit

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({

            if (auth.currentUser!=null){

                val intent = Intent(this, ActivityDashboard::class.java)
                startActivity(intent)
                finish()
            }
            else{

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 3000) // 3000 is the delayed time in milliseconds.
    }
}

