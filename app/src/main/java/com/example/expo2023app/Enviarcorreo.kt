package com.example.expo2023app

import android.os.AsyncTask
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class Enviarcorreo (private val destinatario: String,
                    private val codigo: String,
                    private val asunto: String):

    AsyncTask<Void?, Void?, Void?>() {
    override fun doInBackground(params: Array<Void?>): Void? {


        val props = Properties()
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"


        val session = Session.getDefaultInstance(props,
            object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {



                    return PasswordAuthentication("juliocesar12143333@gmail.com", "nttdiaxjmsnpvgrt")
                }
            })
        try {
            val message = MimeMessage(session)

            //Cambiamos el valor por el correo desde donde enviaremos el mensaje
            message.setFrom(InternetAddress("juliocesar12143333@gmail.com"))
            message.addRecipient(Message.RecipientType.TO, InternetAddress(destinatario))
            message.subject = asunto
            message.setText(this.codigo)
            Transport.send(message)

//            println("Correo enviado satisfactoriamente")
        } catch (e: MessagingException) {
            e.printStackTrace()

            println("El correo no se ha podido enviar")
        }
        return null
    }
}