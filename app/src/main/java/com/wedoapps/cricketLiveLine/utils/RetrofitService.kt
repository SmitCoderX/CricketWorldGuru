package cricket.t20.api

import com.google.gson.JsonObject
import com.wedoapps.cricketLiveLine.utils.ApplicationList
import com.wedoapps.cricketLiveLine.utils.CommonResponse
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.GetApplicationList
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface RetrofitService {

    @GET("ads/public/api/get_apps")
    suspend fun getAllAppsList() : Response<GetApplicationList>

    @POST("ads/public/api/get_app")
    fun sendApplicationNameToServer(@Body jsonObject: JsonObject): Call<ApplicationList>

    @POST("app/public/api/add_token")
    fun sendPushTokenToServer(@Body jsonObject: JsonObject): Call<CommonResponse>


   /* fun doSendFcmToken(
        @Body
        body: JsonObject
    ): Call<CommonResponseModel<TokenModelData>>*/


    companion object {
        var retrofitService: RetrofitService? = null
        fun getInstance() : RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }
}