package com.example.expo2023app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.DecimalFormat

private lateinit var AgregarProducto: Button
private lateinit var cardsLayout: LinearLayout
private lateinit var btnRefresh: LinearLayout
var conect1: Connection? = null

private val AGREGAR_PRODUCTO_REQUEST_CODE = 1
private val EDITAR_PRODUCTO_REQUEST_CODE = 2

class VistaProductos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_productos)


        AgregarProducto=findViewById(R.id.MainProductos_AgregarProducto)
        AgregarProducto.setOnClickListener {
            val i = Intent(this, Productos::class.java)
            startActivityForResult(i, AGREGAR_PRODUCTO_REQUEST_CODE)
        }

        //Hacemos referencia a el linear layout en el cual vamos a sampar las cards
        cardsLayout = findViewById(R.id.MainProductos_LlProductos)

        Actualizar()

        btnRefresh=findViewById(R.id.MainProductos_refresh)
        btnRefresh.setOnClickListener {
            cardsLayout.removeAllViews()
            Actualizar()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun Actualizar(){
        //creamos la conexion
        connection = ConnectSql().dbConn()
        //Si la conexion jala, se buscaran datos
        if (connection != null) {
            try {
                //Sacamos los datos que mostraremos en la card
                val statement = connection!!.createStatement()
                val query = "SELECT IdProducto, nombre, tbClasificacion.clasificacion, precioUnit, foto FROM tbProductos JOIN tbClasificacion ON tbProductos.IdClasificacion = tbClasificacion.IdClasificacion;"
                val resultSet = statement.executeQuery(query)

                //Sacamos los datos que obtuvimos de la busqueda sql
                while (resultSet.next()) {
                    //Vamo a sacar el id pq asi sabremos cual es la card que queremos eliminar, no se mostrara en la card, pero se guardara
                    val Id = resultSet.getString("IdProducto")
                    val nombre = resultSet.getString("nombre")
                    val clasificacion = resultSet.getString("clasificacion")
                    val precio = resultSet.getFloat("precioUnit")
                    val foto: ByteArray? = resultSet.getBytes("foto")

                    //Creamos una nueva card (la cual tendriamos que haber hecho el diseño antes) /layout/card_producto
                    val cardView = layoutInflater.inflate(R.layout.card_producto, null)

                    //Hacemos las variables para hacer referencia a los componentes de la card (TextViews, Botones, etc...)

                    //Texto de las cards
                    val blNombre = cardView.findViewById<TextView>(R.id.cardProducto_Producto)
                    val lblClasificacion = cardView.findViewById<TextView>(R.id.cardProducto_Clasificacion)

                    var price = DecimalFormat("#.##").format(precio)

                    val blPrecio = cardView.findViewById<TextView>(R.id.cardProducto_Precio)

                    //Yo en este caso, ocupé TextViews como botones debido a que me daba hueva rediseñar os botones por completo, pero el proceso +
                    //Es es el mismo si se ocupa un boton, ya que ambos pueden hacer referencia a el metodo ".setOnClickListener{}"

                    //Botones de las cards
                    val btnEditar = cardView.findViewById<TextView>(R.id.cardProducto_btnEditar)
                    val btnEliminar = cardView.findViewById<TextView>(R.id.cardProducto_btnEliminar)

                    val btnInfo = cardView.findViewById<LinearLayout>(R.id.cardProducto_Info)
                    btnInfo.setOnClickListener {
                        val i = Intent(this, Productos_Info::class.java)
                        i.putExtra("nomb", nombre)
                        i.putExtra("cat", clasificacion)
                        i.putExtra("price", "$" + price)
                        if (foto != null && foto.isNotEmpty()) {
                            i.putExtra("foto", foto) // Pasar el ByteArray directamente
                        }
                        startActivity(i)
                    }

                    btnEditar.setOnClickListener {
                        val i = Intent(this, Productos_Editar::class.java)
                        i.putExtra("nomb", nombre)
                        i.putExtra("cat", clasificacion)
                        i.putExtra("price", "$" + price)
                        if (foto != null && foto.isNotEmpty()) {
                            i.putExtra("foto", foto) // Pasar el ByteArray directamente
                        }
                        startActivityForResult(i, EDITAR_PRODUCTO_REQUEST_CODE)
                    }

                    btnEliminar.setOnClickListener {
                        var con = ConnectSql().dbConn()
                        if (con!=null){
                            try {
                                val addProducto: PreparedStatement =  con.prepareStatement("delete from tbProductos where IdProducto=?")!!
                                addProducto.setString(1, Id)
                                addProducto.executeUpdate()
                                cardsLayout.removeView(cardView)
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
                    lblClasificacion.text = clasificacion
                    blPrecio.text = "$"+price

                    //Finalmente sampar la card a el LinearLayout
                    cardsLayout.addView(cardView)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AGREGAR_PRODUCTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                cardsLayout.removeAllViews()
                Actualizar()
            }
        }
    }
}