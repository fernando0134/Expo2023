package com.example.expo2023app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.sql.PreparedStatement
import java.sql.SQLException

class EditarAgenda : AppCompatActivity() {

    private lateinit var Masaje1: EditText
    private lateinit var Precio: EditText
    private lateinit var btnActualizar: Button

    private lateinit var spinner: Spinner


    private lateinit var producto_antes:TextView
    private lateinit var precio_antes:TextView
    private lateinit var categoria_antes:TextView

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_agenda)



        val nombre_got = intent.getStringExtra("nomb")
        val clasificacion_got = intent.getStringExtra("cat")
        val price_got = intent.getStringExtra("price")


        producto_antes=findViewById(R.id.EditarEmpleado_name_antes)
        precio_antes=findViewById(R.id.EditarEmpleado_Dui_antes)
        categoria_antes=findViewById(R.id.EditarEmpleado_depa_antes)

        producto_antes.setText(nombre_got)
        precio_antes.setText(price_got)
        categoria_antes.setText(clasificacion_got)

        val conn = ConnectSql().dbConn()

        val masajistas = ArrayList<String>()


        spinner = findViewById(R.id.EditarMasajita)
        try {

            val statement = conn?.createStatement()
            val query = "SELECT masajes FROM Tbcitas"

            val resultSet = statement?.executeQuery(query)

            masajistas.add("Nueva informacion...")


            while (resultSet?.next() == true) {
                //
                val TipoMasaje1 = resultSet.getString("masajes")
                masajistas.add(TipoMasaje1)
            }


            val adapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, masajistas)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            resultSet?.close()
            statement?.close()
        }catch (ex: SQLException) {
            println(ex.message)
        }


        Masaje1=findViewById(R.id.EditarMasaje)
        Precio=findViewById(R.id.EditarPrecio)

        btnActualizar=findViewById(R.id.Actualizar)

        btnActualizar.setOnClickListener {
            if(Precio.text.toString()!="" && Masaje1.text.toString()!="" && spinner.selectedItem.toString()!="Nuevo Masaje..."){
                try {
                    val addMasajes: PreparedStatement =  conn?.prepareStatement(

                        "UPDATE TbMasajes\n" +
                                "SET IdMasajes = c.idcita, nombre = ?, precioUnit = ?\n" +
                                "FROM TbMasajes p\n" +
                                "JOIN Tbcitas c ON p.idcita = c.idcita\n" +
                                "WHERE p.IdMasajes = 1\n" +
                                "AND c.masajes = ?;"

                    )!!

                    addMasajes.setString(1, Masaje1.text.toString())
                    addMasajes.setString(2, Precio.text.toString())
                    addMasajes.setString(3, spinner.selectedItem.toString())
                    addMasajes.executeUpdate()

                    Toast.makeText(this, "Se ha actualizado correctamente", Toast.LENGTH_SHORT).show()

                    setResult(Activity.RESULT_OK, Intent())
                    conn.close()
                    finish()
                }
                catch (ex: SQLException){
                    Toast.makeText(this, "Error al ingresar: "+ex, Toast.LENGTH_SHORT).show()
                    println(ex)
                    setResult(Activity.RESULT_OK, Intent())
                    conn?.close()
                }
            }
            else{
                Toast.makeText(this, "Rellene todos los campo", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onBackPressed() {
        finish()
    }
}
