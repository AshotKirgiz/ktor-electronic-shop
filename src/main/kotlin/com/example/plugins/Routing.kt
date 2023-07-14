package com.example.plugins

import com.example.auth.JwtService
import com.example.auth.hash
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import com.example.customRoutes.userRoutes
import com.example.customRoutes.productRoutes
import com.example.repository.Repo

fun Application.configureRouting() {
    routing {
        get("/")
        {
            call.respondText("Hello World!")
        }

        val db = Repo()
        val jwtService = JwtService
        val hashFunction = { s:String -> hash(s) }
        userRoutes(db,jwtService,hashFunction)
        productRoutes(db)
    }
}
