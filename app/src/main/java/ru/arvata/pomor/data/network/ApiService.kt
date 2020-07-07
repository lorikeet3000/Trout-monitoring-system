package ru.arvata.pomor.data.network

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.arvata.pomor.BASE_URL
import ru.arvata.pomor.data.UserLocalDataSource
import ru.arvata.pomor.data.getSessionCookie
import ru.arvata.pomor.data.saveSessionCookie

/*
    implementation 'com.squareup.retrofit2:retrofit:2.5.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:3.10.0'
 */

object Network {

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val sessionCookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
            //todo save cookie only from "login" url
            val sessionCookie = cookies.find {
                it.name() == "session"
            }
            if(sessionCookie != null) {
                saveSessionCookie(sessionCookie)
            }
        }

        override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
            val list = mutableListOf<Cookie>()
            val cookie = getSessionCookie(url)
            if(cookie != null) {
                list.add(cookie)
            }
            return list
        }
    }

    private val client: OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
        cookieJar(sessionCookieJar)
    }.build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}

interface ApiService {

    @POST("core/auth/login")
    suspend fun login(@Body credentials: CredentialsNetwork): Response<UserNetwork>

    @POST("indicators")
    suspend fun sendIndicators(@Body list: List<IndicatorNetwork>): Response<ResponseBody>

    @GET("summary")
    suspend fun getData(@Query(value = "start") start: String, @Query(value = "end") end: String): Response<SummaryNetwork>

    @GET("core/users")
    suspend fun getUsers(): Response<List<UserNetwork>>

    @POST("tasks")
    suspend fun sendPlan(@Body plan: CreatePlanNetwork): Response<ResponseBody>

    @PUT("tasks/{planId}")
    suspend fun editPlan(@Path("planId") planId: String, @Body plan: CreatePlanNetwork): Response<ResponseBody>

    @DELETE("tasks/{planId}")
    suspend fun deletePlan(@Path("planId") planId: String): Response<ResponseBody>

    @PUT("tasks/status/{planId}")
    suspend fun changePlanStatus(@Path("planId") planId: String, @Body status: PlanStatusNetwork ): Response<ResponseBody>
}