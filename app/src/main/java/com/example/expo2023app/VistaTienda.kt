package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

private lateinit var BtnProductos: Button

class VistaTienda : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_tienda)

        BtnProductos = findViewById(R.id.ProductosTienda)

        BtnProductos.setOnClickListener {
            val inicio: Intent = Intent(this, ProductosVista::class.java)
            startActivity(inicio)
        }
    }
}