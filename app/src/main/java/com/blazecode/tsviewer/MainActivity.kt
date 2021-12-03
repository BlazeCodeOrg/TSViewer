package com.blazecode.tsviewer

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.blazecode.tsviewer.databinding.ActivityMainBinding
import com.blazecode.tsviewer.enums.ConnectionStatus
import androidx.core.widget.addTextChangedListener
import com.blazecode.tsviewer.databinding.ContentMainBinding
import com.blazecode.tsviewer.util.ConnectionManager
import com.github.theholywaffle.teamspeak3.api.wrapper.Client


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contentBinding: ContentMainBinding

    private var IP_ADRESS : String = ""
    private var USERNAME : String = ""
    private var PASSWORD : String = ""

    private var clientList = mutableListOf<Client>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        contentBinding = binding.content
        setContentView(binding.root)


        contentBinding.inputEditTextIp.addTextChangedListener(){
            IP_ADRESS = it.toString()
        }

        contentBinding.inputEditTextUsername.addTextChangedListener(){
            USERNAME = it.toString()
        }

        contentBinding.inputEditTextPassword.addTextChangedListener(){
            PASSWORD = it.toString()
        }

        contentBinding.buttonLogIn.setOnClickListener {
            getClients()
        }
    }

    private fun getClients(){
        if(isAllInfoProvided()) {
            val connectionManager = ConnectionManager
            clientList = connectionManager.getClients(IP_ADRESS, USERNAME, PASSWORD, "TSViewer", true, false)

            Toast.makeText(this, "got ${clientList.size} clients", Toast.LENGTH_SHORT).show()
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
        contentBinding.inputEditTextIp.setText(IP_ADRESS)
        contentBinding.inputEditTextUsername.setText(USERNAME)
        contentBinding.inputEditTextPassword.setText(PASSWORD)
    }

    fun isAllInfoProvided() : Boolean {
        if (contentBinding.inputEditTextIp.text.isNullOrEmpty() || contentBinding.inputEditTextUsername.text.isNullOrEmpty() || contentBinding.inputEditTextPassword.text.isNullOrEmpty()){
            if(contentBinding.inputEditTextIp.text.isNullOrEmpty()) contentBinding.inputEditTextIp.error = getString(R.string.mustBeProvided)
            if(contentBinding.inputEditTextUsername.text.isNullOrEmpty()) contentBinding.inputEditTextUsername.error = getString(R.string.mustBeProvided)
            if(contentBinding.inputEditTextPassword.text.isNullOrEmpty()) contentBinding.inputEditTextPassword.error = getString(R.string.mustBeProvided)

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