package com.example.expo2023app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import java.io.ByteArrayOutputStream
import java.sql.PreparedStatement
import java.sql.SQLException







private lateinit var lblmasaje:EditText
private lateinit var lblPrecio:EditText
private lateinit var btnAgendar:Button


private lateinit var spinner:Spinner

class Agendar : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar)

        val conn = ConnectSql().dbConn()
        val Masajitas = ArrayList<String>()


        spinner = findViewById(R.id.Masajitas)


        try {

            val statement = conn?.createStatement()
            val query = "SELECT masajes FROM Tbcitas"
            val resultSet = statement?.executeQuery(query)

            Masajitas.add("Seleccione una opcion...")


            while (resultSet?.next() == true) {

                val TipoMasaje = resultSet.getString("masajes")

                Masajitas.add(TipoMasaje)
            }


            val adapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, Masajitas)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            resultSet?.close()
            statement?.close()
        }catch (ex: SQLException) {
            println(ex.message)
        }


        lblmasaje=findViewById(R.id.masaje)
        lblPrecio=findViewById(R.id.precio)

        btnAgendar=findViewById(R.id.AgendarMasaje)

        btnAgendar.setOnClickListener {
            if(lblPrecio.text.toString()!="" && lblmasaje.text.toString()!="" && spinner.selectedItem.toString()!="Seleccione una opcion..."){
                try {
                    val addMasajes: PreparedStatement =  conn?.prepareStatement(


                        "INSERT INTO TbMasajes (idcita, nombre, precioUnit)\n" +
                                "SELECT c.idcita, ?, ?\n" +
                                "FROM Tbcitas c\n" +
                                "WHERE c.masajes = ?;"
                    )!!

                    val inicio: Intent = Intent(this, VistaAgendar::class.java)
                    addMasajes.setString(1, lblmasaje.text.toString())
                    addMasajes.setString(2, lblPrecio.text.toString())
                    addMasajes.setString(4, spinner.selectedItem.toString())
                    addMasajes.executeUpdate()

                    Toast.makeText(this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()

                    setResult(Activity.RESULT_OK, Intent())
                    startActivity(inicio)
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