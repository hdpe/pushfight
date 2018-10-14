package me.hdpe.pushfight.tools

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.*

fun main(vararg args: String) {
    val bytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).encoded

    System.out.println("accessKeyId=" + UUID.randomUUID())
    System.out.println("secret=" + String(Base64.getEncoder().encode(bytes)))
}