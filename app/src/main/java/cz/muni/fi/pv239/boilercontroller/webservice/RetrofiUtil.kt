package cz.muni.fi.pv239.boilercontroller.webservice

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtil {


    fun createWebService(): TemperatureConfigWebservice {
        val client =  OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor("demo@demo.com"))
            .build()

        val retrofit = Retrofit.Builder()
//            .baseUrl("https://jsonplaceholder.typicode.com")
            .baseUrl("https://bojler-controller.herokuapp.com")
//            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(TemperatureConfigWebservice::class.java)
    }

   fun createJsonRequestBody(vararg params: Pair<String, String>) =
        JSONObject(mapOf(*params)).toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
}