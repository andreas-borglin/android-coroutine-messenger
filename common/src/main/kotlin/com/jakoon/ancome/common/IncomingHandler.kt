package com.jakoon.ancome.common

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch


class IncomingHandler : Handler(Looper.getMainLooper()) {

    val channel = Channel<String>()
    private val scope = CoroutineScope(Dispatchers.IO)
    var replyToMessenger: Messenger? = null

    override fun handleMessage(msg: Message) {
        Log.i("IncomingHandler", "New message")
        val type = msg.what
        val data = msg.data
        replyToMessenger = msg.replyTo
        when (type) {
            MessageKeys.MESSAGE_TYPE_DATA -> {
                val message = data.getString(MessageKeys.MESSAGE_DATA_KEY)
                scope.launch { channel.send(message) }
            }
            else -> Log.i("IncomingHandler", "Unknown msg type: $type")
        }
    }

    fun receiveMessages(): ReceiveChannel<String> = channel
}