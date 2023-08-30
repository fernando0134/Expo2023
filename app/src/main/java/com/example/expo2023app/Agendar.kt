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



private lateinit var SubirFoto : TextView

private lateinit var TomarFoto : TextView

private lateinit var Imagen: ImageView


private var foto: ByteArray? = null

private val PICK_IMAGE_REQUEST = 1
private val REQUEST_IMAGE_CAPTURE = 2



private lateinit var lblmasaje:EditText
private lateinit var lblPrecio:EditText
private lateinit var btnAgendar:Button


private lateinit var spinner:Spinner

class Agendar : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar)

        val conn = ConnectSql().dbConn()
        val Masajitas = ArrayList<String>()


        spinner = findViewById(R.id.Masajitas)


        try {

            val statement = conn?.createStatement()
            val query = "SELECT masajes FROM Tbcitas"
            val resultSet = statement?.executeQuery(query)

            Masajitas.add("Seleccione una opcion...")


            while (resultSet?.next() == true) {

                val TipoMasaje = resultSet.getString("masajes")

                Masajitas.add(TipoMasaje)
            }


            val adapter = ArrayAdapter(this, R.drawable.custom_spinner_adapter, Masajitas)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            resultSet?.close()
            statement?.close()
        }catch (ex: SQLException) {
            println(ex.message)
        }


        SubirFoto=findViewById(R.id.SubirFoto)
        TomarFoto=findViewById(R.id.TomarFoto)
        Imagen=findViewById(R.id.foto)

        val drawable = Imagen.drawable
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
        foto = stream.toByteArray()

        SubirFoto.setOnClickListener {
            getIMG(this, false)
        }
        //Y si quiere tomar la foto pues el parametro va a ser verdadero
        TomarFoto.setOnClickListener {
            getIMG(this, true)
        }

        lblmasaje=findViewById(R.id.masaje)
        lblPrecio=findViewById(R.id.precio)

        btnAgendar=findViewById(R.id.AgendarMasaje)

        btnAgendar.setOnClickListener {
            if(lblPrecio.text.toString()!="" && lblmasaje.text.toString()!="" && spinner.selectedItem.toString()!="Seleccione una opcion..."){
                try {
                    val addMasajes: PreparedStatement =  conn?.prepareStatement(


                        "INSERT INTO TbMasajes (idcita, nombre, precioUnit, foto)\n" +
                                "SELECT c.idcita, ?, ?, ?\n" +
                                "FROM Tbcitas c\n" +
                                "WHERE c.masajes = ?;"
                    )!!

                    val inicio: Intent = Intent(this, VistaAgendar::class.java)
                    addMasajes.setString(1, lblmasaje.text.toString())
                    addMasajes.setString(2, lblPrecio.text.toString())
                    addMasajes.setBytes(3, foto)
                    addMasajes.setString(4, spinner.selectedItem.toString())
                    addMasajes.executeUpdate()

                    Toast.makeText(this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show()

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