/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.scrapguidev2.util

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import com.blazecode.tsviewer.BuildConfig
import com.blazecode.tsviewer.R
import java.util.*

// TUTORIAL
// https://www.baeldung.com/kotlin/builder-pattern

class MailUtil private constructor(
    val context: Context,
    val overrideRecipient: String,
    val subject: String,
    val includeDeviceInfo: Boolean?
)   {

    data class Builder(
        var context: Context,
        var overrideRecipient: String = "null",
        var subject: String = "null",
        var includeDeviceInfo: Boolean? = false
    ) {

        fun context(context: Context) = apply { this.context = context }
        fun overrideRecipient(overrideRecipient: String) = apply { this.overrideRecipient = overrideRecipient }
        fun subject(subject: String) = apply { this.subject = subject }
        fun includeDeviceInfo(includeDeviceInfo: Boolean) = apply { this.includeDeviceInfo = includeDeviceInfo }
        fun send() = MailUtil(context, overrideRecipient, subject, includeDeviceInfo).sendMail(this)
    }

    fun sendMail(builder: Builder) {
        val MAIL_ADDRESS =
            if (builder.overrideRecipient == "null") context.resources.getString(R.string.email_address)
            else builder.overrideRecipient

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(MAIL_ADDRESS))
        intent.putExtra(Intent.EXTRA_SUBJECT, builder.subject + " - " + context?.getString(R.string.app_name))

        if (builder.includeDeviceInfo == true) {
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "App Version: ${BuildConfig.VERSION_NAME}" +
                        "\nAndroid Version: ${Build.VERSION.SDK_INT}" +
                        "\nDeviceInfo: ${Build.MANUFACTURER} ${Build.MODEL}" +
                        "\nDeviceLanguage: ${Resources.getSystem().configuration.locale.language}" +
                        "\nSetLanguage: ${Locale.getDefault()}" +
                        "\n\nPlease describe your issue below this line.\n\n"
            )
        }
        context?.startActivity(Intent.createChooser(intent, context?.getString(R.string.send_email)))
    }

}