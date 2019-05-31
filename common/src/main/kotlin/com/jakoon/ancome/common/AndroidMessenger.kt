package com.jakoon.ancome.common

import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.util.Log
import kotlinx.coroutines.launch


class AndroidMessenger(var messenger: Messenger? = null, val incomingHandler: IncomingHandler = IncomingHandler()) :
    AncomeMessenger() {

    init {
        scope.launch {
            for (receiveMessage in incomingHandler.receiveMessages()) {
                // TODO bit smelly - fix up
                if (messenger == null) {
                    messenger = incomingHandler.replyToMessenger
                }
                Log.i("AndroidMessenger", "$where received message")
                receiveChannel.send(receiveMessage)
            }
        }

    }

    override fun sendMessage(message: String) {
        val msg = Message.obtain(null, MessageKeys.MESSAGE_TYPE_DATA)
        val bundle = Bundle()
        bundle.putString(MessageKeys.MESSAGE_DATA_KEY, message)
        msg.data = bundle
        msg.replyTo = Messenger(incomingHandler)

        Log.i("AndroidMessenger", "$where : Sending to other end")
        messenger?.send(msg)
    }
}