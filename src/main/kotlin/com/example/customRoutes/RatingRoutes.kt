package com.example.customRoutes

import com.example.data.model.Rating
import com.example.data.table.RatingTable
import com.example.data.table.RatingTable.name
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
        post(CREATE_RATE) {
            val rating = try {
                call.receive<Rating>()
            } catch (e:Exception) {
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Missing Fields"))
                return@post
            }
            try {
                val name = call.request.queryParameters["name"]!!
                db.checkRating(name)
                /*call.respond(HttpStatusCode.Conflict,SimpleResponse(false, "You have already rated"))
                val productid = call.request.queryParameters["productid"]!!
                db.addRating(rating as Rating, productid)
                call.respond(HttpStatusCode.OK,SimpleResponse(true,"Rating added Successfully"))*/
                call.respond(HttpStatusCode.OK,SimpleResponse(true,"You Have Already Rated"))
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false,e.message ?: "Some Problem Occurred"))
            }
        }

        get(RATE) {
            try {
                val productid = call.request.queryParameters["productid"]!!
                val rating = db.getRating(productid)
                call.respond(HttpStatusCode.OK,rating)
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Some Problem Occurred")
            }
        }

        post(UPDATE_RATE) {
            val rating = try {
                call.receive<Rating>()
            } catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Missing Fields"))
                return@post
            }
            try {
                db.updateRating(rating)
                call.respond(HttpStatusCode.OK,SimpleResponse(true, "Rating Updated Successfully"))
            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message?: "Some Problem Occurred"))
            }
        }

        delete(DELETE_RATE) {
            val gradeId = try {
                call.request.queryParameters["gradeid"]!!
            } catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false, "QueryParameter: gradeid is not exist"))
                return@delete
            }
            try {
                db.deleteRating(gradeId.toInt())
                call.respond(HttpStatusCode.OK,SimpleResponse(true, "Rating Deleted Successfully"))
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message?: "Some Problem Occurred"))
            }
        }
    }
}