package cz.muni.fi.pv239.boilercontroller.ui.main


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
import cz.muni.fi.pv239.boilercontroller.repository.TemperatureConfigRepository
import cz.muni.fi.pv239.boilercontroller.ui.detail.DetailActivity
import cz.muni.fi.pv239.boilercontroller.util.PrefManager
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_temperature_config.view.*
import java.text.SimpleDateFormat
import java.util.*

class ListFragment : Fragment() {

    private val adapter = TemperatureConfigAdapter()
    private val prefManager: PrefManager? by lazy { context?.let { PrefManager(it) } }
    private val temperatureConfigRepository by lazy { TemperatureConfigRepository() }

    companion object {
        const val REQ_TEMPERATURE_CONFIG = 1000
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_list, container, false).apply {
//            last_app_start_text_view.text = prefManager?.lastAppStartDate?.toPresentableDate()

            recycler_view.layoutManager = LinearLayoutManager(context)

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    temperatureConfigRepository.getAllTemperatureConfigs { temperatureConfigs ->
                        temperatureConfigRepository.getBoost { boost ->
                            temperatureConfigRepository.getCurrentTemperatureConfig { temperatureConfig ->
                                //  Current
                                temperatureConfig?.let {
                                    currentTempValue.text = temperatureConfig.temperature.toString()
                                    lastSyncValue.text = temperatureConfig.time.toPresentableDate()
                                }
                                // Current

                                // temperatureConfigs
                                temperatureConfigs?.let {
                                    adapter.submitList(temperatureConfigs.sortedBy { it.time })
                                    recycler_view.adapter = adapter

                                    // desired logic
                                    var desiredTempIsSet = false

                                    // Boost
                                    boost?.let {
                                        boostLayout.visibility = View.VISIBLE
                                        boostActiveTill.text = (boost.time + boost.duration * 60000).toPresentableDate()
                                        desiredTempValue.text = boost.temperature.toString()
                                        desiredTempIsSet = true
                                    }
                                    if (boost == null) {
                                        boostActiveTill.text = ""
                                        boostLayout.visibility = View.GONE
                                        desiredTempIsSet = false
                                    }
                                    //  Boost

                                    //  calculationOfDesired if boost is not set
                                    val currentTime = SimpleDateFormat("HH:mm").format(Date())
                                    temperatureConfigs.sortedByDescending { it.time }.forEach {
                                        if (it.time <= currentTime && !desiredTempIsSet) {
                                            desiredTempValue.text = it.temperature.toString()
//                                            Log.d("DESIRED", "setting " + it.temperature + " from time " + it.time)
                                            desiredTempIsSet = true;
                                        }
                                    }
                                    if (!desiredTempIsSet) {
                                        desiredTempValue.text = temperatureConfigs.maxBy { it.time }?.temperature.toString()
                                    }
                                    // desired logic end
                                }
                                // temperatureConfigs end
                            }
                        }

                    }
                }
            }, 0, 10000)

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    if(currentTempValue.text != desiredTempValue.text) {
                        statusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.complementaryColor))
                    } else {
                        statusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryLightColor))
                    }
                }
            }, 1000, 3000)



            add_button.setOnClickListener {
                startActivityForResult(DetailActivity.newIntent(context), REQ_TEMPERATURE_CONFIG)
            }

//            deleteTemperatureConfigButton.setOnClickListener {
//                Log.d("DELETE-pushed", view.toString())
//            }
        }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode != Activity.RESULT_OK) return
//
//        when (requestCode) {
//            REQ_TEMPERATURE_CONFIG -> {
//                val temperatureConfig = data?.getParcelableExtra<TemperatureConfig>(DetailActivity.ARG_TEMPERATURE_CONFIG) ?: return
//                adapter.addTemperatureConfig(temperatureConfig)
//
//                temperatureConfigRepository.insertTemperatureConfig(temperatureConfig)
//            }
//        }
//    }
}