package com.yeweijiehust.artmuseum.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url

interface ArtMuseumApi {
    @GET
    suspend fun health(@Url url: String): Response<HealthDto>

    @GET
    suspend fun me(@Url url: String): Response<AuthResponseDto>

    @POST
    suspend fun login(@Url url: String, @Body body: LoginRequestDto): Response<AuthResponseDto>

    @POST
    suspend fun register(@Url url: String, @Body body: RegisterRequestDto): Response<AuthResponseDto>

    @POST
    suspend fun logout(@Url url: String): Response<Unit>

    @GET
    suspend fun images(@Url url: String): Response<ImageListDto>

    @GET
    suspend fun image(@Url url: String): Response<ImageDto>

    @GET
    suspend fun myImages(@Url url: String): Response<ImageListDto>

    @Multipart
    @POST
    suspend fun upload(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("altText") altText: RequestBody
    ): Response<ImageDto>

    @PATCH
    suspend fun update(@Url url: String, @Body body: ImageUpdateDto): Response<ImageDto>

    @DELETE
    suspend fun delete(@Url url: String): Response<Unit>
}
