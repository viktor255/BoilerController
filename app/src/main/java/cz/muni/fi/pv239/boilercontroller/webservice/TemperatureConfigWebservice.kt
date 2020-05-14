package cz.muni.fi.pv239.boilercontroller.webservice

import cz.muni.fi.pv239.boilercontroller.webservice.response.BoostResponse
import cz.muni.fi.pv239.boilercontroller.webservice.response.CurrentTemperatureConfigResponse
import cz.muni.fi.pv239.boilercontroller.webservice.response.TemperatureConfigResponse
import retrofit2.Call
import retrofit2.http.GET

interface TemperatureConfigWebservice {

    @GET("/timeConfig")
    fun getTemperatureConfigs(): Call<TemperatureConfigResponse>

    @GET("/currentTimeConfig")
    fun getCurrentTemperatureConfig(): Call<CurrentTemperatureConfigResponse>

    @GET("/boost")
    fun getBoost(): Call<BoostResponse>
}