package com.sempatpanick.githubuserlookup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.edit
import com.sempatpanick.githubuserlookup.databinding.ActivitySettingsBinding
import com.sempatpanick.githubuserlookup.receiver.AlarmReceiver

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val PREFS_NAME = "settings"
        private const val SWITCH_NOTIFICATION = "switch_notification"
    }

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmReceiver = AlarmReceiver()

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.settings)

        binding.swNotificationReminder.isChecked = sharedPreferences.getBoolean(SWITCH_NOTIFICATION, false)

        binding.swNotificationReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val repeatTime = "09:00"
                val repeatMessage = "Halo, sudahkah anda membuka aplikasi hari ini?"
                alarmReceiver.setRepeatingAlarm(this, repeatTime, repeatMessage)
                alarmReceiver.isAlarmSet(this)
                Toast.makeText(this, getString(R.string.activated_notification), Toast.LENGTH_SHORT).show()
            } else {
                alarmReceiver.cancelAlarm(this)
                alarmReceiver.isAlarmSet(this)
                Toast.makeText(this, getString(R.string.deactivated_notification), Toast.LENGTH_SHORT).show()
            }

            saveSwitchNotification(isChecked)
        }
        binding.btnChangeLanguage.setOnClickListener {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveSwitchNotification(isChecked: Boolean) {
        sharedPreferences.edit {
            putBoolean(SWITCH_NOTIFICATION, isChecked)
        }
    }
}