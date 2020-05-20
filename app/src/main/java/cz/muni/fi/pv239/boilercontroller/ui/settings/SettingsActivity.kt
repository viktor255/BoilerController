package cz.muni.fi.pv239.boilercontroller.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.muni.fi.pv239.boilercontroller.R

class SettingsActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_container)

        supportFragmentManager
        val fragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}