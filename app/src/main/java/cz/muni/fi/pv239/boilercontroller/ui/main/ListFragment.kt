package cz.muni.fi.pv239.boilercontroller.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.extension.toPresentableDate
import cz.muni.fi.pv239.boilercontroller.model.TemperatureConfig
import cz.muni.fi.pv239.boilercontroller.repository.TemperatureConfigRepository
import cz.muni.fi.pv239.boilercontroller.ui.detail.DetailActivity
import cz.muni.fi.pv239.boilercontroller.util.PrefManager
import kotlinx.android.synthetic.main.fragment_list.view.*

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
            last_app_start_text_view.text = prefManager?.lastAppStartDate?.toPresentableDate()

            recycler_view.layoutManager = LinearLayoutManager(context)

            temperatureConfigRepository.getAllTemperatureConfigs { temperatureConfigs ->
                temperatureConfigs?.let {
                    adapter.submitList(temperatureConfigs)
                    recycler_view.adapter = adapter
                }
            }

            add_button.setOnClickListener {
                startActivityForResult(DetailActivity.newIntent(context), REQ_TEMPERATURE_CONFIG)
            }
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