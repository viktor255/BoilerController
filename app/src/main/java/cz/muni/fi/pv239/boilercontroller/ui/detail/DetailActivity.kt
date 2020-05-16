package cz.muni.fi.pv239.boilercontroller.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.model.TemperatureConfig

class DetailActivity : AppCompatActivity() {

    companion object {
        const val ARG_TEMPERATURE_CONFIG = "arg_temperature_config"

        fun newIntent(context: Context) = Intent(context, DetailActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_container)

        val temperatureConfig = intent.getParcelableExtra<TemperatureConfig>(ARG_TEMPERATURE_CONFIG) ?: TemperatureConfig()

        if (savedInstanceState == null) {
            val fragment = DetailFragment(temperatureConfig)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}