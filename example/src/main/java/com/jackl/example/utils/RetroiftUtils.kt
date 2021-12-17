package com.jackl.example.utils

import com.google.gson.Gson
import com.jackl.example.model.MyResponseModel
import com.jackl.example.api.UploadService
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * @description:
 * @author: jackl
 * @date: 2021/12/15
 */
object RetroiftUtils {
    val baseUrl : String = ""
    val method : String = ""

    val uploadApi: UploadService by lazy { retrofit.create(UploadService::class.java) }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy { HttpLoggingInterceptor() }
    private val okHttpClient: OkHttpClient by lazy {
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20000, TimeUnit.MILLISECONDS)
            .writeTimeout(10000, TimeUnit.MILLISECONDS)
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .build()
    }

    val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

    fun upload(file : File): MyResponseModel? {
        val body: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        )
        return uploadApi.upload(method,body).execute().body()
    }

}