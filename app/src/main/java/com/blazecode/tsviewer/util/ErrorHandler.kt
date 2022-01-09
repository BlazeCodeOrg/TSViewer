package com.blazecode.tsviewer.util

import android.content.Context
import android.os.Looper
import android.widget.Toast

class ErrorHandler(val context: Context) {

    private var cutException: String = ""

    fun reportError(exception: String) {

        //USED FOR DISPLAYING THE TOAST WITHOUT A VIEW
        //CANNOT CREATE MULTIPLE LOOPERS
        if(Looper.myLooper() == null){
            Looper.prepare()
        }

        //DISPLAY EXCEPTION
        cutException = when {
            exception.contains(">>") -> exception.split(">>")[1].trim()
            exception.contains("Exception:") -> exception.split("Exception:")[1].trim()
            else -> exception
        }
        Toast.makeText(context, cutException, Toast.LENGTH_LONG).show()
        println(exception)
    }
}