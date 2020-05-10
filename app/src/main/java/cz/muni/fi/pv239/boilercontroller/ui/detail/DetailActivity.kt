package cz.muni.fi.pv239.boilercontroller.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.muni.fi.pv239.boilercontroller.R

class DetailActivity : AppCompatActivity() {

    companion object {
        const val ARG_TEMPERATURE_CONFIG = "arg_note"

        fun newIntent(context: Context) = Intent(context, DetailActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_container)

        if (savedInstanceState == null) {
            val fragment = DetailFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}