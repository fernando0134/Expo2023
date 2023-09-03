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
import android.widget.Toast
import java.sql.PreparedStatement
import java.sql.SQLException

private lateinit var lblClientesNom: EditText
private lateinit var lblTelefonos: EditText
private lateinit var btnAgregarClientes: Button


private lateinit var spinner: Spinner

class Clientes : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        //Empezamos con mandar a llamar la conexion
        val conn = ConnectSql().dbConn()

        val Departamentos = ArrayList<String>()

        spinner = findViewById(R.id.AgregarCliente_Departamento)

        try {
            //Aca hacemos una consulta de toda a vida a sql con ayuda de la conexion que hicimos antes
            val statement = conn?.createStatement()
            val query = "SELECT Departementos FROM DepartamentosClientes"
            //Vamo a decir que el resultset va a ser igual a el resultado de lo que encontro en la base
            val resultSet = statement?.executeQuery(query)

            //Agregamos este texto a el array para que salga de primero
            Departamentos.add("Seleccione una opcion...")

            //Ahora, por cada fila encontrada en la consulta va a repetir este proceso
            while (resultSet?.next() == true) {
                //Busca en la columna clasificacion
                val depart = resultSet.getString("Departamentos")
                //La pone en e array que hicimos antes
                Departamentos.add(depart)
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
        } catch (ex: SQLException) {
            println(ex.message)
        }


        //Para subir los datos a la base

        lblClientesNom=findViewById(R.id.AgregarCliente_name)
        lblTelefonos=findViewById(R.id.AgregarCliente_Telefono)

        btnAgregarClientes=findViewById(R.id.AgregarCliente_agregar)

        btnAgregarClientes.setOnClickListener {
            //Verificamos que los cammpos no sean los de por defecto
            if(lblTelefonos.text.toString()!="" && lblClientesNom.text.toString()!="" && spinner.selectedItem.toString()!="Seleccione una opcion..."){
                try {
                    val addClientes: PreparedStatement =  conn?.prepareStatement(

                        //Hacemos la query de inserción, con los datos que queramos insertar
                        "INSERT INTO Tbcliente ((iddepartamentocliente,  nombreCliente, telefonoCliente)\n" +
                                //Vamos a declarar que insertaremos los datos que se desglocen en una consulta
                                //Osea, se va a insertar lo que salga como resultado de una consulta
                                //Uno de los parametros que queremos que salgan seria el IdClasificacion, asi que mandaremos a llamar a este haciendo referencia a la
                                //tabla /c/ la cual posteriormente declaramos que es /tbClasificacion/
                                //Asi que vamos a insertar los datos que queremos que ya tenemos los cuales queremos que salgan como parametros /?/
                                "SELECT c.iddepartamentocliente, ?, ?, ?\n" +
                                //Abreviamos la tabla como /c/
                                "FROM DepartamentosClientes c\n" +
                                //Tomamos de parametro para mostrar el id en la consulta que queremos que salga
                                "WHERE c.Departementos = ?;"
                    )!!

                    addClientes.setString(1, lblClientesNom.text.toString())
                    addClientes.setString(2, lblTelefonos.text.toString())

                    addClientes.setString(3, spinner.selectedItem.toString())
                    addClientes.executeUpdate()

                    Toast.makeText(this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()

                    setResult(Activity.RESULT_OK, Intent())
                    conn.close()
                    finish()
                } catch (ex: SQLException){
                    Toast.makeText(this, "Error al ingresar: "+ex, Toast.LENGTH_SHORT).show()
                    println(ex)
                    setResult(Activity.RESULT_OK, Intent())
                    conn?.close()
                }
            }else{
                Toast.makeText(this, "Rellene todos los campo", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onBackPressed() {
        finish()
    }
}