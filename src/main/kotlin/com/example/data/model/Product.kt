package com.example.data.model

import io.ktor.server.auth.*

data class Product(
    val id:String,
    val title:String,
    val description:String,
    val category:String,
    val price:Int,
    val availability:Boolean
)
