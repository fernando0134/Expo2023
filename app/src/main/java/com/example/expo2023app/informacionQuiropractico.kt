package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var btninformacion : Button

class informacionQuiropractico : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_quiropractico)

        btninformacion = findViewById(R.id.Btninformacionmasaje)

        btninformacion.setOnClickListener {
            val informacion: Intent = Intent(this,Agendar::class.java)
            startActivity(informacion)
        }

    }
}