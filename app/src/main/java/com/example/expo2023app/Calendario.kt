package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var btnCAlendario : Button

class Calendario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        btnCAlendario = findViewById(R.id.calend)

        btnCalendario.setOnClickListener{
            val uwu: Intent = Intent(this, Agendar::class.java)
            startActivity(uwu)
        }
    }
}