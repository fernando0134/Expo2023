package com.example.expo2023app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var Calen : Button

class Calendario : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        Calen = findViewById(R.id.BtnCalendario)

        Calen.setOnClickListener{
            val uwu: Intent = Intent(this, Agendar::class.java)
            startActivity(uwu)
        }
    }
}