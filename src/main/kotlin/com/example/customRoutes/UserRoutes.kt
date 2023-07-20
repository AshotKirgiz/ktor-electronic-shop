package com.example.customRoutes

import com.example.auth.JwtService
import com.example.data.model.LoginRequest
import com.example.data.model.RegisterRequest
import com.example.data.model.User
import com.example.repository.Repo
import com.example.util.SimpleResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"
const val RESET_PASSWORD = "$USERS/password/reset"

fun Route.userRoutes(
    db: Repo,
    jwtService: JwtService,
    hashFunction: (String)->String
){
    post(REGISTER_REQUEST) {
        val registerRequest = try {
            call.receive<RegisterRequest>()
        }catch (e:Exception){
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false,"Missing some Fields"))
            return@post
        }

        try {
            val user = User(registerRequest.email,hashFunction(registerRequest.password),registerRequest.name)
            db.addUser(user)
            call.respond(HttpStatusCode.OK,SimpleResponse(true,jwtService.generateToken(user)))
        }catch (e:Exception){
            call.respond(HttpStatusCode.Conflict,SimpleResponse(false,e.message ?: "Some Problem Occurred"))
        }
    }

    post(LOGIN_REQUEST) {
        val loginRequest = try {
            call.receive<LoginRequest>()
        }catch (e:Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing some Fields"))
            return@post
        }

        try {
            val user = db.findUserByEmail(loginRequest.email)

            if(user == null){
                call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Wrong Email Id"))
            } else {
                if(user.hashPassword == hashFunction(loginRequest.password)) {
                    call.respond(HttpStatusCode.OK,SimpleResponse(true,jwtService.generateToken(user)))
                } else {
                    call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"Password Incorrect"))
                }
            }
        } catch (e:Exception){
            call.respond(HttpStatusCode.Conflict,SimpleResponse(false,e.message ?: "Some Problem Occurred"))
        }
    }

    authenticate("jwt") {
        post(RESET_PASSWORD) {
            TODO()
        }
    }
}

