package com.jackl.example.api

import com.jackl.example.model.MyResponseModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * @description:
 * @author: jackl
 * @date: 2021/12/15
 */
interface UploadService {
    @Multipart
    @POST("{path}")
    fun upload(@Path("path") method: String, @Part file: MultipartBody.Part): Call<MyResponseModel?>
}