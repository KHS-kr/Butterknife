package kr.butterknife.talenthouse.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kr.butterknife.talenthouse.network.request.NormalLoginReq
import kr.butterknife.talenthouse.network.request.SignUpReq
import kr.butterknife.talenthouse.network.response.NormalLoginRes
import kr.butterknife.talenthouse.network.response.SignUpRes
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

//private const val BASE_URL = "http://3.137.162.68:4000/"
private const val BASE_URL = "http://10.0.2.2:4000/"

private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
private val clientBuilder = OkHttpClient.Builder().apply {
    addInterceptor(loggingInterceptor)
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface ButterKnifeApiService {
    @POST("login/normal")
    fun login(@Body user : NormalLoginReq) : Call<NormalLoginRes>

    @POST("user")
    fun addUser(@Body user : SignUpReq) : Call<SignUpRes>
}

object ButterKnifeApi {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi)) // json to kotlin
        .baseUrl(BASE_URL)
        .client(clientBuilder.build())
        .build()

    val retrofitService : ButterKnifeApiService by lazy {
        retrofit.create(ButterKnifeApiService::class.java)
    }
}