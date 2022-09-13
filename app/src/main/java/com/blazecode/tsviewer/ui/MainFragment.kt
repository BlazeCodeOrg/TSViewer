/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.*
import com.blazecode.tsviewer.R
import com.blazecode.tsviewer.databinding.MainFragmentAdvancedLayoutBinding
import com.blazecode.tsviewer.databinding.MainFragmentBinding
import com.blazecode.tsviewer.databinding.MainFragmentScheduleLayoutBinding
import com.blazecode.tsviewer.util.ClientsWorker
import com.blazecode.tsviewer.util.ErrorHandler
import com.google.common.util.concurrent.ListenableFuture
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var advancedLayoutBinding: MainFragmentAdvancedLayoutBinding
    private lateinit var scheduleLayoutBinding: MainFragmentScheduleLayoutBinding

    private val errorHandler = context?.let { ErrorHandler(it) }
    private lateinit var workManager: WorkManager

    private val TAG = "scheduleClients"

    private var IP_ADRESS : String = ""
    private var USERNAME : String = ""
    private var PASSWORD : String = ""
    private var NICKNAME : String = "TSViewer"
    private var INCLUDE_QUERY_CLIENTS : Boolean = false
    private var SCHEDULE_TIME : Float = 15f
    private var RUN_ONLY_WIFI : Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        advancedLayoutBinding = MainFragmentAdvancedLayoutBinding.bind(_binding!!.root)
        scheduleLayoutBinding = MainFragmentScheduleLayoutBinding.bind(_binding!!.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workManager = context?.let { WorkManager.getInstance(it) }!!

        binding.inputEditTextIp.addTextChangedListener(){
            IP_ADRESS = it.toString()
            savePreferences()
        }

        binding.inputEditTextUsername.addTextChangedListener(){
            USERNAME = it.toString()
            savePreferences()
        }

        binding.inputEditTextPassword.addTextChangedListener(){
            PASSWORD = it.toString()
            savePreferences()
        }

        binding.buttonAdvanced.setOnClickListener {
            binding.advancedLayout.isVisible = !binding.advancedLayout.isVisible
            if (binding.buttonAdvanced.text == getString(R.string.more)) binding.buttonAdvanced.text = getString(R.string.less)
            else binding.buttonAdvanced.text = getString(R.string.more)
        }

        advancedLayoutBinding.inputEditTextNickname.addTextChangedListener(){
            NICKNAME = it.toString()
            savePreferences()
        }

        advancedLayoutBinding.switchIncludeQueryClients.setOnCheckedChangeListener { compoundButton, isChecked ->
            INCLUDE_QUERY_CLIENTS = isChecked
            savePreferences()
        }

        advancedLayoutBinding.buttonInfoIncludeQueryClients.setOnClickListener {
            showTooltip(it, getString(R.string.tooltip_include_query_clients))
        }

        advancedLayoutBinding.switchOnlyWiFi.setOnCheckedChangeListener { compoundButton, isChecked ->
            RUN_ONLY_WIFI = isChecked
            savePreferences()
        }

        scheduleLayoutBinding.timeSlider.setLabelFormatter { "${scheduleLayoutBinding.timeSlider.value.toString().split(".")[0]} min" }
        scheduleLayoutBinding.timeSlider.addOnChangeListener { slider, value, fromUser ->
            SCHEDULE_TIME = value
            scheduleLayoutBinding.textViewScheduleTime.text = "${scheduleLayoutBinding.timeSlider.value.toString().split(".")[0]} min"
            savePreferences()
        }

        binding.buttonLogIn.setOnClickListener {
            if(isAllInfoProvided()){
                val clientWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<ClientsWorker>()
                    .build()
                workManager.enqueue(clientWorkRequest)
            }
        }

        scheduleLayoutBinding.buttonStartSchedule.setOnClickListener {
            val clientWorkRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<ClientsWorker>(
                SCHEDULE_TIME.toLong(),                                                                                 //GIVE NEW WORK TIME
                TimeUnit.MINUTES,
                1, TimeUnit.MINUTES)                                                                      //FLEX TIME INTERVAL
                .build()

            if(isAllInfoProvided() && !isWorkScheduled(TAG)){
                val oneTimeclientWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<ClientsWorker>().build()          //RUN ONE TIME
                workManager.enqueue(oneTimeclientWorkRequest)
                workManager.enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.REPLACE, clientWorkRequest)     //SCHEDULE THE NEXT RUNS
                if(isWorkScheduled(TAG)){
                    scheduleLayoutBinding.buttonStartSchedule.text = getString(R.string.stop_schedule)
                } else {
                    errorHandler!!.reportError("Error starting Worker")
                }
            } else if (isWorkScheduled(TAG)) {
                workManager.cancelUniqueWork(TAG)
                if(!isWorkScheduled(TAG)){
                    scheduleLayoutBinding.buttonStartSchedule.text = getString(R.string.start_schedule)
                } else {
                    errorHandler!!.reportError("Error cancelling Worker")
                }
            }
        }
    }

    private fun isWorkScheduled(TAG: String) : Boolean {
        val instance = WorkManager.getInstance(requireContext())
        val statuses: ListenableFuture<List<WorkInfo>> = instance.getWorkInfosForUniqueWork(TAG)
        return try {
            var running = false
            val workInfoList: List<WorkInfo> = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        savePreferences()
    }

    override fun onResume() {
        super.onResume()
        loadPreferences()
    }

    private fun showTooltip(anchorView: View, text: String){

        val tooltip = Tooltip.Builder(anchorView.context)
            .anchor(anchorView, 0, 0, false)
            .text(text)
            .styleId(R.style.tsTooltipStyle)
            .maxWidth(resources.displayMetrics.widthPixels / 4 * 3)
            .arrow(true)
            .closePolicy(ClosePolicy.TOUCH_ANYWHERE_CONSUME)
            .overlay(true)
            .create()

        tooltip.show(anchorView, Tooltip.Gravity.CENTER, true)
    }

    fun savePreferences(){
        val preferences : SharedPreferences = context?.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)!!
        val editor : SharedPreferences.Editor = preferences.edit()
        //IP, USERNAME AND PASS ARE ENCRYPTED
        editor.putString("nick", NICKNAME)
        editor.putBoolean("includeQuery", INCLUDE_QUERY_CLIENTS)
        editor.putFloat("scheduleTime", SCHEDULE_TIME)
        editor.putBoolean("run_only_wifi", RUN_ONLY_WIFI)
        editor.commit()

        saveEncryptedPreferences()
    }

    private fun saveEncryptedPreferences(){

        //SAVE DATA
        val encryptedSharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
            this.requireContext(),
            "encrypted_preferences",
            getMasterKey(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        with (encryptedSharedPreferences.edit()) {
            this.putString("ip", IP_ADRESS)
            this.putString("user", USERNAME)
            this.putString("pass", PASSWORD)
            apply()
        }
    }

    fun loadPreferences(){
        val preferences = context?.getSharedPreferences("preferences", AppCompatActivity.MODE_PRIVATE)!!
        NICKNAME = preferences.getString("nick", getString(R.string.app_name)).toString()
        INCLUDE_QUERY_CLIENTS = preferences.getBoolean("includeQuery", false)
        SCHEDULE_TIME = preferences.getFloat("scheduleTime", 15f)
        RUN_ONLY_WIFI = preferences.getBoolean("run_only_wifi", true)

        loadEncryptedPreferences()
        loadViews()
    }

    private fun loadEncryptedPreferences(){
        val encryptedSharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
            this.requireContext(),
            "encrypted_preferences",
            getMasterKey(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

        IP_ADRESS = encryptedSharedPreferences.getString("ip", "").toString()
        USERNAME = encryptedSharedPreferences.getString("user", "").toString()
        PASSWORD = encryptedSharedPreferences.getString("pass", "").toString()
    }

    private fun getMasterKey() : MasterKey {
        //MAKE AN ENCRYPTION KEY
        val spec = KeyGenParameterSpec.Builder("_androidx_security_master_key_",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        return MasterKey.Builder(this.requireContext())
            .setKeyGenParameterSpec(spec)
            .build()
    }

    private fun loadViews(){
        binding.inputEditTextIp.setText(IP_ADRESS)
        binding.inputEditTextUsername.setText(USERNAME)
        binding.inputEditTextPassword.setText(PASSWORD)
        advancedLayoutBinding.inputEditTextNickname.setText(NICKNAME)
        if(isWorkScheduled(TAG)) scheduleLayoutBinding.buttonStartSchedule.text = getString(R.string.stop_schedule)
        if(!isWorkScheduled(TAG)) scheduleLayoutBinding.buttonStartSchedule.text = getString(R.string.start_schedule)
        advancedLayoutBinding.switchIncludeQueryClients.isChecked = INCLUDE_QUERY_CLIENTS
        advancedLayoutBinding.switchOnlyWiFi.isChecked = RUN_ONLY_WIFI
        scheduleLayoutBinding.timeSlider.value = SCHEDULE_TIME
        scheduleLayoutBinding.textViewScheduleTime.text = "${SCHEDULE_TIME.toString().split(".")[0]} min"
    }

    private fun isAllInfoProvided() : Boolean {
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