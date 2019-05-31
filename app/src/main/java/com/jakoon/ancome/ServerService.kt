package com.jakoon.ancome

import android.util.Log
import com.jakoon.ancome.common.AncomeMessenger
import com.jakoon.ancome.server.AncomeService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ServerService : AncomeService() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onNewClient(messenger: AncomeMessenger) {
        scope.launch {
            for (message in messenger.receiveChannel()) {
                Log.i("ServerService", "server received message")
                delay(1000)
                messenger.sendChannel().send("Hello I received: $message")
                Log.i("ServerService", "Sent response")
            }
        }
    }
}