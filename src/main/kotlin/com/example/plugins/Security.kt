package com.example.plugins


import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.example.auth.JwtService
import com.example.repository.Repo

val db = Repo()
val jwtService = JwtService

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Electronic Shop"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val user = db.findUserByEmail(email)
                user
            }
        }
    }
}
