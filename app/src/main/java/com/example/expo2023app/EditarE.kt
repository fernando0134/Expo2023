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


//Los  campos de texto que usamos para poner el texto
@SuppressLint("StaticFieldLeak")
private lateinit var lblEmpleados: EditText
private lateinit var lblDui: EditText
private lateinit var btnActualizarEmpleado: Button

private lateinit var spinner: Spinner

//Valores anteriores
private lateinit var Nombre_antesEmpleado: TextView
private lateinit var Dui_antesEmpleado: TextView
private lateinit var Departamento_antesEmpleado: TextView

class EditarE : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_e)


        val Nombre_Empleado = intent.getStringExtra("nomb")
        val Departamento_Empleado = intent.getStringExtra("depa")
        val Dui_Empleado = intent.getStringExtra("dui")



        Nombre_antesEmpleado=findViewById(R.id.EditarEmpleado_name_antes)
        Dui_antesEmpleado=findViewById(R.id.EditarEmpleado_Dui_antes)
        Departamento_antesEmpleado=findViewById(R.id.EditarEmpleado_depa_antes)

        Nombre_antesEmpleado.setText(Nombre_Empleado)
        Dui_antesEmpleado.setText(Dui_Empleado)
        Departamento_antesEmpleado.setText(Departamento_Empleado)

        val conn = ConnectSql().dbConn()
        val Depart = ArrayList<String>()

        //Busca el spinner por el id
        spinner = findViewById(R.id.EditarEmpleado_depa)

        try {
            //Aca hacemos una consulta de toda a vida a sql con ayuda de la conexion que hicimos antes
            val statement = conn?.createStatement()
            val query = "SELECT Departamentos FROM tbDepartamentos"
            //Vamo a decir que el resultset va a ser igual a el resultado de lo que encontro en la base
            val resultSet = statement?.executeQuery(query)

            //Agregamos este texto a el array para que salga de primero
            Depart.add("Nuevo departamento...")

            //Ahora, por cada fila encontrada en la consulta va a repetir este proceso
            while (resultSet?.next() == true) {
                //Busca en la columna clasificacion
                val Departamentos = resultSet.getString("Departamentos")
                //La pone en e array que hicimos antes
                Depart.add(Departamentos)
            }
            val adapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, Depart)

            //Y luego, finalmente ponemos los elementos en e adaptador (los textView que creamos antes) y los ponemos en el spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            //Cerramos las conexiones
            resultSet?.close()
            statement?.close()
        }catch (ex: SQLException) {
            println(ex.message)
        }

        lblEmpleados=findViewById(R.id.EditarEmpleado_name)
        lblDui=findViewById(R.id.EditarDui_Dui)

        btnActualizarEmpleado=findViewById(R.id.EditarEmpleado_actualizar)

        btnActualizarEmpleado.setOnClickListener {
            //Verificamos que los cammpos no sean los de por defecto
            if(lblDui.text.toString()!="" && lblEmpleados.text.toString()!="" && spinner.selectedItem.toString()!="Nueva categoria..."){
                try {
                    val addEmpleados: PreparedStatement =  conn?.prepareStatement(

                        //Hacemos la query de inserción, con los datos que queramos insertar
                        "UPDATE TbEmpleados\n" +
                                "SET idagregar = c.idagregar, nombre = ?, dui = ?\n" +
                                "FROM TbEmpleados p\n" +
                                "JOIN TbDepartamentos c ON p.idagregar = c.idagregar\n" +
                                "WHERE p.idempleados = 1\n" +
                                "AND c.Departamentos = ?;"

                    )!!

                    addEmpleados.setString(1, lblEmpleados.text.toString())
                    addEmpleados.setString(2, lblDui.text.toString())
                    addEmpleados.setString(3, spinner.selectedItem.toString())
                    addEmpleados.executeUpdate()

                    Toast.makeText(this, "Se ha actualizado correctamente", Toast.LENGTH_SHORT).show()

                    setResult(Activity.RESULT_OK, Intent())
                    conn.close()
                    finish()
                }  catch (ex: SQLException){
                    Toast.makeText(this, "Error al ingresar: "+ex, Toast.LENGTH_SHORT).show()
                    println(ex)
                    setResult(Activity.RESULT_OK, Intent())
                    conn?.close()
                }
            } else{
                Toast.makeText(this, "Rellene todos los campo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}