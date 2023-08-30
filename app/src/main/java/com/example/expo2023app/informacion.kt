package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var agendar: Button
class informacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion)


        agendar = findViewById(R.id.Btninformacionmasaje)

        agendar.setOnClickListener {
            val Clinica: Intent = Intent(this, Agendar::class.java)
            startActivity(Clinica)
        }

    }
}