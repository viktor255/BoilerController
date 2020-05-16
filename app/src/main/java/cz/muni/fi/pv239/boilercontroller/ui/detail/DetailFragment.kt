package cz.muni.fi.pv239.boilercontroller.ui.detail

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.model.TemperatureConfig
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailFragment(private var temperatureConfig: TemperatureConfig) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        context?.let { context ->
            view.temperature_config_time_input.text = temperatureConfig.time
            view.temperature_config_time_input.setOnClickListener {
                val listener = TimePickerDialog.OnTimeSetListener {
                        _,
                        hourOfDay,
                        minute ->
                    fun calculateHourString(hourOrMinute: Int): String { return if (hourOrMinute > 9) "$hourOrMinute" else "0$hourOrMinute" }
                    temperatureConfig = temperatureConfig.copy(time = "${calculateHourString(hourOfDay)}:${calculateHourString(minute)}")
                    view.temperature_config_time_input.text = temperatureConfig.time
                }

                TimePickerDialog(
                    context,
                    listener,
                12,
                0,
                true
                ).show()
            }
        }

        view.numberpicker_main_picker.maxValue = 6
        view.numberpicker_main_picker.minValue = 0
        view.numberpicker_main_picker.value = temperatureConfig.temperature.toInt()
        view.numberpicker_main_picker.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            view.numberpicker_main_picker.value = newValue
            temperatureConfig = temperatureConfig.copy(temperature = newValue)
        }

        view.save_button.setOnClickListener {
            val intent = Intent()
            intent.putExtra(DetailActivity.ARG_TEMPERATURE_CONFIG, temperatureConfig)

            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        }

        return view
    }
}