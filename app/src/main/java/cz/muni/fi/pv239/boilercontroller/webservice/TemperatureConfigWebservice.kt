package cz.muni.fi.pv239.boilercontroller.webservice

import cz.muni.fi.pv239.boilercontroller.model.Boost
import cz.muni.fi.pv239.boilercontroller.model.TemperatureConfig
import cz.muni.fi.pv239.boilercontroller.model.User
import cz.muni.fi.pv239.boilercontroller.webservice.response.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface TemperatureConfigWebservice {

    @GET("/timeConfig")
    fun getTemperatureConfigs(): Call<TemperatureConfigsResponse>

    @POST("/timeConfig")
    fun addTemperatureConfig(@Body temperatureConfig: TemperatureConfig, @Query("token") token: String): Call<TemperatureConfigResponse>

    @PATCH("/timeConfig/{id}")
    fun editTemperatureConfig(@Body temperatureConfig: TemperatureConfig, @Path("id") id: String?,  @Query("token") token: String): Call<TemperatureConfigResponse>

    @DELETE("/timeConfig/{id}")
    fun deleteTemperatureConfig(@Path("id") id: String?, @Query("token") token: String): Call<TemperatureConfigResponse>

    @GET("/currentTimeConfig")
    fun getCurrentTemperatureConfig(): Call<CurrentTemperatureConfigResponse>

    @GET("/boost")
    fun getBoost(): Call<BoostResponse>

    @GET("/boostConfig/api")
    fun getBoostConfig(@Query("token") token: String): Call<BoostConfigResponse>

    @POST("/boost")
    fun addBoost(@Body boost: Boost, @Query("token") token: String): Call<BoostResponse>

    @DELETE("/boost/{id}")
    fun deleteBoost(@Path("id") id: String?, @Query("token") token: String): Call<BoostResponse>

    @POST("/user/signin")
    fun jsonLogin(@Body params: RequestBody): Call<User>

}