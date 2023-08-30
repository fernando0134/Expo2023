package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class pantallacarga : AppCompatActivity() {
    private val tiempo = 2000
    private  val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantallacarga)

        handler.postDelayed({
            val intent: Intent = Intent(this,Menu::class.java)
            startActivity(intent)
        },tiempo.toLong())
    }
}