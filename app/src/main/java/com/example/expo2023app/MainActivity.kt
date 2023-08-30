package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.sql.PreparedStatement

lateinit var TNombre: EditText
lateinit var TCodigo: EditText
lateinit var BEnviar: Button
lateinit var BRegistrarse: Button
lateinit var BInvisible: Button

class MainActivity : AppCompatActivity() {

    private var connectSql = ConnectSql()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TNombre = findViewById(R.id.TxtCorreo)
        TCodigo = findViewById(R.id.TxtContraseña)
        BEnviar = findViewById(R.id.BtnEnviar)
        BRegistrarse = findViewById(R.id.BtnRegistrarse)
        BInvisible = findViewById(R.id.BtnInvisible)

        val encriptador = Encriptar()

        BInvisible.setOnClickListener {
            val Reinicio: Intent = Intent(this, recuperacion::class.java)
            startActivity(Reinicio)
        }

        BEnviar.setOnClickListener {

            if(TNombre.text.toString().trim() == "" || TCodigo.text.toString().trim() == "")
            {
                Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show()
            }
            else
            {
                try
                {

                    val statement =connectSql.dbConn()?.createStatement()
                    val resulSet =statement?.executeQuery("select id_usuario, correo_usuario, contra_usuario from usuarios where correo_usuario = '${TNombre.text.toString()}' and contra_usuario = '${encriptador.sha256(TCodigo.text.toString().trim())}';")


                    if(resulSet?.next() == false)
                    {
                        Toast.makeText(this, "Nombre de usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show()
                    }
                    val resulSet1 =statement?.executeQuery("select id_usuario, correo_usuario, contra_usuario from usuarios where correo_usuario = '${TNombre.text.toString()}' and contra_usuario = '${ encriptador.sha256(TCodigo.text.toString().trim())}';")

                    while (resulSet1?.next() == true) {

                        val a1 = resulSet1.getString("correo_usuario")
                        val a2 = resulSet1.getString("contra_usuario")
                        val a4 = resulSet1.getString("id_usuario")


                        if(TNombre.text.toString() == a1 &&  encriptador.sha256(TCodigo.text.toString().trim())== a2)
                        {
                            val inicio: Intent = Intent(this, pantallacarga::class.java)

                            val statement1: PreparedStatement = connectSql.dbConn()?.prepareStatement("Insert into tbRegistros values($a4);")!!
                            statement1.executeUpdate()
                            startActivity(inicio)
                            TNombre.text.clear()
                            TCodigo.text.clear()
                        }
                        else
                        {
                            Toast.makeText(this,"Nombre de usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show()
                        }

                    }

                }
                catch (e: java.lang.Exception)
                {
                    Toast.makeText(this, "Error en la aplicación", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
        BRegistrarse.setOnClickListener {
            val RegistrarseInicio: Intent = Intent(this, Registrarse::class.java)
            startActivity(RegistrarseInicio)
        }
    }

}