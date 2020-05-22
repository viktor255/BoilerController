package cz.muni.fi.pv239.boilercontroller.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.extension.toPresentableDate
import cz.muni.fi.pv239.boilercontroller.model.Boost
import cz.muni.fi.pv239.boilercontroller.model.CurrentTemperatureConfig
import cz.muni.fi.pv239.boilercontroller.model.TemperatureConfig
import cz.muni.fi.pv239.boilercontroller.repository.TemperatureConfigRepository
import cz.muni.fi.pv239.boilercontroller.ui.detail.DetailActivity
import cz.muni.fi.pv239.boilercontroller.util.PrefManager
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.fragment_list.view.desiredTempValue
import java.text.SimpleDateFormat
import java.util.*

class ListFragment : Fragment() {
    private val adapter by lazy { context?.let { TemperatureConfigAdapter(it, this) } }
    private val prefManager: PrefManager? by lazy { context?.let { PrefManager(it) } }
    private val temperatureConfigRepository by lazy { context?.let { TemperatureConfigRepository(it) } }
    private var activeBoost: Boost? = null
    private var currentTemperatureConfigStatus: CurrentTemperatureConfig? = null
    private var currentTemperatureConfigs: List<TemperatureConfig> = emptyList()

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

            getBoostConfig()

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    getTemperatureConfigurations()
                }
            }, 0, 10000)

            Timer().schedule(object : TimerTask() {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            REQ_TEMPERATURE_CONFIG -> {
                val temperatureConfig =
                    data?.getParcelableExtra<TemperatureConfig>(DetailActivity.ARG_TEMPERATURE_CONFIG) ?: return
                temperatureConfigRepository?.addTemperatureConfig(temperatureConfig) {
                    it?.let {
                        adapter?.addTemperatureConfig(it)
                        currentTemperatureConfigs = adapter?.getTemperatureConfigs() ?: emptyList()
                        desiredTempValue.text = getDesiredTemperature().toString()
                    }
                }
            }

            REQ_TEMPERATURE_CONFIG_EDIT -> {
                val temperatureConfigEdit =
                    data?.getParcelableExtra<TemperatureConfig>(DetailActivity.ARG_TEMPERATURE_CONFIG) ?: return
                temperatureConfigRepository?.editTemperatureConfig(temperatureConfigEdit) {
                    it?.let {
                        adapter?.editTemperatureConfig(it)
                        currentTemperatureConfigs = adapter?.getTemperatureConfigs() ?: emptyList()
                        Log.d("DESIRED-temp", getDesiredTemperature().toString())
                        desiredTempValue.text = getDesiredTemperature().toString()
                    }
                }
            }
        }
    }

    private fun addBoost() {
        temperatureConfigRepository?.addBoost {
            it?.let {
                boostLayout.visibility = View.VISIBLE
                boostActiveTill.text = (it.time + it.duration * 60000).toPresentableDate()
                desiredTempValue.text = it.temperature.toString()
                activeBoost = it
                desiredTempValue.text = getDesiredTemperature().toString()
            }
        }
    }

    private fun deleteBoost() {
        activeBoost?._id?.let { id ->
            temperatureConfigRepository?.deleteBoost(id) {
                boostActiveTill.text = ""
                boostLayout.visibility = View.GONE
                activeBoost = null
                desiredTempValue.text = getDesiredTemperature().toString()
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
        temperatureConfigRepository?.getBoostConfig {
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
                statusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.complementaryColor))
            } else {
                statusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryLightColor))
            }
        } else {
            statusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryLightColor))
        }
    }

    private fun getTemperatureConfigurations() {
        temperatureConfigRepository?.getAllTemperatureConfigs { temperatureConfigs ->
            temperatureConfigRepository?.getBoost { boost ->
                temperatureConfigRepository?.getCurrentTemperatureConfig { temperatureConfig ->
                    //  Current
                    temperatureConfig?.let {
                        currentTemperatureConfigStatus = it
                        currentTempValue.text = it.temperature.toString()
                        lastSyncValue.text = it.time.toPresentableDate()
                    }

                    // temperatureConfigs
                    temperatureConfigs?.let {
                        adapter?.submitList(temperatureConfigs.sortedBy { it.time })
                        currentTemperatureConfigs = it
                        recycler_view.adapter = adapter

                        // Boost
                        boost?.let {
                            boostLayout.visibility = View.VISIBLE
                            boostActiveTill.text = (boost.time + boost.duration * 60000).toPresentableDate()
                            boostAuthor.text = boost.author
                            activeBoost = boost
                        }
                        if (boost == null) {
                            boostActiveTill.text = ""
                            boostAuthor.text = ""
                            boostLayout.visibility = View.GONE
                            activeBoost = boost
                        }
                        desiredTempValue.text = getDesiredTemperature().toString()
                    }
                }
            }
        }
    }
}