package com.example.projectakhirtis.network

import com.example.projectakhirtis.model.GeneralResponse
import com.example.projectakhirtis.model.LoginResponse
import com.example.projectakhirtis.model.RegisterRequest
import com.example.projectakhirtis.model.Ticket
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("tickets")
    suspend fun getTickets(
        @Header("Authorization") token: String
    ): Response<List<Ticket>>

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

    @POST("tickets")
    suspend fun addTicket(
        @Header("Authorization") token: String,
        @Body ticket: Ticket
    ): Response<GeneralResponse>

    @DELETE("tickets/{id}")
    suspend fun deleteTicket(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<GeneralResponse>

    @PUT("tickets/{id}")
    suspend fun updateTicket(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body ticket: Ticket
    ): Response<GeneralResponse>
}

interface CalendarApi {
    @POST("calendar/add-event")
    fun createEvent(@Body ticket: Ticket): Call<ResponseBody>
}
