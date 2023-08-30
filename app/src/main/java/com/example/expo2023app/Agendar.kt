package com.example.expo2023app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import java.sql.PreparedStatement
import java.sql.SQLException

lateinit var CajitaNombre: EditText
lateinit var CajitaFecha: EditText
lateinit var CajitaMasaje: EditText
lateinit var Boton: Button
lateinit var Mostar: Button
lateinit var ListVista: ListView
class Agendar : AppCompatActivity() {

    private var connectSql = ConnectSql()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar)

        CajitaNombre = findViewById(R.id.txtNombre)
        CajitaFecha = findViewById(R.id.txtFecha)
        CajitaMasaje = findViewById(R.id.txtMasaje)
        Boton = findViewById(R.id.btnAgendar)
        ListVista = findViewById(R.id.miLista)
        Mostar = findViewById(R.id.btnMostar)

        Boton.setOnClickListener {
            if (CajitaNombre.text.toString().trim() == ""|| CajitaFecha.text.toString().trim() == "" || CajitaMasaje.text.toString().trim() == ""){
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show()
            }
            else{
            try {
                val addAgendar: PreparedStatement =
                    connectSql.dbConn()?.prepareStatement("insert into Tbcitas values (?,?,?)")!!
                addAgendar.setString(1, CajitaNombre.text.toString())
                addAgendar.setString(2, CajitaFecha.text.toString())
                addAgendar.setString(3, CajitaMasaje.text.toString())

                addAgendar.executeUpdate()

                Toast.makeText(this, "Masaje agegando ingresado correctamente", Toast.LENGTH_SHORT)
                    .show()
                CajitaFecha.clearFocus()
                CajitaNombre.clearFocus()
                CajitaMasaje.clearFocus()

                //Para ocultar el teclado
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(Boton.windowToken, 0)

                CajitaNombre.setText("")
                CajitaFecha.setText("")
                val Clinica: Intent = Intent(this, informacion::class.java)
                startActivity(Clinica)

            } catch (ex: SQLException) {
                Toast.makeText(this, "Error al ingresar", Toast.LENGTH_SHORT).show()
            }
        }
        }

        val myData = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, myData)
        ListVista.adapter = adapter


        Mostar.setOnClickListener {
            myData.clear()
            try{
                val statement = connectSql.dbConn()?.createStatement()
                val resulSet = statement?.executeQuery("select cliente, tipodemasaje,fechayhora from Tbcitas")

                while (resulSet?.next() == true) {

                    val column1 = resulSet.getString("nombre")
                    val column2 = resulSet.getString("fecha")
                    val column3 = resulSet.getString("masaje")
                    val newElement = "$column1, $column2,$column3"

                    myData.add(newElement)

                    adapter.notifyDataSetChanged()

                }
                ListVista.visibility = View.VISIBLE
            }catch (ex: SQLException){
                Toast.makeText(this, "Error al mostrar", Toast.LENGTH_SHORT).show()
            }
        }

    }
}