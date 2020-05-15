package cz.muni.fi.pv239.boilercontroller.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.util.PrefManager

class MainActivity : AppCompatActivity() {

//    private val prefManager: PrefManager? by lazy { PrefManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_container)

//        prefManager?.lastAppStartDate = System.currentTimeMillis()

        val fragment = ListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
//            .add(R.id.fragment_container, fragment)
//            .add(R.id.status_bar_fragment, fragment2)
            .commit()
    }
}
