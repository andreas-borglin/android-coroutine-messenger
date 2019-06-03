package com.jakoon.ancome

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.jakoon.ancome.client.AncomeClient
import com.jakoon.ancome.common.AncomeMessenger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch


class ClientService : Service() {

    private val scope = CoroutineScope(Dispatchers.Main)
    private val binder = LocalBinder()
    private lateinit var ancomeClient: AncomeClient
    private lateinit var ancomeMessenger: AncomeMessenger

    inner class LocalBinder : Binder() {
        fun getService(): ClientService = this@ClientService
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("ClientService", "Client connected")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        scope.cancel()
        ancomeClient.disconnect()

        return super.onUnbind(intent)
    }

    suspend fun connect() {
        ancomeClient = AncomeClient(this@ClientService)
        ancomeMessenger = ancomeClient.connect(ComponentName(packageName, ServerService::class.java.name))
    }

    fun receiveMessages(): ReceiveChannel<String> = ancomeMessenger.receiveChannel()

    fun sendMessage(message: String) {
        scope.launch {
            ancomeMessenger.sendChannel().send(message)
        }
    }
}