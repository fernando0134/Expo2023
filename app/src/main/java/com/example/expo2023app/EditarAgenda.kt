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

class EditarAgenda : AppCompatActivity() {


    private lateinit var SubirFoto : TextView

    private lateinit var TomarFoto : TextView

    private lateinit var Imagen: ImageView

    private var foto: ByteArray? = null

    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 2

    private lateinit var Masaje1: EditText
    private lateinit var Precio: EditText
    private lateinit var btnActualizar: Button

    private lateinit var spinner: Spinner


    private lateinit var producto_antes:TextView
    private lateinit var precio_antes:TextView
    private lateinit var categoria_antes:TextView

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_agenda)



        val nombre_got = intent.getStringExtra("nomb")
        val clasificacion_got = intent.getStringExtra("cat")
        val price_got = intent.getStringExtra("price")
        val foto_got = intent.getByteArrayExtra("foto")

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

        val conn = ConnectSql().dbConn()

        val masajistas = ArrayList<String>()


        spinner = findViewById(R.id.EditarMasajita)
        try {

            val statement = conn?.createStatement()
            val query = "SELECT masajes FROM Tbcitas"

            val resultSet = statement?.executeQuery(query)

            masajistas.add("Nueva informacion...")


            while (resultSet?.next() == true) {
                //
                val TipoMasaje1 = resultSet.getString("masajes")
                masajistas.add(TipoMasaje1)
            }


            val adapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, masajistas)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            resultSet?.close()
            statement?.close()
        }catch (ex: SQLException) {
            println(ex.message)
        }

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


        Masaje1=findViewById(R.id.EditarMasaje)
        Precio=findViewById(R.id.EditarPrecio)

        btnActualizar=findViewById(R.id.Actualizar)

        btnActualizar.setOnClickListener {
            if(Precio.text.toString()!="" && Masaje1.text.toString()!="" && spinner.selectedItem.toString()!="Nueva categoria..."){
                try {
                    val addMasajes: PreparedStatement =  conn?.prepareStatement(


                        "UPDATE TbMasajes\n" +
                                "SET IdMasajes = c.idcita, nombre = ?, precioUnit = ?, foto = ?\n" +
                                "FROM TbMasajes p\n" +
                                "JOIN Tbcitas c ON p.idcita = c.idcita\n" +
                                "WHERE p.IdMasajes = 28\n" +
                                "AND c.masajes = ?;"

                    )!!

                    addMasajes.setString(1, Masaje1.text.toString())
                    addMasajes.setString(2, Precio.text.toString())
                    addMasajes.setBytes(3, foto)
                    addMasajes.setString(4, spinner.selectedItem.toString())
                    addMasajes.executeUpdate()

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


    private fun getIMG(activity: Activity, capturarFoto: Boolean) {

        val intent: Intent
        if (capturarFoto) {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        } else {

            intent = Intent(Intent.ACTION_GET_CONTENT)

            intent.type = "image/*"
        }
        activity.startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), if (capturarFoto) REQUEST_IMAGE_CAPTURE else PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageByteArray = byteArrayOutputStream.toByteArray()

            foto = imageByteArray

            Imagen.setImageBitmap(imageBitmap)
        }

        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            val imageUri = data.data

            val inputStream = contentResolver.openInputStream(imageUri!!)
            val imageByteArray = inputStream?.readBytes()

            if (imageByteArray != null) {
                foto = imageByteArray
            }

            Imagen.setImageURI(imageUri)
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
