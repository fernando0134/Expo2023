package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class CargaClinica : AppCompatActivity() {

    private val tiempo = 2000
    private  val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carga_clinica)

        handler.postDelayed({
            val intent: Intent = Intent(this,VistaClinica::class.java)
            startActivity(intent)
        },tiempo.toLong())

    }
}