package com.example.customRoutes

import com.example.data.model.Product
import com.example.data.table.ProductTable
import com.example.util.SimpleResponse
import com.example.repository.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val PRODUCTS="$API_VERSION/product"
const val CREATE_PRODUCTS = "$PRODUCTS/create"
const val UPDATE_PRODUCTS = "$PRODUCTS/update"
const val DELETE_PRODUCTS = "$PRODUCTS/delete"
const val FIND_PRODUCTS_ID = "$PRODUCTS/id"
const val FIND_PRODUCTS_CATEGORY = "$PRODUCTS/category"

fun Route.productRoutes(
    db:Repo,
) {
    authenticate("jwt") {
        post(CREATE_PRODUCTS) {
            val product = try {
                call.receive<Product>()
            } catch (e:Exception) {
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Missing Fields"))
            }

            try {
                db.addProduct(product as Product)
                call.respond(HttpStatusCode.OK,SimpleResponse(true,"Product added Successfully"))
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false,e.message ?: "Some Problem Occurred"))
            }
        }

        get(PRODUCTS) {
            try {
                val products = db.getAllProducts()
                call.respond(HttpStatusCode.OK,products)
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Some Problem Occurred")
            }
        }

        get(FIND_PRODUCTS_ID) {
            try {
                val id = call.request.queryParameters["id"]!!
                if (db.checkProductById(id)) {
                    val products = db.findProductById(id)
                    call.respond(HttpStatusCode.OK,products)
                } else {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false,"Product doesn't exist"))
                }
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Some Problem Occurred")
            }
        }

        get(FIND_PRODUCTS_CATEGORY) {
            try {
                val category = call.request.queryParameters["category"]!!
                if (db.checkProductByCategory(category)) {
                    val products = db.findProductByCategory(category)
                    call.respond(HttpStatusCode.OK,products)
                } else {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false,"Product doesn't exist"))
                }
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Some Problem Occurred")
            }
        }

        post(UPDATE_PRODUCTS) {
            val product = try {
                call.receive<Product>()
            } catch (e:Exception) {
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Missing Fields"))
                return@post
            }

            try {
                val id = product.id
                if (db.checkProductById(id)) {
                    db.updateProduct(product)
                    call.respond(HttpStatusCode.OK,SimpleResponse(true,"Product updated Successfully"))
                } else {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false,"Product doesn't exist"))
                }
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false,e.message ?: "Some Problem Occurred"))
            }
        }

        delete(DELETE_PRODUCTS) {
            val id = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "QueryParameter: id is not exist"))
                return@delete
            }
            try {
                if (db.checkProductById(id)) {
                    db.deleteProduct(id)
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Product Deleted Successfully"))
                } else {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false,"Product doesn't exist"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred"))
            }
        }
    }
}