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



//Los  campos de texto que usamos pa poner el texto
@SuppressLint("StaticFieldLeak")
private lateinit var lblEmpleadooNom:EditText
private lateinit var lblDui:EditText
private lateinit var btnAgregarEmpleado:Button
private  lateinit var  btnVer: Button

private lateinit var spinner:Spinner


class Empleados : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empleados)

        //Para crear la funcion de agregar en el crud
        //Empezamos con mandar a llamar la conexion
        val conn = ConnectSql().dbConn()
        //Hacemos un array para contener las clasificaciones de los productos y ponerlas en un spinner
        val Depart = ArrayList<String>()

        ////Buscamos el spinner por el id
        spinner = findViewById(R.id.AgregarEmpleado_Departamento)

        try {
            //Aca hacemos una consulta  a sql con ayuda de la conexion que hicimos antes
            val statement = conn?.createStatement()
            val query = "SELECT Departamentos FROM TbDepartamentos"
            //Vamo a decirle que el resultset va a ser igual a el resultado de lo que encontro en la base
            val resultSet = statement?.executeQuery(query)

            //Agregamos este texto a el array para que salga de primero
            Depart.add("Seleccione una opcion...")

            //Ahora, por cada fila encontrada en la consulta va a repetir este proceso
            while (resultSet?.next() == true) {
                //Busca en la columna Departamentos
                val Departamentos = resultSet.getString("Departamentos")
                //La pone en e array que hicimos antes
                Depart.add(Departamentos)
            }

            //Ahora hacemos el adaptador de el spinner/dropBox

            //Vamos a hacer que por cada elemento en el array que hicimos antes, que se cree un TextView personalizado y
            //que este, se ponga en el spinner como elemento

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



        //Para subir los datos a la base

        lblEmpleadooNom=findViewById(R.id.AgregarEmpleado_name)
        lblDui=findViewById(R.id.AgregarEmpleado_Dui)
        btnAgregarEmpleado=findViewById(R.id.AgregarEmpleados_agregar)
        btnVer = findViewById(R.id.AgregarEmpleados_Ver)

        btnAgregarEmpleado.setOnClickListener {
            //Verificamos que los cammpos no sean los de por defecto
            if(lblDui.text.toString()!="" && lblEmpleadooNom.text.toString()!="" && spinner.selectedItem.toString()!="Seleccione una opcion..."){
                try {
                    val addProducto: PreparedStatement =  conn?.prepareStatement(

                        //Hacemos la query de inserción, con los datos que queramos insertar
                        "INSERT INTO TbEmpleados (idagregar, nombre, dui)\n" +
                                //Vamos a insertar lo que salga como resultado de una consulta
                                //Uno de los parametros que queremos que salgan seria el idagregar, asi que mandaremos a llamar a este haciendo referencia a la
                                //tabla /c/ la cual posteriormente declaramos que es /TbDepartamentos/
                                //Asi que vamos a insertar los datos que queremos que ya tenemos los cuales queremos que salgan como parametros /?/
                                "SELECT c.idagregar, ?, ?\n" +
                                //Abreviamos la tabla como /c/
                                "FROM TbDepartamentos c\n" +
                                //Tomamos de parametro el nombre de la clasificacion para mostrar el id en la consulta que queremos que salga
                                "WHERE c.Departamentos = ?;"

                        //hicimos esto debido a que en android, al hacer demasiadas consultas suele petar el sistema, asi que vamos a optar por una insercion mas limpia
                        //la cual hace el proceso de buscar el id del departamento por el mismo nombre desde la base de datos y no desde la aplicacion

                    )!!

                    addProducto.setString(1, lblEmpleadooNom.text.toString())
                    addProducto.setString(2, lblDui.text.toString())
                    addProducto.setString(3, spinner.selectedItem.toString())
                    addProducto.executeUpdate()

                    Toast.makeText(this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()
                    val inicio: Intent = Intent(this, VistaEmpleados::class.java)
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

        btnVer.setOnClickListener {
            val inicio: Intent = Intent(this, VistaEmpleados::class.java)
            startActivity(inicio)
        }

    }
}