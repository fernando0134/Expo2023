package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


lateinit var btnA : Button
lateinit var btnB: Button
lateinit var btnc : Button


class ProductosVista : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos_vista)

        btnA = findViewById(R.id.Añadir3)
        btnB = findViewById(R.id.Añadir1)
        btnc = findViewById(R.id.Añadir2)

        btnA.setOnClickListener {
            val masajes: Intent = Intent(this, Productos::class.java)
            startActivity(masajes)
        }

        btnB.setOnClickListener {
            val masajes: Intent = Intent(this, Productos::class.java)
            startActivity(masajes)
        }
        btnc.setOnClickListener {
            val masajes: Intent = Intent(this, Productos::class.java)
            startActivity(masajes)
        }
    }
}