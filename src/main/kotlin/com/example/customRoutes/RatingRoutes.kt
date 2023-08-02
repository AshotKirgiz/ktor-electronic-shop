package com.example.customRoutes

import com.example.data.model.Rating
import com.example.data.table.RatingTable
import com.example.util.SimpleResponse
import com.example.repository.Repo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val RATE = "$PRODUCTS/rate"
const val USER_RATE = "$PRODUCTS/user-rate"
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
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }

            val principal = call.principal<JWTPrincipal>()
            val name = principal!!.payload.getClaim("username").asString()
            val productid = rating.productid

            if (db.checkRating(name, productid)) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "Rating already exist"))
            } else {
                db.addRating(rating)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Rating added successfully"))
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

        get(USER_RATE) {
            try {
                val principal = call.principal<JWTPrincipal>()
                val name = principal!!.payload.getClaim("username").asString()
                val rate = db.getUserRating(name)
                call.respond(HttpStatusCode.OK, rate)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Some Problem Occurred"))
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
                val gradeid = rating.gradeid
                if (db.checkGradeid(gradeid)) {
                    db.updateRating(rating)
                    call.respond(HttpStatusCode.OK,SimpleResponse(true, "Rating Updated Successfully"))
                } else {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false,"Rate doesn't exist"))
                }
            } catch (e:Exception){
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message?: "Some Problem Occurred"))
            }
        }

        delete(DELETE_RATE) {
            val gradeid = try {
                call.request.queryParameters["gradeid"]!!.toInt()
            } catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false, "QueryParameter: gradeid is not exist"))
                return@delete
            }
            try {
                if (db.checkGradeid(gradeid)) {
                    db.deleteRating(gradeid)
                    call.respond(HttpStatusCode.OK,SimpleResponse(true, "Rating Deleted Successfully"))
                } else {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false,"Rate doesn't exist"))
                }
            } catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message?: "Some Problem Occurred"))
            }
        }
    }
}