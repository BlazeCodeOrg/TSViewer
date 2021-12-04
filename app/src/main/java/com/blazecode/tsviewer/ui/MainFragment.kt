package com.blazecode.tsviewer.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.databinding.MainFragmentAdvancedLayoutBinding
import com.blazecode.tsviewer.databinding.MainFragmentBinding
import com.blazecode.tsviewer.util.ConnectionManager
import com.blazecode.tsviewer.util.ErrorHandler
import com.github.theholywaffle.teamspeak3.api.wrapper.Client

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var advancedLayoutBinding: MainFragmentAdvancedLayoutBinding

    private val errorHandler = ErrorHandler()

    private var IP_ADRESS : String = ""
    private var USERNAME : String = ""
    private var PASSWORD : String = ""
    private var NICKNAME : String = "TSViewer"
    private var RANDOMIZE_NICKNAME : Boolean = true
    private var INCLUDE_QUERY_CLIENTS : Boolean = false

    private var clientList = mutableListOf<Client>()
    private var clientListNames = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        advancedLayoutBinding = MainFragmentAdvancedLayoutBinding.bind(_binding!!.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputEditTextIp.addTextChangedListener(){
            IP_ADRESS = it.toString()
        }

        binding.inputEditTextUsername.addTextChangedListener(){
            USERNAME = it.toString()
        }

        binding.inputEditTextPassword.addTextChangedListener(){
            PASSWORD = it.toString()
        }

        binding.buttonAdvanced.setOnClickListener {
            binding.advancedLayout.isVisible = !binding.advancedLayout.isVisible
            if (binding.buttonAdvanced.text == getString(R.string.more)) binding.buttonAdvanced.text = getString(R.string.less)
            else binding.buttonAdvanced.text = getString(R.string.more)
        }

        advancedLayoutBinding.inputEditTextNickname.addTextChangedListener(){
            NICKNAME = it.toString()
        }

        advancedLayoutBinding.switchNicknameRandomize.setOnCheckedChangeListener { compoundButton, isChecked ->
            RANDOMIZE_NICKNAME = isChecked
        }

        advancedLayoutBinding.buttonInfoRandomize.setOnClickListener {
            TODO("Randomize Info Button")
        }

        advancedLayoutBinding.switchIncludeQueryClients.setOnCheckedChangeListener { compoundButton, isChecked ->
            INCLUDE_QUERY_CLIENTS = isChecked
        }

        advancedLayoutBinding.buttonInfoIncludeQueryClients.setOnClickListener {
            TODO("Include Query Clients Info Button")
        }

        binding.buttonLogIn.setOnClickListener {
            try {
                getClients()
            } catch (exeption: Exception){
                errorHandler.reportError(exeption, "Connection Failed: Wrong IP, Username or Password")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getClients(){
        if(isAllInfoProvided()) {
            val connectionManager = ConnectionManager
            clientList = connectionManager.getClients(IP_ADRESS, USERNAME, PASSWORD, NICKNAME, RANDOMIZE_NICKNAME, INCLUDE_QUERY_CLIENTS)
            extractNames()

            Toast.makeText(context, "got ${clientList.size} client(s): ${clientListNames}.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractNames(){
        clientListNames.clear()
        for(client in clientList){
            clientListNames.add(client.nickname)
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
        val preferences : SharedPreferences = context?.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)!!
        val editor : SharedPreferences.Editor = preferences.edit()
        editor.putString("ip", IP_ADRESS)
        editor.putString("user", USERNAME)
        editor.putString("pass", PASSWORD)
        editor.putString("nick", NICKNAME)
        editor.putBoolean("randNick", RANDOMIZE_NICKNAME)
        editor.putBoolean("includeQuery", INCLUDE_QUERY_CLIENTS)
        editor.commit()
    }

    fun loadPreferences(){
        val preferences = context?.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)!!
        IP_ADRESS = preferences.getString("ip", "").toString()
        USERNAME = preferences.getString("user", "").toString()
        PASSWORD = preferences.getString("pass", "").toString()
        NICKNAME = preferences.getString("nick", getString(R.string.app_name)).toString()
        RANDOMIZE_NICKNAME = preferences.getBoolean("randNick", true)
        INCLUDE_QUERY_CLIENTS = preferences.getBoolean("includeQuery", false)

        loadViews()
    }

    fun loadViews(){
        binding.inputEditTextIp.setText(IP_ADRESS)
        binding.inputEditTextUsername.setText(USERNAME)
        binding.inputEditTextPassword.setText(PASSWORD)
        advancedLayoutBinding.inputEditTextNickname.setText(NICKNAME)
        advancedLayoutBinding.switchNicknameRandomize.isChecked = RANDOMIZE_NICKNAME
        advancedLayoutBinding.switchIncludeQueryClients.isChecked = INCLUDE_QUERY_CLIENTS
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


}