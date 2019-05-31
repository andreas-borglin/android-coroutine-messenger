package com.jakoon.ancome.common

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch


abstract class AncomeMessenger {

    internal val sendChannel = Channel<String>()
    internal val receiveChannel = Channel<String>()
    internal val scope = CoroutineScope(Dispatchers.IO)

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

    internal abstract fun sendMessage(message: String)

}