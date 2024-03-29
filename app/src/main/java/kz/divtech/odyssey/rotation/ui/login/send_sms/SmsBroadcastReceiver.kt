package kz.divtech.odyssey.rotation.ui.login.send_sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern


class SmsBroadcastReceiver() : BroadcastReceiver() {

    private var otpListener: OTPReceiveListener? = null

    fun setListener(otpListener: OTPReceiveListener){
        this.otpListener = otpListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION){
            val extras = intent.extras
            val status = extras?.let {
                it[SmsRetriever.EXTRA_STATUS] as Status?
            }
            when (status!!.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val sms = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    sms?.let {
                        val p = Pattern.compile("\\d+")
                        val m = p.matcher(it)
                        if (m.find()) {
                            val otp = m.group()
                            otpListener?.onOTPReceived(otp)
                        }
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    otpListener?.onTimeout()
                }
            }

        }
    }

    interface OTPReceiveListener {
        fun onOTPReceived(code: String?)
        fun onTimeout()
    }
}