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

//este va a ser el boton que defina si quiere subir una foto
private lateinit var SubirFotoEmpleado : TextView
//Y este si quiere tomar la foto desde la camara
private lateinit var TomarFotoEmpleado : TextView
//Este va a ser el ImageView en el que se podra observar que imagen va a subir
private lateinit var ImagenEmpleado: ImageView

//Esta va a ser la imagen que se almacene pero en forma de arreglo de bits para manipuarlo desde codigo
private var foto: ByteArray? = null
//Esto va a ser lo que va a definir si el usuario selecciono la opcion de tomar la imagen de archivos
//O tomarla desde la camara
private val PICK_IMAGE_REQUEST = 1
private val REQUEST_IMAGE_CAPTURE = 2

//Los demas campos de texto que usamos pa poner el texto
@SuppressLint("StaticFieldLeak")
private lateinit var lblEmpleadooNom:EditText
private lateinit var lblDui:EditText
private lateinit var btnAgregarEmpleado:Button

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

        //Buscamos los elementos que vamos a ocupar por su id
        SubirFotoEmpleado =findViewById(R.id.AgregarEmpleado_SubirFoto)
        TomarFotoEmpleado =findViewById(R.id.AgregarEmpleados_TomarFoto)
        ImagenEmpleado =findViewById(R.id.AgregarEmpleado_foto)


        val drawable = ImagenEmpleado.drawable
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
        foto = stream.toByteArray()

        //Hacemos que si presiona el boton de subir foto, pase el parametro de
        //tomar foto, como falso
        SubirFotoEmpleado.setOnClickListener {
            getIMG(this, false)
        }
        //Y si quiere tomar la foto pues el parametro va a ser verdadero
        TomarFotoEmpleado.setOnClickListener {
            getIMG(this, true)
        }


        //Para subir los datos a la base

        lblEmpleadooNom=findViewById(R.id.AgregarEmpleado_name)
        lblDui=findViewById(R.id.AgregarEmpleado_Dui)
        btnAgregarEmpleado=findViewById(R.id.AgregarEmpleados_agregar)

        btnAgregarEmpleado.setOnClickListener {
            //Verificamos que los cammpos no sean los de por defecto
            if(lblDui.text.toString()!="" && lblEmpleadooNom.text.toString()!="" && spinner.selectedItem.toString()!="Seleccione una opcion..."){
                try {
                    val addProducto: PreparedStatement =  conn?.prepareStatement(

                        //Hacemos la query de inserción, con los datos que queramos insertar
                        "INSERT INTO TbEmpleados (idagregar, nombre_Empleado, dui, fotoempleado)\n" +
                                //Vamos a insertar lo que salga como resultado de una consulta
                                //Uno de los parametros que queremos que salgan seria el idagregar, asi que mandaremos a llamar a este haciendo referencia a la
                                //tabla /c/ la cual posteriormente declaramos que es /TbDepartamentos/
                                //Asi que vamos a insertar los datos que queremos que ya tenemos los cuales queremos que salgan como parametros /?/
                                "SELECT c.idagregar, ?, ?, ?\n" +
                                //Abreviamos la tabla como /c/
                                "FROM TbDepartamentos c\n" +
                                //Tomamos de parametro el nombre de la clasificacion para mostrar el id en la consulta que queremos que salga
                                "WHERE c.Departamentos = ?;"

                        //hicimos esto debido a que en android, al hacer demasiadas consultas suele petar el sistema, asi que vamos a optar por una insercion mas limpia
                        //la cual hace el proceso de buscar el id del departamento por el mismo nombre desde la base de datos y no desde la aplicacion

                    )!!

                    addProducto.setString(1, lblEmpleadooNom.text.toString())
                    addProducto.setString(2, lblDui.text.toString())
                    addProducto.setBytes(3, foto)
                    addProducto.setString(4, spinner.selectedItem.toString())
                    addProducto.executeUpdate()

                    Toast.makeText(this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()

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

    private fun getIMG(activity: Activity, capturarFoto: Boolean) {
        val intent: Intent
        if (capturarFoto) {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        } else {
            intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
        }
        //Luego, se ejecuta un /Intent for activity result/ El cual como su nombre indica
        //Esque va a abrir un intent el cual si o si va a tener que devolver algo, en este caso, le pasamos de parametro tambien un if
        //Que indica que si escogio capturar la foto, va a tomar como parametro de recuperacion del dato como un /1/ ya que antes defnimos que "REQUEST_IMAGE_CAPTURE" = /1/
        //Y si escogio NO capturar la foto, sino que tomara de los archivos, pasara como parametro de recuperacion un /2/ ya que antes definimos que "PICK_IMAGE_REQUEST" = /2/
        activity.startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), if (capturarFoto) REQUEST_IMAGE_CAPTURE else PICK_IMAGE_REQUEST)
    }


    //ahora hacemos que la activity se quede en reposo hasta que en la siguente pantalla retorne algo
    //tomando como parametro el parametro de recuperacion, la accion de /onBacKPressed/ que seria el equivalente a darle a la flechita de atras
    //la cual en codigo, seria /RESULT_OK/, osea: /RESULT_OK/ retroceder en una activity, y como ultimo parametro, el intent que abrio

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Si el parametro de recuperacion es el mismo que el de tomar foto, y se haya detectado que se volvio de una activity siguiente, a
        //La activity acual, se ejecutara el siguiente cogigo
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val imageBitmap = data?.extras?.get("data") as Bitmap

            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageByteArray = byteArrayOutputStream.toByteArray()
            foto = imageByteArray
            ImagenEmpleado.setImageBitmap(imageBitmap)
        }

        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) { //se obtiene e Uri de la imagen que escogimos en los archivos
            val imageUri = data.data
            val inputStream = contentResolver.openInputStream(imageUri!!)
            val imageByteArray = inputStream?.readBytes()
            if (imageByteArray != null) {
                foto = imageByteArray
            }
            ImagenEmpleado.setImageURI(imageUri)
        }
    }
}