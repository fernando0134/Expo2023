package com.example.expo2023app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.DecimalFormat

private lateinit var AgregarEmpleado: Button
private lateinit var cardsLayout1: LinearLayout
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
        cardsLayout1 = findViewById(R.id.MainEmpleados_LiEmpleados)

        Actualizar()

        btnRefresh1=findViewById(R.id.MainEmpleados_refresh)
        btnRefresh1.setOnClickListener {
            cardsLayout1.removeAllViews()
            Actualizar()
        }
    }

    private fun Actualizar(){
        //creamos la conexion
        connection = ConnectSql().dbConn()
        //Si la conexion jala, se buscaran datos
        if (connection != null) {
            try {
                //Sacamos los datos que mostraremos en la card
                val statement = connection!!.createStatement()
                val query = "SELECT idEmpleados, nombre_Empleado, TbDepartamentos.Departamentos, dui, fotoempleado FROM TbEmpleados JOIN TbDepartamentos ON TbEmpleados.idagregar = TbDepartamentos.idagregar;"
                val resultSet = statement.executeQuery(query)

                //Sacamos los datos que obtuvimos de la busqueda sql
                while (resultSet.next()) {
                    //Vamo a sacar el id porque asi sabremos cual es la card que queremos eliminar, no se mostrara en la card, pero se guardara
                    val Id = resultSet.getString("idEmpleados")
                    val nombre = resultSet.getString("nombre_Empleado")
                    val departamento = resultSet.getString("Departamentos")
                    val dui = resultSet.getFloat("dui")
                    val foto: ByteArray? = resultSet.getBytes("fotoempleado")

                    //Creamos una nueva card
                    val cardView = layoutInflater.inflate(R.layout.card_empleados, null)

                    //Hacemos las variables para hacer referencia a los componentes de la card (TextViews, Botones, etc...)

                    //Texto de las cards
                    val blNombre = cardView.findViewById<TextView>(R.id.cardEmpleados_Empleados)
                    val lblDepartamento = cardView.findViewById<TextView>(R.id.cardEmpleados_Departamento)

                    var Dui = DecimalFormat("#.##").format(dui)

                    val blDui = cardView.findViewById<TextView>(R.id.cardEmpleado_Dui)

                    //Yo en este caso, ocupé TextViews como botones debido a que me daba hueva rediseñar os botones por completo, pero el proceso +
                    //Es es el mismo si se ocupa un boton, ya que ambos pueden hacer referencia a el metodo ".setOnClickListener{}"

                    //Botones de las cards
                    val btnEditar = cardView.findViewById<TextView>(R.id.cardEmpleados_btnEliminar)
                    val btnEliminar = cardView.findViewById<TextView>(R.id.cardEditar_btnEditar)

                    val btnInfo = cardView.findViewById<LinearLayout>(R.id.cardEmpleados_Info)
                    btnInfo.setOnClickListener {
                        val i = Intent(this, InformacionEmpleados::class.java)
                        i.putExtra("nomb", nombre)
                        i.putExtra("depa", departamento)
                        i.putExtra("dui", "$" + dui)
                        if (foto != null && foto.isNotEmpty()) {
                            i.putExtra("foto", foto) // Pasar el ByteArray directamente
                        }
                        startActivity(i)
                    }

                    btnEditar.setOnClickListener {
                        val i = Intent(this, EditarEmpleados::class.java)
                        i.putExtra("nomb", nombre)
                        i.putExtra("departamento", departamento)
                        i.putExtra("dui", Dui)
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
                                cardsLayout1.removeView(cardView)
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
                    lblDepartamento.text = departamento
                    blDui.text = Dui

                    //Finalmente llamar a la card a el LinearLayout
                    cardsLayout1.addView(cardView)
                }

                resultSet.close()
                statement.close()
                connection!!.close()
            } catch (ex: SQLException) {
                // Manejo de excepciones en caso de error en la consulta
                ex.printStackTrace()
            }
        }
    }

}