package com.example.expo2023app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException



private lateinit var AgregarEmpleado: Button
private lateinit var cardsLayoutEmpleado: LinearLayout
private lateinit var btnRefresh1: LinearLayout
var Conectar: Connection? = null

class VistaEmpleados : AppCompatActivity() {

    private val AGREGAR_EMPLEADOS_REQUEST_CODE = 1
    private val EDITAR_PRODUCTO_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_empleados)


        AgregarEmpleado=findViewById(R.id.MainEmpleados_AgregarEmpleados)

        AgregarEmpleado.setOnClickListener {
            val i = Intent(this, Empleados::class.java)
            startActivityForResult(i, AGREGAR_EMPLEADOS_REQUEST_CODE)
        }

        //Hacemos referencia a el linear layout en el cual vamos a sampar las cards
        cardsLayoutEmpleado = findViewById(R.id.MainEmpleados_LlEmpleados)

        Actualizar()

        btnRefresh1=findViewById(R.id.MainEmpleados_refresh)
        btnRefresh1.setOnClickListener {
            cardsLayoutEmpleado.removeAllViews()
            Actualizar()
        }

    }
    private fun Actualizar(){
        Conectar = ConnectSql().dbConn()

        if (Conectar != null) {
            //Sacamos los datos que mostraremos en la card
            val statement = Conectar!!.createStatement()
            val query = "SELECT idEmpleados, nombre_Empleado, TbDepartamentos.Departamentos, dui, fotoempleado FROM TbEmpleados JOIN TbDepartamentos ON TbEmpleados.idEmpleados = TbDepartamentos.idagregar;"
            val resultSet = statement.executeQuery(query)

            //Sacamos los datos que obtuvimos de la busqueda sql
            while (resultSet.next()) {
                //Vamos a sacar el id pq asi sabremos cual es la card que queremos eliminar, no se mostrara en la card, pero se guardara
                val Id = resultSet.getString("idEmpleados")
                val nombre = resultSet.getString("nombre_Empleado")
                val departamento = resultSet.getString("Departamentos")
                val dui = resultSet.getString("Dui")
                val foto: ByteArray? = resultSet.getBytes("fotoempleado")

                //Creamos una nueva card (la cual tendriamos que haber hecho el diseño antes) /layout/card_producto
                val cardView = layoutInflater.inflate(R.layout.activity_card_empleados, null)

                //Hacemos las variables para hacer referencia a los componentes de la card (TextViews, Botones, etc...)

                //Texto de las cards
                val blNombre = cardView.findViewById<TextView>(R.id.cardEmpleados_Empleados)
                val lblDepartamentos = cardView.findViewById<TextView>(R.id.cardEmpleados_Departamento)

                val blDui = cardView.findViewById<TextView>(R.id.cardEmpleado_Dui)

                //Botones de las cards

                val btnEditar = cardView.findViewById<TextView>(R.id.cardEditar_btnEditar)
                val btnEliminar = cardView.findViewById<TextView>(R.id.cardEmpleados_btnEliminar)

                val btnInfo = cardView.findViewById<LinearLayout>(R.id.cardEmpleados_Info)

                btnInfo.setOnClickListener {
                    val i = Intent(this, InformacionEmpleados::class.java)
                    i.putExtra("nomb", nombre)
                    i.putExtra("departamento", departamento)
                    i.putExtra("Dui", dui)
                    if (foto != null && foto.isNotEmpty()) {
                        i.putExtra("foto", foto) // Pasar el ByteArray directamente
                    }
                    startActivity(i)
                }


                btnEditar.setOnClickListener {
                    val i = Intent(this, EditarEmpleados::class.java)
                    i.putExtra("nomb", nombre)
                    i.putExtra("departamento", departamento)
                    i.putExtra("Dui", dui)
                    if (foto != null && foto.isNotEmpty()) {
                        i.putExtra("foto", foto) // Pasar el ByteArray directamente
                    }
                    startActivityForResult(i, EDITAR_PRODUCTO_REQUEST_CODE)
                }

                btnEliminar.setOnClickListener {
                    var con = ConnectSql().dbConn()
                    if (con!=null){
                        try {
                            val addProducto: PreparedStatement =  con.prepareStatement("delete from TbEmpleados where idEmpleados=?")!!
                            addProducto.setString(1, Id)
                            addProducto.executeUpdate()
                            cardsLayoutEmpleado.removeView(cardView)
                            Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
                            con.close()
                        }
                        catch (ex: SQLException){
                            Toast.makeText(this, "Ocurrio un error: "+ex, Toast.LENGTH_SHORT).show()
                            println(ex)
                            con.close()
                        }
                    }
                }
                //Definir valores de las cards
                blNombre.text = nombre
                lblDepartamentos.text = departamento
                blDui.text = dui

                //Finalmente sampar la card a el LinearLayout
                cardsLayoutEmpleado.addView(cardView)
            }
            resultSet.close()
            statement.close()
            Conectar!!.close()
            }
        }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AGREGAR_EMPLEADOS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                cardsLayoutEmpleado.removeAllViews()
                Actualizar()
            }
        }
    }
    }
