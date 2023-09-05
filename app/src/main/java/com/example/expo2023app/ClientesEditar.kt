package com.example.expo2023app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.sql.PreparedStatement
import java.sql.SQLException

private lateinit var lblClientesNom: EditText
private lateinit var lblTelefono: EditText
private lateinit var btnActualizarClientes: Button

private lateinit var spinner: Spinner

//Valores anteriores
private lateinit var Clientes_antes: TextView
private lateinit var telefonos_antes: TextView
private lateinit var DepartamentosClientes_antes: TextView

class ClientesEditar : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes_editar)

        // Obtener los parámetros del intent
        val nombreCliente_got = intent.getStringExtra("nomb")
        val DepartamentoCliente_got = intent.getStringExtra("depa")
        val TelefonoCliente_got = intent.getStringExtra("cel")

        Clientes_antes=findViewById(R.id.Editarcliente_name_antes)
        telefonos_antes=findViewById(R.id.EditarCliente_Telefono_antes)
        DepartamentosClientes_antes=findViewById(R.id.EditarCliente_Departamento_antes)

        Clientes_antes.setText(nombreCliente_got)
        telefonos_antes.setText(DepartamentoCliente_got)
        DepartamentosClientes_antes.setText(TelefonoCliente_got)

        //Empezamos con mandar a llamar la conexion
        val conn = ConnectSql().dbConn()
        //Hacemos un array para  ponerlas en un spinner
        val Departamentos = ArrayList<String>()

        //Busca el spinner por el id
        spinner = findViewById(R.id.EditarCliente_Departamento)

        try {
            //Aca hacemos una consulta de toda a vida a sql con ayuda de la conexion que hicimos antes
            val statement = conn?.createStatement()
            val query = "SELECT Departementos FROM DepartamentosClientes"
            //Vamo a decir que el resultset va a ser igual a el resultado de lo que encontro en la base
            val resultSet = statement?.executeQuery(query)

            //Agregamos este texto a el array para que salga de primero
            Departamentos.add("Nuevo Departamento...")

            //Ahora, por cada fila encontrada en la consulta va a repetir este proceso
            while (resultSet?.next() == true) {
                //Busca en la columna clasificacion
                val depa = resultSet.getString("Departementos")
                //La pone en e array que hicimos antes
                Departamentos.add(depa)
            }

            //Ahora hacemos el adaptador de el spinner/dropBox

            //Vamos a hacer que por cada elemento en el array que hicimos antes, que se cree un TextView personalizado y
            //que este, se ponga en el spinner como elemento

            val adapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, Departamentos)

            //Y luego, finalmente ponemos los elementos en e adaptador (los textView que creamos antes) y los ponemos en el spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            //Cerramos las conexiones
            resultSet?.close()
            statement?.close()
        }catch (ex: SQLException) {
            println(ex.message)
        }


        //Para subir los datos a la base

        lblClientesNom=findViewById(R.id.EditarCliente_name)
        lblTelefono=findViewById(R.id.EditarCliente_Telefono)

        btnActualizarClientes=findViewById(R.id.EditarCliente_actualizar)

        btnActualizarClientes.setOnClickListener {
            //Verificamos que los cammpos no sean los de por defecto
            if(lblClientesNom.text.toString()!="" && lblTelefono.text.toString()!="" && spinner.selectedItem.toString()!="Nuevo Departamento..."){
                try {
                    val addClientes: PreparedStatement =  conn?.prepareStatement(

                        //Hacemos la query de inserción, con los datos que queramos insertar
                        "UPDATE Tbcliente\n" +
                                "SET iddepartamentocliente = c.iddepartamentocliente, nombreCliente = ?, telefonoCliente = ?\n" +
                                "FROM Tbcliente p\n" +
                                "JOIN DepartamentosClientes c ON p.iddepartamentocliente = c.iddepartamentocliente\n" +
                                "WHERE p.idcliente = 1\n" +
                                "AND c.Departementos = ?;"
                    )!!

                    addClientes.setString(1, lblClientesNom.text.toString())
                    addClientes.setString(2, lblTelefono.text.toString())
                    addClientes.setString(3, spinner.selectedItem.toString())
                    addClientes.executeUpdate()

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