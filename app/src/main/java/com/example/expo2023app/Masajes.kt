package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var btnQuiropractico : Button
lateinit var btnRelajante: Button
lateinit var btnmasajes : Button

class Masajes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masajes)

        btnRelajante = findViewById(R.id.relajante)
        btnmasajes = findViewById(R.id.BtnMasaje)
        btnQuiropractico = findViewById(R.id.Quiropractico)

        btnmasajes.setOnClickListener {
            val masajes: Intent = Intent(this, Agendar::class.java)
            startActivity(masajes)
        }

        btnQuiropractico.setOnClickListener {
            val quiropractico: Intent = Intent(this,informacionQuiropractico::class.java)
            startActivity(quiropractico)
        }

        btnRelajante.setOnClickListener{
            val relajante: Intent = Intent(this, informacion::class.java)
            startActivity(relajante)
        }
    }
}