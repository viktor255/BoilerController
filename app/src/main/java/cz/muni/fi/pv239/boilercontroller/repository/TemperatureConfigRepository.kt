package cz.muni.fi.pv239.boilercontroller.repository

import android.content.Context
import android.util.Log
import cz.muni.fi.pv239.boilercontroller.model.*
import cz.muni.fi.pv239.boilercontroller.util.PrefManager
import cz.muni.fi.pv239.boilercontroller.webservice.RetrofitUtil
import cz.muni.fi.pv239.boilercontroller.webservice.RetrofitUtil.createJsonRequestBody
import cz.muni.fi.pv239.boilercontroller.webservice.response.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TemperatureConfigRepository(context: Context) {

    private val temperatureWebservice by lazy { RetrofitUtil.createWebService() }
    private val prefManager: PrefManager by lazy { PrefManager(context) }

    fun getAllTemperatureConfigs(callback: (List<TemperatureConfig>?) -> Unit) {
        temperatureWebservice.getTemperatureConfigs().enqueue(object: Callback<TemperatureConfigsResponse> {
            override fun onFailure(call: Call<TemperatureConfigsResponse>, t: Throwable) {
                callback.invoke(null)
                Log.d("HTTTP-Error", call.toString())
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<TemperatureConfigsResponse>,
                response: Response<TemperatureConfigsResponse>
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

    fun getBoostConfig(callback: (BoostConfig?) -> Unit) {
        Log.d("TOKEN-aa", prefManager.token.toString())
        prefManager.token?.let {
            temperatureWebservice.getBoostConfig(it).enqueue(object : Callback<BoostConfigResponse> {
                override fun onFailure(call: Call<BoostConfigResponse>, t: Throwable) {
                    callback.invoke(null)
                    Log.d("HTTTP-Error", call.toString())
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<BoostConfigResponse>,
                    response: Response<BoostConfigResponse>
                ) {
                    if (response.isSuccessful) {
                        val boost = response.body()?.obj
                        callback.invoke(boost)
                      Log.d("BOOST-config", boost.toString())
                    } else {
                        Log.e("HTTTP-ERR", response.errorBody().toString());
                    }
                }
            })
        }
    }

    fun addBoost(callback: (Boost?) -> Unit) {
        prefManager.token?.let { token ->
            prefManager.email?.let { email ->
                val temp = prefManager.boostConfigTemperature
                val duration = prefManager.boostConfigDuration
                val time: Long = duration * 60000 + System.currentTimeMillis()
                val newBoost = Boost(null, email, duration, temp, time)

                temperatureWebservice.addBoost(newBoost, token)
                    .enqueue(object : Callback<BoostResponse> {
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
                                Log.d("BOOST", boost.toString())
                            } else {
                                Log.e("HTTTP-ERR", response.errorBody().toString());
                            }
                        }
                    })
            }
        }
    }

    fun deleteBoost(id: String, callback: (Boost?) -> Unit) {
        prefManager.token?.let { token ->
                temperatureWebservice.deleteBoost(id, token)
                    .enqueue(object : Callback<BoostResponse> {
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
                                Log.d("BOOST", boost.toString())
                            } else {
                                Log.e("HTTTP-ERR", response.errorBody().toString());
                            }
                        }
                    })
            }
    }

    fun insertTemperatureConfig(temperatureConfig: TemperatureConfig) {
        TODO("Not yet implemented")
    }



    fun signIn(email: String, password: String, callback: (User?) -> Unit) {
        val req = createJsonRequestBody(
            "email" to email, "password" to password)
        temperatureWebservice.jsonLogin(req).enqueue(object: Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                callback.invoke(null)
                Log.d("HTTTP-Error", call.toString())
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                if (response.isSuccessful) {
                    val user = response.body()
                    callback.invoke(user)
                    Log.d("HTTTP", response.body().toString())
                    Log.d("TOKEN ", user.toString())
                } else {
                    Log.e("HTTTP-ERR", response.errorBody().toString());
                }
            }
        })
    }

    fun deleteConfig(temperatureConfig: TemperatureConfig, callback: (TemperatureConfig?) -> Unit) {
        prefManager.token?.let {
            temperatureWebservice.deleteTimeConfig(temperatureConfig._id, it)
                .enqueue(object : Callback<TemperatureConfigResponse> {
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
                            val deletedTemperatureConfig = response.body()?.obj
                            callback.invoke(deletedTemperatureConfig)
                            //                    Log.d("HTTTP", response.body().toString())
                        } else {
                            Log.e("HTTTP-ERR", response.errorBody().toString());
                        }
                    }
                })
        }

    }

}