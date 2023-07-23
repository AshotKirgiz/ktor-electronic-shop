package com.example.data.model

import io.ktor.server.auth.*

data class User(
    val id:Int,
    val email:String,
    val hashPassword:String,
    val userName:String
): Principal
