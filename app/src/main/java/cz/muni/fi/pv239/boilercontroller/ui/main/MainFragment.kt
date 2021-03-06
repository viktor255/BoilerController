package cz.muni.fi.pv239.boilercontroller.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.extension.toPresentableDate
import cz.muni.fi.pv239.boilercontroller.model.Boost
import cz.muni.fi.pv239.boilercontroller.model.CurrentTemperatureConfig
import cz.muni.fi.pv239.boilercontroller.model.TemperatureConfig
import cz.muni.fi.pv239.boilercontroller.repository.WebAPIRepository
import cz.muni.fi.pv239.boilercontroller.ui.detail.DetailActivity
import cz.muni.fi.pv239.boilercontroller.util.BoostNotificationWorker
import cz.muni.fi.pv239.boilercontroller.util.PrefManager
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_list.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainFragment(context: Context? = null) : Fragment() {
    private val adapter by lazy { context?.let { TemperatureConfigAdapter(it, this) } }
    private val prefManager: PrefManager? by lazy { context?.let { PrefManager(it) } }
    private val apiRepository by lazy { context?.let { WebAPIRepository(it) } }
    private var activeBoost: Boost? = null
    private var currentTemperatureConfigStatus: CurrentTemperatureConfig? = null
    private var currentTemperatureConfigs: List<TemperatureConfig> = emptyList()

    private var timer1: Timer = Timer()
    private var timer2: Timer = Timer()

    companion object {
        const val REQ_TEMPERATURE_CONFIG = 1000
        const val REQ_TEMPERATURE_CONFIG_EDIT = 1005
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_list, container, false).apply {
            recycler_view.layoutManager = LinearLayoutManager(context)
            recycler_view.adapter = adapter

            getBoostConfig()
            getAllTemperatureConfigs()
            setUpdateCurrentTemperatureAndBoostTimer()

            timer1.schedule(object : TimerTask() {
                override fun run() {
                    updateStatusBar()
                }
            }, 3000, 1000)


            add_button.setOnClickListener {
                startActivityForResult(DetailActivity.newIntent(context), REQ_TEMPERATURE_CONFIG)
            }

            boost_button.setOnClickListener {
                addBoost()
            }

            deleteBoostButton.setOnClickListener {
                deleteBoost()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        timer1.cancel()
        timer2.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            REQ_TEMPERATURE_CONFIG -> {
                val temperatureConfig =
                    data?.getParcelableExtra<TemperatureConfig>(DetailActivity.ARG_TEMPERATURE_CONFIG) ?: return
                apiRepository?.addTemperatureConfig(temperatureConfig) {
                    it?.let {
                        adapter?.addTemperatureConfig(it)
                        currentTemperatureConfigs = adapter?.getTemperatureConfigs() ?: emptyList()
                        desiredTempValue?.text = getDesiredTemperature().toString()
                    }
                }
            }

            REQ_TEMPERATURE_CONFIG_EDIT -> {
                val temperatureConfigEdit =
                    data?.getParcelableExtra<TemperatureConfig>(DetailActivity.ARG_TEMPERATURE_CONFIG) ?: return
                apiRepository?.editTemperatureConfig(temperatureConfigEdit) {
                    it?.let {
                        adapter?.editTemperatureConfig(it)
                        currentTemperatureConfigs = adapter?.getTemperatureConfigs() ?: emptyList()
                        Log.d("DESIRED-temp", getDesiredTemperature().toString())
                        desiredTempValue?.text = getDesiredTemperature().toString()
                    }
                }
            }
        }
    }

    private fun setUpdateCurrentTemperatureAndBoostTimer() {
        timer2.schedule(object : TimerTask() {
            override fun run() {
                getCurrentTemperatureAndBoost()
            }
        }, 0, 10000)
    }

    private fun addBoost() {
        apiRepository?.addBoost {
            it?.let {
                boostLayout?.visibility = View.VISIBLE
                boostActiveTill?.text = (it.time + it.duration * 60000).toPresentableDate()
                boostAuthor?.text = it.author
                activeBoost = it
                desiredTempValue?.text = getDesiredTemperature().toString()

                setBoostEndNotification(it);
            }
        }
    }

    private fun setBoostEndNotification(boost: Boost) {
        val uploadWorkRequest = OneTimeWorkRequestBuilder<BoostNotificationWorker>()
            .setInputData(workDataOf(Pair("boost-start", boost.time), Pair("boost-duration", boost.duration)))
            .setInitialDelay(boost.duration, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance().enqueueUniqueWork("boostEndNotification", ExistingWorkPolicy.REPLACE, uploadWorkRequest)
//        activeBoostNotificationId = uploadWorkRequest.id
    }

    private fun deleteBoostEndNotification() {
        WorkManager.getInstance().cancelUniqueWork("boostEndNotification")
    }

    private fun deleteBoost() {
        activeBoost?._id?.let { id ->
            apiRepository?.deleteBoost(id) {
                boostActiveTill?.text = ""
                boostLayout?.visibility = View.GONE
                boostAuthor?.text = ""
                activeBoost = null
                desiredTempValue?.text = getDesiredTemperature().toString()
                deleteBoostEndNotification()
            }
        }
    }


    private fun getDesiredTemperature(): Number {
        activeBoost?.let { return it.temperature }

        var desiredTempIsSet = false
        var desiredTemp: Number = 0
        val currentTime = SimpleDateFormat("HH:mm").format(Date())

        currentTemperatureConfigs.sortedByDescending { it.time }.forEach {
            if (it.time <= currentTime && !desiredTempIsSet) {
                desiredTemp = it.temperature
                desiredTempIsSet = true;
            }
        }
        if (!desiredTempIsSet) {
            desiredTemp = currentTemperatureConfigs.maxBy { it.time }?.temperature ?: 0
        }
        return desiredTemp
    }

    private fun getBoostConfig() {
        apiRepository?.getBoostConfig {
            it?.let {
                prefManager?.boostConfigTemperature = it.temperature
                prefManager?.boostConfigDuration = it.duration
                prefManager?.boostConfigId = it._id
            }
        }
    }

    private fun View.updateStatusBar() {
        val currentDate = System.currentTimeMillis()
        if (currentTemperatureConfigStatus?.time != null) {
            if (currentTempValue.text != desiredTempValue.text ||
                (currentDate - currentTemperatureConfigStatus!!.time) > 300000
            ) {
                statusLayout?.setBackgroundColor(ContextCompat.getColor(context, R.color.complementaryColor))
            } else {
                statusLayout?.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryLightColor))
            }
        } else {
            statusLayout?.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryLightColor))
        }
    }

    private fun getAllTemperatureConfigs() {
        apiRepository?.getAllTemperatureConfigs { temperatureConfigs ->
            temperatureConfigs?.let { it ->
                adapter?.submitList(it.sortedBy { it.time })
                currentTemperatureConfigs = it
                recycler_view.adapter = adapter
            }
        }
    }

    private fun getCurrentTemperatureAndBoost() {
        apiRepository?.getBoost { boost ->
            apiRepository?.getCurrentTemperatureConfig { temperatureConfig ->
                //  Current
                temperatureConfig?.let {
                    currentTemperatureConfigStatus = it
                    currentTempValue?.text = it.temperature.toString()
                    lastSyncValue?.text = it.time.toPresentableDate()
                }

                // Boost
                boost?.let { boost ->
                    boostLayout?.visibility = View.VISIBLE
                    boostActiveTill?.text = (boost.time + boost.duration * 60000).toPresentableDate()
                    boostAuthor?.text = boost.author
                    activeBoost = boost
                }
                if (boost == null) {
                    boostActiveTill?.text = ""
                    boostAuthor?.text = ""
                    boostLayout?.visibility = View.GONE
                    activeBoost = boost
                }
                desiredTempValue?.text = getDesiredTemperature().toString()
            }
        }
    }
}