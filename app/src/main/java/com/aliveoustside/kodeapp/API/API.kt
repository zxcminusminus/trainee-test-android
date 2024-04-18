package com.aliveoustside.kodeapp.API

import com.aliveoustside.kodeapp.model.ServerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface API {

    @GET("/mocks/kode-api/trainee-test/331141861/users")
    @Headers(
        "Accept: application/json, application/xml",
        "Prefer: code=200, example=success"
    )
    suspend fun getEmployees():Response<ServerResponse>

    @GET("/mocks/kode-api/trainee-test/331141861/users")
    @Headers(
        "Accept: application/json, application/xml",
        "Prefer: code=500, example=error-500"
    )
    suspend fun getEmployeesError():Response<ServerResponse>
}