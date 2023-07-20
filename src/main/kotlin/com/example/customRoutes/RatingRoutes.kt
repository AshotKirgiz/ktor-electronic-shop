package com.example.customRoutes

import com.example.data.model.Product
import com.example.data.model.Rating
import com.example.data.table.RatingTable
import com.example.util.SimpleResponse
import com.example.repository.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val RATE = "$PRODUCTS/rate"
const val CREATE_RATE = "$RATE/create"
const val UPDATE_RATE = "$RATE/update"
const val DELETE_RATE = "$RATE/delete"

fun Route.ratingRoutes(
    db: Repo,
) {
    authenticate("jwt") {
        post (CREATE_RATE) {
            val rating = try {
                call.receive<Rating>()
            } catch (e:Exception) {
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Missing Fields"))
            }

            try {
                val productid = call.principal<Product>()!!.id
                db.addRating(rating as Rating, productid)
                call.respond(HttpStatusCode.OK,SimpleResponse(true,"Rating added Successfully"))
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false,e.message ?: "Some Problem Occurred"))
            }
        }

        get (RATE) {
            try {
                val productid = call.principal<Product>()!!.id
                val rating = db.getRating(productid)
                call.respond(HttpStatusCode.OK,rating)
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Rating>())
            }
        }

        post (UPDATE_RATE) {
            val rating = try {
                call.receive<Rating>()
            } catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Missing Fields"))
                return@post
            }
            try {
                val productid = call.principal<Product>()!!.id
                db.updateRating(rating,productid)
                call.respond(HttpStatusCode.OK,SimpleResponse(true, "Rating Updated Successfully"))
            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message?: "Some Problem Occurred"))
            }
        }

        delete (DELETE_RATE) {
            val gradeId = try {
                call.request.queryParameters["id"]!!
            } catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false, "QueryParameter: gradeid is not exist"))
                return@delete
            }
            try {
                val productid = call.principal<Product>()!!.id
                db.deleteRating(gradeId,productid)
                call.respond(HttpStatusCode.OK,SimpleResponse(false, "Rating Deleted Successfully"))
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message?: "Some Problem Occurred"))
            }
        }
    }
}