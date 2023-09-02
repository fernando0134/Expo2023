package com.example.expo2023app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.sql.PreparedStatement
import java.sql.SQLException

//definimos las variables publicas que nos van a ayudar

//este va a ser el boton que defina si quiere subir una foto
private lateinit var SubirFoto1 : TextView
//Y este si quiere tomar la foto desde la camara
private lateinit var TomarFoto1 : TextView
//Este va a ser el ImageView en el que se podra observar que imagen va a subir
private lateinit var Imagen: ImageView

//Esta va a ser la imagen que se almacene pero en forma de arreglo de bits para manipuarlo desde codigo
private var foto: ByteArray? = null
//Esto va a ser lo que va a definir si el usuario selecciono la opcion de tomar la imagen de archivos
//O tomarla desde la camara
private val PICK_IMAGE_REQUEST = 1
private val REQUEST_IMAGE_CAPTURE = 2

//Los demas campos de texto que usamos para poner el texto
@SuppressLint("StaticFieldLeak")
private lateinit var lblEmpleados1Nom: EditText
private lateinit var lblDui1: EditText
private lateinit var btnActualizar: Button

private lateinit var spinner: Spinner

//Valores anteriores
private lateinit var Nombre_antes: TextView
private lateinit var Dui_antes: TextView
private lateinit var Departamento_antes: TextView

class EditarEmpleados : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_empleados)

        val nombre_got = intent.getStringExtra("nomb")
        val departamento_got = intent.getStringExtra("depa")
        val dui_got = intent.getStringExtra("dui")
        val foto_got = intent.getByteArrayExtra("foto") // Recuperar el ByteArray

        if (foto_got != null && foto_got.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(foto_got, 0, foto_got.size)
            val imageView = findViewById<ImageView>(R.id.EditarEmpleado_foto)
            imageView.setImageBitmap(bitmap)
        }

        Nombre_antes=findViewById(R.id.EditarEmpleado_name_antes)
        Dui_antes=findViewById(R.id.EditarEmpleado_Dui_antes)
        Departamento_antes=findViewById(R.id.EditarEmpleado_depa_antes)

        Nombre_antes.setText(nombre_got)
        Dui_antes.setText(dui_got)
        Departamento_antes.setText(departamento_got)


        //Empezamos con mandar a llamar la conexion
        val conn = ConnectSql().dbConn()
        //Hacemos un array para contener las clasificaciones de los productos y ponerlas en un spinner
        val depa1 = ArrayList<String>()

        //Busca el spinner por el id
        spinner = findViewById(R.id.EditarEmpleado_depa)



        try {
            //Aca hacemos una consulta de toda a vida a sql con ayuda de la conexion que hicimos antes
            val statement = conn?.createStatement()
            val query = "SELECT Departamentos FROM TbDepartamentos"
            //Vamo a decir que el resultset va a ser igual a el resultado de lo que encontro en la base
            val resultSet = statement?.executeQuery(query)

            //Agregamos este texto a el array para que salga de primero
            depa1.add("Nuevo departamento...")

            //Ahora, por cada fila encontrada en la consulta va a repetir este proceso
            while (resultSet?.next() == true) {
                //Busca en la columna clasificacion
                val Depa = resultSet.getString("departamento")
                //La pone en e array que hicimos antes
                depa1.add(Depa)
            }

            //Ahora hacemos el adaptador de el spinner/dropBox

            //Vamos a hacer que por cada elemento en el array que hicimos antes, que se cree un TextView personalizado y
            //que este, se ponga en el spinner como elemento


            val adapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, depa1)

            //Y luego, finalmente ponemos los elementos en e adaptador (los textView que creamos antes) y los ponemos en el spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            //Cerramos las conexiones
            resultSet?.close()
            statement?.close()
        }catch (ex: SQLException) {
            println(ex.message)
        }

        //Buscamos los elementos que vamos a ocupar por su id



        //Para subir los datos a la base

        lblEmpleados1Nom=findViewById(R.id.EditarEmpleado_name)
        lblDui1=findViewById(R.id.EditarDui_Dui)

        btnActualizar=findViewById(R.id.EditarEmpleado_actualizar)


        btnActualizar.setOnClickListener {
            //Verificamos que los cammpos no sean los de por defecto
            if(lblDui1.text.toString()!="" && lblEmpleados1Nom.text.toString()!="" && spinner.selectedItem.toString()!="Nuevo Empleado..."){
                try {
                    val addEmpleados: PreparedStatement =  conn?.prepareStatement(

                        //Hacemos la query de inserción, con los datos que queramos insertar
                        "UPDATE TbEmpleados\n" +
                                "SET idagregar = c.idagregar, nombre_Empleado = ?, dui = ?, fotoempleado = ?\n" +
                                "FROM TbEmpleados p\n" +
                                "JOIN TbDepartamentos c ON p.idagregar = c.idagregar\n" +
                                "WHERE p.idEmpleados = 28\n" +
                                "AND c.Departamentos = ?;"

                    )!!

                    addEmpleados.setString(1, lblEmpleados1Nom.text.toString())
                    addEmpleados.setString(2, lblDui1.text.toString())
                    addEmpleados.setBytes(3, foto)
                    addEmpleados.setString(4, spinner.selectedItem.toString())
                    addEmpleados.executeUpdate()

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
}