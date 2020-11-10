package hu.bme.aut.android.gifthing.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import hu.bme.aut.android.gifthing.AppPreferences
import hu.bme.aut.android.gifthing.R
import hu.bme.aut.android.gifthing.ui.WelcomeActivity

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val logoutBtn: TextView = root.findViewById(R.id.btnLogout)

        logoutBtn.setOnClickListener {
            AppPreferences.currentId = null
            AppPreferences.username = null
            AppPreferences.email = null
            AppPreferences.roles = null
            AppPreferences.token = null
            val intent = Intent(context, WelcomeActivity::class.java).apply{
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
            //TODO: login from server
        }

        return root
    }
}