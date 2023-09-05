package com.example.expo2023app

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

private lateinit var nombreCliente1: TextView
private lateinit var Departamento: TextView
private lateinit var telefono: TextView
private lateinit var back: Button

class InformacionCliente : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_cliente)

        // Obtener los parámetros del intent
        val nombreCliente = intent.getStringExtra("nomb")
        val DepartamentoCliente = intent.getStringExtra("depa")
        val TelefonoCliente = intent.getStringExtra("cel")


        nombreCliente1=findViewById(R.id.ClienteInfo_Cliente)
        Departamento=findViewById(R.id.ClienteInfo_Telefono)
        telefono=findViewById(R.id.ClienteInfo_Departamento)
        back=findViewById(R.id.ClienteInfo_Back)

        nombreCliente1.setText(nombreCliente)
        Departamento.setText(DepartamentoCliente)
        telefono.setText(TelefonoCliente)

        back.setOnClickListener {
            finish()
        }
    }
}