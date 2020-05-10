package cz.muni.fi.pv239.boilercontroller.repository

import android.util.Log
import cz.muni.fi.pv239.boilercontroller.model.TemperatureConfig
import cz.muni.fi.pv239.boilercontroller.webservice.RetrofitUtil
import cz.muni.fi.pv239.boilercontroller.webservice.response.TemperatureConfigResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TemperatureConfigRepository {

    private val temperatureWebservice by lazy { RetrofitUtil.createWebService() }

    fun getAllTemperatureConfigs(callback: (List<TemperatureConfig>?) -> Unit) {
        temperatureWebservice.getTemperatureConfigs().enqueue(object: Callback<TemperatureConfigResponse> {
            override fun onFailure(call: Call<TemperatureConfigResponse>, t: Throwable) {
                callback.invoke(null)
                Log.d("HTTTP-Error", call.toString())
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<TemperatureConfigResponse>,
                response: Response<TemperatureConfigResponse>
            ) {
                if (response.isSuccessful) {
//                    val temperatureConfigs = response.body()?.map { TemperatureConfig(it.temperature, it.time) }
                    val temperatureConfigs = response.body()?.obj
                    callback.invoke(temperatureConfigs)
                    Log.d("HTTTP", response.body().toString())
                } else {
                    Log.e("HTTTP-ERR", response.errorBody().toString());
                }
            }
        })
    }

    fun insertTemperatureConfig(temperatureConfig: TemperatureConfig) {
        TODO("Not yet implemented")
    }

}