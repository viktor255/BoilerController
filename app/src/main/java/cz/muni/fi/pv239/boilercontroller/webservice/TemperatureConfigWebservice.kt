package cz.muni.fi.pv239.boilercontroller.webservice

import cz.muni.fi.pv239.boilercontroller.webservice.response.TemperatureConfigResponse
import retrofit2.Call
import retrofit2.http.GET

interface TemperatureConfigWebservice {

//    @GET("/users")
    @GET("/timeConfig")
    fun getTemperatureConfigs(): Call<TemperatureConfigResponse>
}