package com.example.monastery360

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// ---------- REQUEST MODELS ----------
data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val username: String,
    val email: String,
    val password: String,
    val type: String = "user"
)

// ---------- RESPONSE MODELS ----------

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserData?
)

data class SignupResponse(
    val success: Boolean,
    val message: String
)

// User object from DB
data class UserData(
    val _id: String,
    val email: String,
    val username: String,
    val type: String,
    val savedMonasteries: List<String>,
    val bookings: List<String>,
    val BookedEvents: List<String>,
    val createdAt: String?,
    val updatedAt: String?
)

// ---------- API INTERFACE ----------
interface MonasteryApi {

    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/register")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>
}
