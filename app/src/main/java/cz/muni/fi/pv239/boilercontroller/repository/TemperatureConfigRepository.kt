package cz.muni.fi.pv239.boilercontroller.repository

import android.util.Log
import cz.muni.fi.pv239.boilercontroller.model.Boost
import cz.muni.fi.pv239.boilercontroller.model.CurrentTemperatureConfig
import cz.muni.fi.pv239.boilercontroller.model.TemperatureConfig
import cz.muni.fi.pv239.boilercontroller.webservice.RetrofitUtil
import cz.muni.fi.pv239.boilercontroller.webservice.response.BoostResponse
import cz.muni.fi.pv239.boilercontroller.webservice.response.CurrentTemperatureConfigResponse
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
//                    Log.d("HTTTP", response.body().toString())
                } else {
                    Log.e("HTTTP-ERR", response.errorBody().toString());
                }
            }
        })
    }

    fun getCurrentTemperatureConfig(callback: (CurrentTemperatureConfig?) -> Unit) {
        temperatureWebservice.getCurrentTemperatureConfig().enqueue(object: Callback<CurrentTemperatureConfigResponse> {
            override fun onFailure(call: Call<CurrentTemperatureConfigResponse>, t: Throwable) {
                callback.invoke(null)
                Log.d("HTTTP-Error", call.toString())
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<CurrentTemperatureConfigResponse>,
                response: Response<CurrentTemperatureConfigResponse>
            ) {
                if (response.isSuccessful) {
                    val temperatureConfig = response.body()?.obj
                    callback.invoke(temperatureConfig)
//                    Log.d("HTTTP", response.body().toString())
                } else {
                    Log.e("HTTTP-ERR", response.errorBody().toString());
                }
            }
        })
    }

    fun getBoost(callback: (Boost?) -> Unit) {
        temperatureWebservice.getBoost().enqueue(object: Callback<BoostResponse> {
            override fun onFailure(call: Call<BoostResponse>, t: Throwable) {
                callback.invoke(null)
                Log.d("HTTTP-Error", call.toString())
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<BoostResponse>,
                response: Response<BoostResponse>
            ) {
                if (response.isSuccessful) {
                    val boost = response.body()?.obj
                    callback.invoke(boost)
//                    Log.d("HTTTP", response.body().toString())
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