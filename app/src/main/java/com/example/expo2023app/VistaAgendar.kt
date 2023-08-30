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

private lateinit var uwu: Button
private lateinit var cardsLayout: LinearLayout
private lateinit var btnRefresh: LinearLayout
var connection: Connection? = null

class VistaAgendar : AppCompatActivity() {

    private val AGREGAR_masaje_REQUEST_CODE = 1
    private val EDITAR_masaje_REQUEST_CODE = 2

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_agendar)

        uwu=findViewById(R.id.AgendarMasaje)

        uwu.setOnClickListener {
            val i = Intent(this, Agendar::class.java)
            startActivityForResult(i, AGREGAR_masaje_REQUEST_CODE)
        }

        cardsLayout = findViewById(R.id.Mostrarinformacion)

        Actualizar()

        btnRefresh=findViewById(R.id.recargar)

        btnRefresh.setOnClickListener {
            cardsLayout.removeAllViews()
            Actualizar()
        }

    }

    private fun Actualizar(){

        connection = ConnectSql().dbConn()
        if (connection != null) {
            try {

                val statement = connection!!.createStatement()
                val query = "SELECT IdMasajes, Nombre, Tbcitas.masajes, PrecioUnit, foto FROM TbMasajes JOIN Tbcitas ON TbMasajes.idcita = Tbcitas.idcita;"
                val resultSet = statement.executeQuery(query)


                while (resultSet.next()) {

                    val Id = resultSet.getString("IdMasajes")
                    val nombre = resultSet.getString("Nombre")
                    val masajes = resultSet.getString("masajes")
                    val precio = resultSet.getFloat("precioUnit")
                    val foto: ByteArray? = resultSet.getBytes("foto")


                    val cardView = layoutInflater.inflate(R.layout.card_agendar, null)


                    val blNombre = cardView.findViewById<TextView>(R.id.cardInformacion)
                    val lblClasificacion = cardView.findViewById<TextView>(R.id.cardMasaje)

                    var price = DecimalFormat("#.##").format(precio)

                    val blPrecio = cardView.findViewById<TextView>(R.id.cardprecio)



                    val btnEditar = cardView.findViewById<TextView>(R.id.cardEditar)
                    val btnEliminar = cardView.findViewById<TextView>(R.id.cardEliminar)

                    val btnInfo = cardView.findViewById<LinearLayout>(R.id.CardInformacion)
                    btnInfo.setOnClickListener {
                        val i = Intent(this, MostrarInformacion::class.java)
                        i.putExtra("nomb", nombre)
                        i.putExtra("cat", masajes)
                        i.putExtra("price", "$" + price)
                        if (foto != null && foto.isNotEmpty()) {
                            i.putExtra("foto", foto)
                        }
                        startActivity(i)
                    }

                    btnEditar.setOnClickListener {
                        val i = Intent(this, EditarAgenda::class.java)
                        i.putExtra("nomb", nombre)
                        i.putExtra("cat", masajes)
                        i.putExtra("price", "$" + price)
                        if (foto != null && foto.isNotEmpty()) {
                            i.putExtra("foto", foto) // Pasar el ByteArray directamente
                        }
                        startActivityForResult(i, EDITAR_masaje_REQUEST_CODE)
                    }

                    btnEliminar.setOnClickListener {
                        var con = ConnectSql().dbConn()
                        if (con!=null){
                            try {
                                val addProducto: PreparedStatement =  con.prepareStatement("delete from TbMasajes where IdMasajes=?")!!
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
                    lblClasificacion.text = masajes
                    blPrecio.text = "$"+price


                    cardsLayout.addView(cardView)
                }

                resultSet.close()
                statement.close()
                connection!!.close()
            } catch (ex: SQLException) {
                ex.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AGREGAR_masaje_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                cardsLayout.removeAllViews()
                Actualizar()
            }

        }
    }
}