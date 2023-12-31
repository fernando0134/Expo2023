package com.example.expo2023app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var btnMasajes: Button
lateinit var btnCalendario : Button
lateinit var BtnEmpleados : Button
lateinit var BtnFactura : Button
lateinit var BtnReportes: Button
lateinit var BtnClientes: Button

class VistaClinica : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_clinica)

        btnCalendario = findViewById(R.id.Calendario)
        btnMasajes = findViewById(R.id.BtnMasaje)
        BtnEmpleados  = findViewById(R.id.Empleados)
        BtnFactura = findViewById(R.id.btnFactura)
        BtnReportes = findViewById(R.id.reportes)
        BtnClientes = findViewById(R.id.Cliente)

        btnMasajes.setOnClickListener {
            val Masajes: Intent = Intent(this, Masajes::class.java)
            startActivity(Masajes)
        }

        btnCalendario.setOnClickListener {
            val calendario: Intent = Intent(this,Calendario::class.java)
            startActivity(calendario)
        }

        BtnEmpleados.setOnClickListener {
            val Emplo: Intent = Intent(this,Empleados::class.java)
            startActivity(Emplo)
        }
        BtnReportes.setOnClickListener {
            val Repor: Intent = Intent(this,Reportes::class.java)
            startActivity(Repor)
        }
        BtnClientes.setOnClickListener {
            val clientes: Intent = Intent(this,Clientes::class.java)
            startActivity(clientes)
        }
        BtnFactura.setOnClickListener {
            val factura: Intent = Intent(this,Factura::class.java)
            startActivity(factura)
        }
    }
}