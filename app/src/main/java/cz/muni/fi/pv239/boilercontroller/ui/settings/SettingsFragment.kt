package cz.muni.fi.pv239.boilercontroller.ui.settings

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import cz.muni.fi.pv239.boilercontroller.R
import cz.muni.fi.pv239.boilercontroller.repository.CustomPreferenceDataStore
import cz.muni.fi.pv239.boilercontroller.util.MinMaxFilter


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        retainInstance = true

        val preferenceManager = preferenceManager
        preferenceManager.preferenceDataStore = CustomPreferenceDataStore(context)

        setPreferencesFromResource(R.xml.preferences, rootKey)

        val boostTemperature: EditTextPreference? = findPreference("boostTemperature")
        boostTemperature?.setOnBindEditTextListener {
            it.filters = arrayOf<InputFilter>(MinMaxFilter(1, 6))
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }

        val boostDuration: EditTextPreference? = findPreference("boostDuration")
        boostDuration?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }
}