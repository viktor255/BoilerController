package cz.muni.fi.pv239.boilercontroller.ui.splash

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cz.muni.fi.pv239.boilercontroller.ui.list.ListActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        routeToAppropriatePage()
        finish()
    }

    private fun routeToAppropriatePage() {
        if (false) {
            Log.e("ROUTE-TO-LOGIN", "login not implemented")
        } else {
            startActivity(ListActivity.newIntent(this))
        }
    }

}