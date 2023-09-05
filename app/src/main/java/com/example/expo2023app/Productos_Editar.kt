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

//definimos las variables publicas que nos van a ayudar

//este va a ser el boton que defina si quiere subir una foto
private lateinit var SubirFoto : TextView
//Y este si quiere tomar la foto desde la camara
private lateinit var TomarFoto : TextView
//Este va a ser el ImageView en el que se podra observar que imagen va a subir
private lateinit var Imagen: ImageView

//Esta va a ser la imagen que se almacene pero en forma de arreglo de bits para manipuarlo desde codigo
private var foto: ByteArray? = null
//Esto va a ser lo que va a definir si el usuario selecciono la opcion de tomar la imagen de archivos
//O tomarla desde la camara
private val PICK_IMAGE_REQUEST = 1
private val REQUEST_IMAGE_CAPTURE = 2

//Los demas campos de texto que usamos pa poner el texto
@SuppressLint("StaticFieldLeak")
private lateinit var lblProductoNom: EditText
private lateinit var lblPrecio: EditText
private lateinit var btnActualizar: Button


private lateinit var spinner: Spinner

//Valores anteriores
private lateinit var producto_antes: TextView
private lateinit var precio_antes: TextView
private lateinit var categoria_antes:TextView
class Productos_Editar : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos_editar)

        // Obtener los parámetros del intent
        val nombre_got = intent.getStringExtra("nomb")
        val clasificacion_got = intent.getStringExtra("cat")
        val price_got = intent.getStringExtra("price")
        val foto_got = intent.getByteArrayExtra("foto") // Recuperar el ByteArray

        if (foto_got != null && foto_got.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(foto_got, 0, foto_got.size)
            val imageView = findViewById<ImageView>(R.id.EditarProducto_foto)
            imageView.setImageBitmap(bitmap)
        }

        producto_antes=findViewById(R.id.EditarProducto_name_antes)
        precio_antes=findViewById(R.id.EditarProducto_price_antes)
        categoria_antes=findViewById(R.id.EditarProducto_cat_antes)

        producto_antes.setText(nombre_got)
        precio_antes.setText(price_got)
        categoria_antes.setText(clasificacion_got)



        //Empezamos con mandar a llamar la conexion
        val conn = ConnectSql().dbConn()
        //Hacemos un array para contener las clasificaciones de los productos y ponerlas en un spinner
        val clasificaciones = ArrayList<String>()

        //Busca el spinner por el id
        spinner = findViewById(R.id.EditarProducto_cat)

        try {
            //Aca hacemos una consulta de toda a vida a sql con ayuda de la conexion que hicimos antes
            val statement = conn?.createStatement()
            val query = "SELECT clasificacion FROM tbClasificacion"
            //Vamo a decir que el resultset va a ser igual a el resultado de lo que encontro en la base
            val resultSet = statement?.executeQuery(query)

            //Agregamos este texto a el array para que salga de primero
            clasificaciones.add("Nueva categoria...")

            //Ahora, por cada fila encontrada en la consulta va a repetir este proceso
            while (resultSet?.next() == true) {
                //Busca en la columna clasificacion
                val clasificacion = resultSet.getString("clasificacion")
                //La pone en e array que hicimos antes
                clasificaciones.add(clasificacion)
            }

            //Ahora hacemos el adaptador de el spinner/dropBox

            //Vamos a hacer que por cada elemento en el array que hicimos antes, que se cree un TextView personalizado y
            //que este, se ponga en el spinner como elemento

            //pd. Si no quieres usar un adaptador personalizado, puedes usar el de por defecto:
            //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, clasificaciones)

            val adapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, clasificaciones)

            //Y luego, finalmente ponemos los elementos en e adaptador (los textView que creamos antes) y los ponemos en el spinner
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            //Cerramos las conexiones
            resultSet?.close()
            statement?.close()
        } catch (ex: SQLException) {
            println(ex.message)
        }

        //Buscamos los elementos que vamos a ocupar por su id
        SubirFoto=findViewById(R.id.EditarProducto_SubirFoto)
        TomarFoto=findViewById(R.id.EditarProducto_TomarFoto)
        Imagen=findViewById(R.id.EditarProducto_foto)

        val drawable = Imagen.drawable
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
        foto = stream.toByteArray()

        //Hacemos que si presiona el boton de subir foto, pase el parametro de
        //tomar foto, como falso
        SubirFoto.setOnClickListener {
            getIMG(this, false)
        }
        //Y si quiere tomar la foto pues el parametro va a ser verdadero
        TomarFoto.setOnClickListener {
            getIMG(this, true)
        }

        lblProductoNom=findViewById(R.id.EditarProducto_name)
        lblPrecio=findViewById(R.id.EditarProducto_price)

        btnActualizar=findViewById(R.id.EditarProducto_actualizar)


        btnActualizar.setOnClickListener {
            //Verificamos que los cammpos no sean los de por defecto
            if(lblPrecio.text.toString()!="" && lblProductoNom.text.toString()!="" && spinner.selectedItem.toString()!="Nueva categoria..."){
                try {
                    val addProducto: PreparedStatement =  conn?.prepareStatement(

                        //Hacemos la query de inserción, con los datos que queramos insertar
                        "UPDATE tbProductos\n" +
                                "SET IdClasificacion = c.IdClasificacion, nombre = ?, precioUnit = ?, foto = ?\n" +
                                "FROM tbProductos p\n" +
                                "JOIN tbClasificacion c ON p.IdClasificacion = c.IdClasificacion\n" +
                                "WHERE p.IdProducto = 28\n" +
                                "AND c.clasificacion = ?;"

                    )!!

                    addProducto.setString(1, lblProductoNom.text.toString())
                    addProducto.setString(2, lblPrecio.text.toString())
                    addProducto.setBytes(3, foto)
                    addProducto.setString(4, spinner.selectedItem.toString())
                    addProducto.executeUpdate()

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

    //hacemos una funcion la cual diferencie que boton presiono el usuario
    private fun getIMG(activity: Activity, capturarFoto: Boolean) {
        //Hacemos la variable de un intent para abrir la pantalla correspondiente
        //a lo que desee e usuario
        val intent: Intent
        if (capturarFoto) {
            //En este caso, lo que se ejecutara sera la camara con ayuda del intent anterior
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        } else {
            //Y en este caso, se abrira el explorador de archivos
            intent = Intent(Intent.ACTION_GET_CONTENT)
            //En el cual solo podra seleccionar imagenes
            intent.type = "image/*"
        }
        //Luego, se ejecuta un /Intent for activity result/ El cual como su nombre indica
        //Esque va a abrir un intent el cual si o si va a tener que devolver algo, en este caso, le pasamos de parametro tambien un if
        //Que indica que si escogio capturar la foto, va a tomar como parametro de recuperacion del dato como un /1/ ya que antes defnimos que "REQUEST_IMAGE_CAPTURE" = /1/
        //Y si escogio NO capturar la foto, sino que tomara de archvos, pasara como parametro de recuperacion un /2/ ya que antes definimos que "PICK_IMAGE_REQUEST" = /2/
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
            //Se obtiente la imagen que se tomo como un parametro extra con el nombre de referencia "data" y se convierte a bitmaps
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Obtener el arreglo de bytes de la imagen
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageByteArray = byteArrayOutputStream.toByteArray()
            // Asignar el arreglo de bytes a la variable 'foto'
            foto = imageByteArray
            // Mostrar la imagen en el ImageView
            Imagen.setImageBitmap(imageBitmap)
        }

        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            //se obtiene e Uri de la imagen que escogimos en los archivos
            val imageUri = data.data
            // Obtener el arreglo de bytes de la imagen seleccionada
            val inputStream = contentResolver.openInputStream(imageUri!!)
            val imageByteArray = inputStream?.readBytes()
            // Asignar el arreglo de bytes a la variable 'foto'
            if (imageByteArray != null) {
                foto = imageByteArray
            }
            // Mostrar la imagen en el ImageView
            Imagen.setImageURI(imageUri)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
