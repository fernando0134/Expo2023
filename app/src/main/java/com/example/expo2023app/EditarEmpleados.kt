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

//Los demas campos de texto que usamos para poner el texto
@SuppressLint("StaticFieldLeak")
private lateinit var lblEmpleadosNom: EditText
private lateinit var lblDui: EditText
private lateinit var btnActualizar: Button

private lateinit var spinner: Spinner

//Valores anteriores
private lateinit var Nombre_antes: TextView
private lateinit var Dui_antes: TextView
private lateinit var Departamento_antes: TextView

