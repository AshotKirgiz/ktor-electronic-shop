package com.example.data.model

import io.ktor.server.auth.*

data class Rating(
    val productid:String,
    val gradeid:Int,
    val userName:String,
    val grade:Int,
    val title:String,
    val description:String
): Principal
