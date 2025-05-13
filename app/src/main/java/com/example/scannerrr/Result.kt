package com.example.scannerrr

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.properties.Delegates

class Result : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var done : Button
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        textView=findViewById(R.id.textvw)
        done=findViewById(R.id.Done)
  val recieve = intent.getStringExtra("key")



        if (recieve!=null){
            textView.text=recieve

        }
        else{
            Toast.makeText(this,"Nothing recieved", Toast.LENGTH_SHORT).show()
        }

        done.setOnClickListener{
            database= FirebaseDatabase.getInstance().getReference("MohitData")
            val data = Anurag(recieve)
            database.child("Users").push().setValue(data).addOnSuccessListener{


                startActivity(Intent(this, MainActivity::class.java))

            }
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}