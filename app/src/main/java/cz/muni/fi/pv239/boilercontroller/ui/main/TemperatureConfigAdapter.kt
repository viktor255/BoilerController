package cz.muni.fi.pv239.boilercontroller.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.model.TemperatureConfig
import kotlinx.android.synthetic.main.item_temperature_config.view.*

class TemperatureConfigAdapter: RecyclerView.Adapter<TemperatureConfigAdapter.TemperatureConfigViewHolder>() {

    private val temperatureConfigs: MutableList<TemperatureConfig> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemperatureConfigViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_temperature_config, parent, false)
        return TemperatureConfigViewHolder(view)
    }

    override fun getItemCount(): Int = temperatureConfigs.size

    override fun onBindViewHolder(holder: TemperatureConfigViewHolder, position: Int) {
        holder.bind(temperatureConfigs[position])
    }

    fun addTemperatureConfig(temperatureConfig: TemperatureConfig) {
        temperatureConfigs.add(temperatureConfig)
        notifyDataSetChanged()
    }

    fun submitList(temperatureConfigs: List<TemperatureConfig>) {
        this.temperatureConfigs.clear()
        this.temperatureConfigs.addAll(temperatureConfigs)
        notifyDataSetChanged()
    }

    inner class TemperatureConfigViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        fun bind(temperatureConfig: TemperatureConfig) {
            view.temperature.text = temperatureConfig.temperature.toString()
            view.time.text = temperatureConfig.time
        }
    }
}