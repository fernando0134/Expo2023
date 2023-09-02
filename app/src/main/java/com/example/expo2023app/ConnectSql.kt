package com.example.expo2023app

import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConnectSql {
    //private  val ip="10.20.1.130:49894"
    //private val db="a"
    //private val username ="sa"
    //private val password ="itr2023"

    private  val ip="192.168.1.6:1433"
    private val db="a"
    private val username ="alonso1"
    private val password ="1234"

    fun dbConn(): Connection? {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn: Connection? = null
        val connString: String
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance()
            connString = "jdbc:jtds:sqlserver://$ip;databaseName=$db;user=$username;password=$password"
            conn = DriverManager.getConnection(connString)
        }catch (ex: SQLException) {
            Log.e("Error: ", ex.message!!)
        }
        catch (ex1: ClassNotFoundException)
        {
            Log.e("Error: ", ex1.message!!)
        }catch (ex2: Exception){
            Log.e("Error: ", ex2.message!!)
        }
        return conn
    }

}