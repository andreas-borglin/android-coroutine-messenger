package com.jakoon.ancome.server

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Messenger
import com.jakoon.ancome.common.AncomeMessenger
import com.jakoon.ancome.common.AndroidMessenger
import com.jakoon.ancome.common.IncomingHandler


abstract class AncomeService : Service() {

    private lateinit var messenger: Messenger

    override fun onBind(intent: Intent): IBinder {
        val incomingHandler = IncomingHandler()
        messenger = Messenger(incomingHandler)
        val messageProxy = AndroidMessenger(incomingHandler = incomingHandler)
        messageProxy.where = "Server"

        Handler().postDelayed({ onNewClient(messageProxy) }, 100)

        return messenger.binder
    }

    abstract fun onNewClient(messenger: AncomeMessenger)

}