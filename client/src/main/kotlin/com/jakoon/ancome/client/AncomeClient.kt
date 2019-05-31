package com.jakoon.ancome.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Messenger
import com.jakoon.ancome.common.AncomeMessenger
import com.jakoon.ancome.common.AndroidMessenger
import kotlin.coroutines.suspendCoroutine


class AncomeClient(val context: Context) {

    private var messageProxy: AncomeMessenger? = null
    private var connection: ServiceConnection? = null

    suspend fun connect(componentName: ComponentName): AncomeMessenger = suspendCoroutine {
        val intent = Intent().also { it.component = componentName }
        connection = object : ServiceConnection {

            override fun onServiceDisconnected(className: ComponentName) {
                disconnect()
            }

            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val messenger = Messenger(service)
                messageProxy = AndroidMessenger(messenger)
                it.resumeWith(Result.success(messageProxy!!)) // TODO ugs again
            }
        }
        try {
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        } catch (e: Throwable) {
            it.resumeWith(Result.failure(e))
        }
    }

    fun disconnect() {
        connection?.let {
            context.unbindService(it)
            messageProxy?.close()
            connection = null
            messageProxy = null
        }
    }

}