package cz.muni.fi.pv239.boilercontroller.webservice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtil {

    fun createWebService(): TemperatureConfigWebservice {
        val retrofit = Retrofit.Builder()
//            .baseUrl("https://jsonplaceholder.typicode.com")
            .baseUrl("https://bojler-controller.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(TemperatureConfigWebservice::class.java)
    }
}