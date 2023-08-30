package com.example.expo2023app

import java.security.MessageDigest

class Encriptar {

    fun sha256(passEncry: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(passEncry.toByteArray())

        val hexString = StringBuilder()
        for (byte in digest) {
            hexString.append(String.format("%02x", byte))
        }
        return hexString.toString()
    }
}