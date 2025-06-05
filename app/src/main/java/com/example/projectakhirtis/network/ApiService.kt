package com.example.projectakhirtis.network

import com.example.projectakhirtis.model.GeneralResponse
import com.example.projectakhirtis.model.LoginResponse
import com.example.projectakhirtis.model.RegisterRequest
import com.example.projectakhirtis.model.Ticket
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("tickets")
    suspend fun getTickets(): List<Ticket>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<GeneralResponse>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("tickets")
    suspend fun getTickets(
        @Header("Authorization") token: String
    ): List<Ticket>

    @POST("tickets")
    suspend fun addTicket(
        @Header("Authorization") token: String,
        @Body ticket: Ticket
    ): Response<GeneralResponse>
}
