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
private lateinit var btnVer : Button


private lateinit var spinner: Spinner

class Clientes : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        //Empezamos con mandar a llamar la conexion
        val conn = ConnectSql().dbConn()

        val Departamentos1 = ArrayList<String>()

        spinner = findViewById(R.id.AgregarCliente_Departamento)

        try {

            val statement = conn?.createStatement()
            val query = "SELECT Departamentos FROM TbDepartamentos"
            val resultSet = statement?.executeQuery(query)

            Departamentos1.add("Seleccione una opcion...")


            while (resultSet?.next() == true) {

                val Depa = resultSet.getString("Departamentos")

                Departamentos1.add(Depa)
            }


            val adapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, Departamentos1)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            resultSet?.close()
            statement?.close()
        }catch (ex: SQLException) {
            println(ex.message)
        }


        //Para subir los datos a la base

        lblClientesNom=findViewById(R.id.AgregarCliente_name)
        lblTelefonos=findViewById(R.id.AgregarCliente_Telefono)

        btnAgregarClientes=findViewById(R.id.AgregarCliente_agregar)
        btnVer = findViewById(R.id.Agregarcliente_Ver)


        btnAgregarClientes.setOnClickListener {
            //Verificamos que los cammpos no sean los de por defecto
            if(lblTelefonos.text.toString()!="" && lblClientesNom.text.toString()!="" && spinner.selectedItem.toString()!="Seleccione una opcion..."){
                try {
                    val addClientes: PreparedStatement =  conn?.prepareStatement(

                        //Hacemos la query de inserción, con los datos que queramos insertar
                        "INSERT INTO Tbcliente (iddepartamentocliente,  nombreCliente, telefonoCliente)\n" +
                                //Vamos a declarar que insertaremos los datos que se desglocen en una consulta
                                //Osea, se va a insertar lo que salga como resultado de una consulta
                                //Uno de los parametros que queremos que salgan seria el IdClasificacion, asi que mandaremos a llamar a este haciendo referencia a la
                                //tabla /c/ la cual posteriormente declaramos que es /tbClasificacion/
                                //Asi que vamos a insertar los datos que queremos que ya tenemos los cuales queremos que salgan como parametros /?/
                                "SELECT c.iddepartamentocliente, ?, ?\n" +
                                //Abreviamos la tabla como /c/
                                "FROM DepartamentosClientes c\n" +
                                //Tomamos de parametro para mostrar el id en la consulta que queremos que salga
                                "WHERE c.Departementos = ?;"
                    )!!

                    val inicio: Intent = Intent(this, VistaCliente::class.java)
                    addClientes.setString(1, lblClientesNom.text.toString())
                    addClientes.setString(2, lblTelefonos.text.toString())
                    addClientes.setString(3, spinner.selectedItem.toString())
                    addClientes.executeUpdate()

                    Toast.makeText(this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()

                    setResult(Activity.RESULT_OK, Intent())
                    conn.close()
                    startActivity(inicio)
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

        btnVer.setOnClickListener {
            val inicio: Intent = Intent(this, VistaCliente::class.java)
            startActivity(inicio)
        }
    }
    override fun onBackPressed() {
        finish()
    }
}