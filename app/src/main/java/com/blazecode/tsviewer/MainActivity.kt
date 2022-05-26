package com.blazecode.tsviewer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.blazecode.tsviewer.databinding.ActivityMainBinding
import com.blazecode.tsviewer.ui.GraphFragment
import com.blazecode.tsviewer.ui.MainFragment
import com.blazecode.tsviewer.util.notification.NotificationManager
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.aboutlibraries.LibsBuilder
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.commit { replace(R.id.fragment_container, MainFragment()) }

        val demoModeMenuItem = binding.toolbar.menu.findItem(R.id.action_demo_mode)
        if (BuildConfig.DEBUG) demoModeMenuItem.isVisible = true

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_source -> {
                    openLink(getString(R.string.github_source_url))
                    return@setOnMenuItemClickListener true
                }

                R.id.action_licenses -> {
                    LibsBuilder()
                        .withLicenseShown(true)
                        .withAboutIconShown(true)
                        .withVersionShown(true)
                        .withActivityTitle(getString(R.string.licenses))
                        .withAboutDescription("test description")
                        .start(this)

                    return@setOnMenuItemClickListener true
                }

                R.id.action_send_email -> {
                    sendMail("Report")
                    return@setOnMenuItemClickListener true
                }

                R.id.action_demo_mode -> {
                    demoModeMenuItem.isChecked = !demoModeMenuItem.isChecked
                    demoMode(demoModeMenuItem.isChecked )
                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.action_start -> {
                    supportFragmentManager.commit { replace(R.id.fragment_container, MainFragment()) }
                    true
                }
                R.id.action_graph -> {
                    supportFragmentManager.commit { replace(R.id.fragment_container, GraphFragment(EmptyCoroutineContext)) }
                    true
                }
                else -> false
            }
        }

        //CREATE NOTIFICATION CHANNEL IF FIRST START
        if (isFirstStart()) {
            val notificationManager = NotificationManager(this)
            notificationManager.createChannel()
        }

        //OPTIMIZE TOOLBAR HEIGHT
        val layoutParams = binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.height = resources.configuration.densityDpi

        checkBatteryOptimization()
    }

    private fun sendMail(subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_address)))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject + " - " + getString(R.string.app_name))

        intent.putExtra(
            Intent.EXTRA_TEXT,
            "App Version: ${BuildConfig.VERSION_NAME}" +
                    "\nAndroid Version: ${Build.VERSION.SDK_INT}" +
                    "\nDeviceInfo: ${Build.MANUFACTURER} ${Build.MODEL}" +
                    "\nDeviceLanguage: ${Locale.getDefault().language}" +
                    "\n\nPlease consider attaching a screenshot or recording." +
                    "\nPlease describe your issue below this line.\n\n"
        )

        startActivity(Intent.createChooser(intent, getString(R.string.send_email)))
    }

    private fun openLink (url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun isFirstStart() : Boolean {
        val preferences = getSharedPreferences("preferences", MODE_PRIVATE)!!
        return if(preferences.getBoolean("isFirstStart", true)){
            val prefs : SharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE)!!
            val editor : SharedPreferences.Editor = prefs.edit()
            editor.putBoolean("isFirstStart", false)
            editor.commit()
            true
        } else false
    }

    private fun checkBatteryOptimization(){
        val intent = Intent()
        val powerManager : PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if(!powerManager.isIgnoringBatteryOptimizations(packageName)){
            Snackbar.make(binding.appBarLayout, R.string.batt_optimization, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getColor(R.color.text))
                .setAction(R.string.batt_disable){
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }
                .show()
        }
    }

    private fun demoMode(demoMode: Boolean) {
        val preferences : SharedPreferences = getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)!!
        val editor : SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("demoMode", demoMode)
        editor.commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}