package cz.muni.fi.pv239.boilercontroller.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.repository.TemperatureConfigRepository
import cz.muni.fi.pv239.boilercontroller.ui.list.ListActivity
import cz.muni.fi.pv239.boilercontroller.util.PrefManager
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment(context: Context?) : Fragment() {

    private val prefManager: PrefManager? by lazy { context?.let { PrefManager(it) } }
    private val temperatureConfigRepository by lazy { context?.let { TemperatureConfigRepository(it) } }

    private fun signIn(email: String, password: String) {
        temperatureConfigRepository?.signIn(email, password) { user ->
            user?.let {
                prefManager?.token = user.token
                prefManager?.email = user.email
                prefManager?.password = password
                startActivity(context?.let { context -> ListActivity.newIntent(context) })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.login_button.setOnClickListener {
            signIn(email_text_input.text.toString(), password_text_input.text.toString());
        }

        return view
    }
}