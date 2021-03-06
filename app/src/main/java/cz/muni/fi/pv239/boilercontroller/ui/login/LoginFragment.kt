package cz.muni.fi.pv239.boilercontroller.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.repository.WebAPIRepository
import cz.muni.fi.pv239.boilercontroller.ui.main.MainActivity
import cz.muni.fi.pv239.boilercontroller.util.PrefManager
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment(context: Context?) : Fragment() {

    private val prefManager: PrefManager? by lazy { context?.let { PrefManager(it) } }
    private val apiRepository by lazy { context?.let { WebAPIRepository(it) } }

    private fun signIn(email: String, password: String) {
        apiRepository?.signIn(email, password) { user ->
            user?.let {
                prefManager?.token = user.token
                prefManager?.email = user.email
                prefManager?.password = password
                startActivity(context?.let { context -> MainActivity.newIntent(context) })
            } ?: kotlin.run {
                val toast = Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT)
                toast.show()
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