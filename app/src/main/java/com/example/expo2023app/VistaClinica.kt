package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var btnMasajes: Button
lateinit var btnCalendario : Button
lateinit var BtnEmpleados : Button
lateinit var BtnFactura : Button
class VistaClinica : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_clinica)

        btnCalendario = findViewById(R.id.Calendario)
        btnMasajes = findViewById(R.id.btnmasajes)
        BtnEmpleados  = findViewById(R.id.Empleados)
        BtnFactura = findViewById(R.id.btnFactura)

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
    }
}