package com.example.expo2023app

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView


private lateinit var Nombre: TextView
private lateinit var Dui: TextView
private lateinit var Departamento: TextView
private lateinit var back: Button

class InformacionEmpleados : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_empleados)

        // Obtener los parámetros del intent
        val nombre = intent.getStringExtra("nomb")
        val departamento = intent.getStringExtra("depa")
        val dui = intent.getStringExtra("dui")
        val foto = intent.getByteArrayExtra("foto") // Recuperar el ByteArray

        if (foto != null && foto.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.size)
            val imageView = findViewById<ImageView>(R.id.EmpleadoInfo_Foto)
            imageView.setImageBitmap(bitmap)
        }

        Nombre=findViewById(R.id.EmpleadoInfo_Nombre)
        Dui=findViewById(R.id.EmpleadosInfo_Dui)
        Departamento=findViewById(R.id.EmpleadosInfo_Departamento)
        back=findViewById(R.id.EmpleadosInfo_Back)

        Nombre.setText(nombre)
        Dui.setText(dui)
        Departamento.setText(departamento)

        back.setOnClickListener {
            finish()
        }
    }
}