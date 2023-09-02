package com.example.expo2023app

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView


private lateinit var Persona: TextView
private lateinit var precio: TextView
private lateinit var tipo: TextView
private lateinit var back: Button

class MostrarInformacion : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mostrar_informacion)

        val nombre = intent.getStringExtra("nomb")
        val clasificacion = intent.getStringExtra("cat")
        val price = intent.getStringExtra("price")
        val foto = intent.getByteArrayExtra("foto")

        if (foto != null && foto.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.size)
            val imageView = findViewById<ImageView>(R.id.ProductosInfo_Foto)
            imageView.setImageBitmap(bitmap)
        }

        Persona=findViewById(R.id.NombreCliente)
        precio=findViewById(R.id.PrecioMasaje)
        tipo=findViewById(R.id.TipoMasaje)
        back=findViewById(R.id.BtnRegresar)

        Persona.setText(nombre)
        precio.setText(price)
        tipo.setText(clasificacion)

        back.setOnClickListener {
            finish()
        }
    }
}