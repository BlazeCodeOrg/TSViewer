package com.blazecode.tsviewer

import android.content.SharedPreferences
import android.os.Bundle
import android.os.SharedMemory
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.blazecode.tsviewer.databinding.ActivityMainBinding
import com.blazecode.tsviewer.enums.ConnectionStatus
import com.github.theholywaffle.teamspeak3.TS3Api
import com.github.theholywaffle.teamspeak3.TS3Config
import com.github.theholywaffle.teamspeak3.TS3Query
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.core.widget.addTextChangedListener


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var IP_ADRESS : String = ""
    private var USERNAME : String = ""
    private var PASSWORD : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.inputEditTextIp.addTextChangedListener(){
            IP_ADRESS = it.toString()
        }

        binding.inputEditTextUsername.addTextChangedListener(){
            USERNAME = it.toString()
        }

        binding.inputEditTextPassword.addTextChangedListener(){
            PASSWORD = it.toString()
        }

        binding.buttonLogIn.setOnClickListener {

            if(isAllInfoProvided()) {
                val connectionManager = ConnectionManager
                var connectionResult = ConnectionStatus.CONNECTING
                connectionResult =
                    connectionManager.connect(IP_ADRESS, USERNAME, PASSWORD, "TSViewer")
                while (connectionResult == ConnectionStatus.CONNECTING) {

                }
                if (connectionResult == ConnectionStatus.CONNECTED) {
                    Toast.makeText(this, "Connection Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        savePreferences()
    }

    override fun onResume() {
        super.onResume()
        loadPreferences()
    }

    fun savePreferences(){
        val preferences : SharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE)
        val editor : SharedPreferences.Editor = preferences.edit()
        editor.putString("ip", IP_ADRESS)
        editor.putString("user", USERNAME)
        editor.putString("pass", PASSWORD)
        editor.commit()
     }

    fun loadPreferences(){
        val preferences = getSharedPreferences("preferences", MODE_PRIVATE)
        IP_ADRESS = preferences.getString("ip", "").toString()
        USERNAME = preferences.getString("user", "").toString()
        PASSWORD = preferences.getString("pass", "").toString()

        loadViews()
    }

    fun loadViews(){
        binding.inputEditTextIp.setText(IP_ADRESS)
        binding.inputEditTextUsername.setText(USERNAME)
        binding.inputEditTextPassword.setText(PASSWORD)
    }

    fun isAllInfoProvided() : Boolean {
        if (binding.inputEditTextIp.text.isNullOrEmpty() || binding.inputEditTextUsername.text.isNullOrEmpty() || binding.inputEditTextPassword.text.isNullOrEmpty()){
            if(binding.inputEditTextIp.text.isNullOrEmpty()) binding.inputEditTextIp.error = getString(R.string.mustBeProvided)
            if(binding.inputEditTextUsername.text.isNullOrEmpty()) binding.inputEditTextUsername.error = getString(R.string.mustBeProvided)
            if(binding.inputEditTextPassword.text.isNullOrEmpty()) binding.inputEditTextPassword.error = getString(R.string.mustBeProvided)

            return false
        } else {
            return true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}