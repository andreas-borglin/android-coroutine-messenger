package com.jakoon.ancome

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.selects.select
import kotlin.random.Random

fun Button.listen(): ReceiveChannel<Boolean> {
    val channel = Channel<Boolean>()
    setOnClickListener {
        channel.offer(true)
    }
    return channel
}

class ClientActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var service: ClientService
    private val messagesBuilder = StringBuilder()

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(component: ComponentName, binder: IBinder) {
            service = (binder as ClientService.LocalBinder).getService()
            startTheFun()
        }

        override fun onServiceDisconnected(component: ComponentName) {
            // TODO
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        val intent = Intent(this, ClientService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun startTheFun() {
        scope.launch {
            service.connect()
            while (isActive) {
                Log.i("ClientActivity", "In loop")
                select<Unit> {
                    service.receiveMessages().onReceive {
                        Log.i("ClientActivity", "Received incoming message")
                        messagesBuilder.append(it).append('\n')
                        messages.text = messagesBuilder.toString()
                    }

                    send.listen().onReceive {
                        Log.i("ClientActivity", "Click received")
                        service.sendMessage("Message #${Random.nextInt()}")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        unbindService(connection)
    }
}