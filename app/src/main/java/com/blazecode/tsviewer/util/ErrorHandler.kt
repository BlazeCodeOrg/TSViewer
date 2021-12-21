package com.blazecode.tsviewer.util

import android.content.Context
import android.os.Looper
import android.widget.Toast

class ErrorHandler(val context: Context) {

    fun reportError(exception: String) {

        //USED FOR DISPLAYING THE TOAST WITHOUT A VIEW
        //CANNOT CREATE MULTIPLE LOOPERS
        if(Looper.myLooper() == null){
            Looper.prepare()
        }

        //DISPLAY EXCEPTION
        val cutException = exception.split(">>")[1]
        Toast.makeText(context, cutException, Toast.LENGTH_LONG).show()
    }
}