package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

lateinit var TIngresarCodigo: EditText
lateinit var BSiguiente: Button

class IngresarCodigoRecuperacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingresar_codigo_recuperacion)

        TIngresarCodigo = findViewById(R.id.txtIngresarCodigo)
        BSiguiente = findViewById(R.id.btnEnviarCodigo)

        val codigo: String = intent.getStringExtra("0")!!
        val destinatario: String = intent.getStringExtra("1")!!

        val StringCodigo: String = "Su código de recuperación es: $codigo"

        val EnviarCoorreo = Enviarcorreo(destinatario, StringCodigo, "Código de recuperación de cuenta es")
        EnviarCoorreo.execute()

        BSiguiente.setOnClickListener {
            if(TIngresarCodigo.text.toString().trim() == "")
            {
                Toast.makeText(this, "Ingrese el código de recuperación", Toast.LENGTH_SHORT).show()
            }
            else if (TIngresarCodigo.text.toString().trim() == codigo)
            {
                val Restablecer: Intent = Intent(this, vistarecuperacion::class.java)
                Restablecer.putExtra("0", destinatario)
                startActivity(Restablecer)
            }
            else
            {
                Toast.makeText(this, "El código ingresado es incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}