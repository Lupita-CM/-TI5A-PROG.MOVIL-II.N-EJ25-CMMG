package com.example.broadcastreceiver_y_telefonia.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Verificamos si la acción es un cambio en el estado del teléfono
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            // Si el estado es "RINGING" (llamada entrante)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                // Verificamos si el número entrante es el número especificado por el usuario
                if (incomingNumber == getSavedNumber(context)) {
                    val message = getSavedMessage(context)
                    sendSms(incomingNumber, message)
                    Log.d("CallReceiver", "SMS enviado a $incomingNumber: $message")
                }
            }
        }
    }

    // Función para obtener el número guardado por el usuario
    private fun getSavedNumber(context: Context): String? {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("saved_number", null)
    }

    // Función para obtener el mensaje guardado por el usuario
    private fun getSavedMessage(context: Context): String {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("saved_message", "Estoy ocupado, te llamo luego.") ?: "Estoy ocupado, te llamo luego."
    }

    // Función para enviar un SMS
    private fun sendSms(phoneNumber: String?, message: String) {
        if (phoneNumber != null) {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        }
    }
}