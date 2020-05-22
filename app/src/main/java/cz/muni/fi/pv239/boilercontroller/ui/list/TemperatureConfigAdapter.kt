package cz.muni.fi.pv239.boilercontroller.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.model.TemperatureConfig
import cz.muni.fi.pv239.boilercontroller.repository.TemperatureConfigRepository
import cz.muni.fi.pv239.boilercontroller.ui.detail.DetailActivity
import kotlinx.android.synthetic.main.item_temperature_config.view.*

class TemperatureConfigAdapter(private val context: Context, private val fragment: ListFragment): RecyclerView.Adapter<TemperatureConfigAdapter.TemperatureConfigViewHolder>() {

    private val temperatureConfigs: MutableList<TemperatureConfig> = mutableListOf()
    private val temperatureConfigRepository by lazy { TemperatureConfigRepository(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemperatureConfigViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_temperature_config, parent, false)
        return TemperatureConfigViewHolder(view)
    }

    override fun getItemCount(): Int = temperatureConfigs.size

    override fun onBindViewHolder(holder: TemperatureConfigViewHolder, position: Int) {
        holder.bind(temperatureConfigs[position])
    }

    fun getTemperatureConfigs(): List<TemperatureConfig> {
        return temperatureConfigs
    }

    fun addTemperatureConfig(temperatureConfig: TemperatureConfig) {
        temperatureConfigs.add(temperatureConfig)
        temperatureConfigs.sortBy { it.time }
        notifyDataSetChanged()
    }

    fun editTemperatureConfig(temperatureConfig: TemperatureConfig) {
        val index = temperatureConfigs.indexOfFirst{ e -> e.time == temperatureConfig.time}
        temperatureConfigs[index] = temperatureConfig
        notifyDataSetChanged()
    }

    fun deleteTemperatureConfig(position: Int) {
        temperatureConfigRepository.deleteConfig(temperatureConfigs[position]) { temperatureConfig ->
            temperatureConfig?.let {
                temperatureConfigs.removeAt(position)
                notifyDataSetChanged()
            }
        }
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

            view.deleteTemperatureConfigButton.setOnClickListener{
                deleteTemperatureConfig(adapterPosition)
            }

            view.editTemperatureConfigButton.setOnClickListener {
                val intent = DetailActivity.newIntent(context)
                intent.putExtra(DetailActivity.ARG_TEMPERATURE_CONFIG, temperatureConfig)
                fragment.startActivityForResult(intent, ListFragment.REQ_TEMPERATURE_CONFIG_EDIT)
            }
        }
    }
}