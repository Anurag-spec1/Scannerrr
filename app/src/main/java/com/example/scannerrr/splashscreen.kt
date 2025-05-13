package com.example.scannerrr

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.github.ybq.android.spinkit.SpinKitView

class splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Find the SpinKitView
        val spinKitView = findViewById<SpinKitView>(R.id.spin_kit)
        spinKitView.visibility = android.view.View.VISIBLE // Ensure the loader is visible

        // Delay transition to MainActivity for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Close SplashScreen to prevent back navigation
        }, 3000)
    }
}

