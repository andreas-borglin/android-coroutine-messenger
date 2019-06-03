package com.jakoon.ancome.common

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


abstract class AncomeMessenger {

    protected val sendChannel = Channel<String>()
    protected val receiveChannel = Channel<String>()
    protected val scope = CoroutineScope(Dispatchers.IO)

    // TODO temp variable
    var where = "Client"

    init {
        scope.launch {
            for (message in sendChannel) {
                Log.i("AncomeMessenger", "$where - Outgoing message")
                sendMessage(message)
            }
        }
    }

    fun receiveChannel(): ReceiveChannel<String> = receiveChannel

    fun sendChannel(): SendChannel<String> = sendChannel

    fun close() {
        sendChannel.close()
        receiveChannel.close()
    }

    protected abstract fun sendMessage(message: String)

}