package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
lateinit var btnClinica: Button
lateinit var btnTiflotienda: Button

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        btnClinica = findViewById(R.id.btnClinica)
        btnTiflotienda = findViewById(R.id.btnTlifo)

        btnClinica.setOnClickListener {
            val Clinica: Intent = Intent(this, CargaClinica::class.java)
            startActivity(Clinica)
        }

        btnTiflotienda.setOnClickListener {
            val tiflotienda: Intent = Intent(this, CargaTienda::class.java)
            startActivity(tiflotienda)
        }

    }
}