package com.example.expo2023app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

lateinit var BRegistrarseFinal: Button
lateinit var TCorreo: EditText
lateinit var TContra: EditText
lateinit var TConfirmar: EditText


class Registrarse : AppCompatActivity() {

    private var connectSql = ConnectSql();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        BRegistrarseFinal = findViewById(R.id.btnRegistrarseFinal)
        TCorreo = findViewById(R.id.txtCampoCorreo)
        TContra = findViewById(R.id.txtContra)
        TConfirmar = findViewById(R.id.txtConfirmarContra)

        val PatronEmail: String = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        val encriptador = Encriptar()

        BRegistrarseFinal.setOnClickListener {
            val Correo: String = TCorreo.text.toString().trim()

            if(Correo == "" || TContra.text.toString().trim() == "")
            {
                Toast.makeText(this, "Asegúrese de ingresar todas las credenciales", Toast.LENGTH_SHORT).show()
            }
            else if(ChecarCorreo(TCorreo.text.toString()).toString() == "0")
            {
                Toast.makeText(this, "El correo ingresado ya ha sido utilizado", Toast.LENGTH_SHORT).show()
            }
            else if(Correo.count() > 30)
            {
                Toast.makeText(this, "El correo ingresado es demasiado extenso", Toast.LENGTH_SHORT).show()
            }
            else if(TContra.text.toString().trim().count() > 20)
            {
                Toast.makeText(this, "La contraseña ingresada es demasiado extensa", Toast.LENGTH_SHORT).show()
            }
            else if(TContra.text.toString().trim().count() < 8)
            {
                Toast.makeText(this, "La contraseña es demasiado corta", Toast.LENGTH_SHORT).show()
            }
            else if(TContra.text.toString().trim() != TConfirmar.text.toString().trim())
            {
                Toast.makeText(this, "Las contraseñas no coinciden, asegúrese de ingresar la contraseña correctamente", Toast.LENGTH_SHORT).show()
                TContra.requestFocus()
            }
            else if(Correo.matches(PatronEmail.toRegex()) && AgregarUsuario(Correo, encriptador.sha256(TContra.text.toString().trim())) == 1)
            {
                Toast.makeText(this, "!Se ha registrado exitosamente¡", Toast.LENGTH_LONG).show()

                finish()
            }
            else
            {
                Toast.makeText(this, "La dirección de correo electrónico es inválida", Toast.LENGTH_SHORT).show()
            }
        }
    }public fun ChecarCorreo(correo: String): Int {

        val statement = connectSql.dbConn()?.createStatement()
        val resulSet =
            statement?.executeQuery("select correo_usuario from usuarios where correo_usuario = '$correo';")

        if (resulSet?.next() == true) {
            val correo_usuario = resulSet?.getString("correo_usuario") ?: ""

            if (correo_usuario == "") {
                return 1
            } else {
                return 0
            }
        } else {
            return 1
        }
    }  public fun AgregarUsuario(correo: String, contra: String): Int {
        try
        {
            val AgregarUsuario =connectSql.dbConn()?.prepareStatement("Insert into usuarios values('$contra','$correo');")!!
            AgregarUsuario.executeUpdate()
        }
        catch (e: java.lang.Exception)
        {
            Toast.makeText(this, "Ha habido un error en la aplicación, intentélo más tarde o reinicie la aplicación", Toast.LENGTH_LONG).show()
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            return 0
        }
        return 1
    }
}