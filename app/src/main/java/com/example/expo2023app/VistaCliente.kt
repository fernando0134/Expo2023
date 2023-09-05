 package com.example.expo2023app

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

 private lateinit var AgregarCliente: Button
 private lateinit var cardsLayout: LinearLayout
 private lateinit var btnRefresh: LinearLayout
 var conect: Connection? = null

class VistaCliente : AppCompatActivity() {

    private val AGREGAR_CLIENTE_REQUEST_CODE = 1
    private val EDITAR_CLIENTE_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_cliente)

        AgregarCliente=findViewById(R.id.MainCliente_AgregarCliente)

        AgregarCliente.setOnClickListener {
            val i = Intent(this, Clientes::class.java)
            startActivityForResult(i, AGREGAR_CLIENTE_REQUEST_CODE)
        }



        //Hacemos referencia a el linear layout en el cual vamos a sampar las cards
        cardsLayout = findViewById(R.id.MainProductos_LlProductos)

        Actualizar()

        btnRefresh=findViewById(R.id.MainClientess_refresh)
        btnRefresh.setOnClickListener {
            cardsLayout.removeAllViews()
            Actualizar()
        }
    }

    private fun Actualizar(){
        //creamos la conexion
        conect = ConnectSql().dbConn()
        //Si la conexion jala, se buscaran datos
        if (conect != null) {
            try {
                //Sacamos los datos que mostraremos en la card
                val statement = conect!!.createStatement()
                val query = "SELECT idcliente, nombreCliente, DepartamentosClientes.Departementos, telefonoCliente FROM Tbcliente JOIN DepartamentosClientes ON DepartamentosClientes.iddepartamentocliente = Tbcliente.iddepartamentocliente;"
                val resultSet = statement.executeQuery(query)

                //Sacamos los datos que obtuvimos de la busqueda sql
                while (resultSet.next()) {
                    //Vamo a sacar el id pq asi sabremos cual es la card que queremos eliminar, no se mostrara en la card, pero se guardara
                    val Id = resultSet.getString("idcliente")
                    val nombreCliente = resultSet.getString("nombreCliente")
                    val departamentos = resultSet.getString("Departementos")
                    val telefono = resultSet.getString("telefonoCliente")

                    //Creamos una nueva card
                    val cardView = layoutInflater.inflate(R.layout.card_cliente, null)

                    //Hacemos las variables para hacer referencia a los componentes de la card (TextViews, Botones, etc...)

                    //Texto de las cards
                    val blNombreCliente = cardView.findViewById<TextView>(R.id.cardCliente_Cliente)
                    val lblDepartamentoCliente = cardView.findViewById<TextView>(R.id.cardCliente_Departamento)
                    val bltelefono = cardView.findViewById<TextView>(R.id.cardCliente_Telefono)

                    val btnEditarClientes = cardView.findViewById<TextView>(R.id.cardCliente_btnEditar)
                    val btnEliminarClientes = cardView.findViewById<TextView>(R.id.cardCliente_btnEliminar)


                    val btnInfoCliente = cardView.findViewById<LinearLayout>(R.id.cardCliente_Info)

                    btnInfoCliente.setOnClickListener {
                        val i = Intent(this, InformacionCliente::class.java)
                        i.putExtra("nomb", nombreCliente)
                        i.putExtra("depa", departamentos)
                        i.putExtra("cel",  telefono)
                        startActivity(i)
                    }

                    btnEditarClientes.setOnClickListener {
                        val i = Intent(this, ClientesEditar::class.java)
                        i.putExtra("nomb", nombreCliente)
                        i.putExtra("depa", departamentos)
                        i.putExtra("cel",  telefono)
                        startActivityForResult(i, EDITAR_CLIENTE_REQUEST_CODE)
                    }

                    btnEliminarClientes.setOnClickListener {
                        var con = ConnectSql().dbConn()
                        if (con!=null){
                            try {
                                val addProducto: PreparedStatement =  con.prepareStatement("delete from Tbcliente where idcliente=?")!!
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
                    blNombreCliente.text = nombreCliente
                    lblDepartamentoCliente.text = departamentos
                    bltelefono.text = telefono

                    //Finalmente sampar la card a el LinearLayout
                    cardsLayout.addView(cardView)

                    resultSet.close()
                    statement.close()
                    conect!!.close()
                }
            }catch (ex: SQLException) {
                // Manejo de excepciones en caso de error en la consulta
                ex.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AGREGAR_CLIENTE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                cardsLayout.removeAllViews()
                Actualizar()
            }
        }
    }
}