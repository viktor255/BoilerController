package cz.muni.fi.pv239.boilercontroller.ui.main


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.ui.login.LoginActivity
import cz.muni.fi.pv239.boilercontroller.ui.settings.SettingsActivity
import cz.muni.fi.pv239.boilercontroller.util.PrefManager

class MainActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    private val prefManager: PrefManager? by lazy { PrefManager(this) }

    private fun logout() {
        prefManager?.email = null
        prefManager?.password = null
        prefManager?.token = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_container)

        val fragment = MainFragment(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(SettingsActivity.newIntent(this))
                true
            }
            R.id.logout -> {
                logout()
                startActivity(LoginActivity.newIntent(this))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
