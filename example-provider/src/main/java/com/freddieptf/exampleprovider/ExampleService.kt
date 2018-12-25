package com.freddieptf.exampleprovider

import android.app.IntentService
import android.content.Intent
import android.os.*
import android.util.Log
import com.freddieptf.malry.api.MessageCodes

/**
 * Created by freddieptf on 11/24/18.
 */

class ExampleService : IntentService("ExampleService") {

    private val TAG = javaClass.simpleName

    private val messenger = Messenger(MyHandle())
    private val clients = ArrayList<Messenger>()

    private inner class MyHandle : Handler() {
        override fun handleMessage(message: Message) {
            Log.d(TAG, "message ${message.what}")
            when (message.what) {
                MessageCodes.PING -> {
                    val client = message.replyTo
                    if (!clients.contains(client)) clients += client
                    try {
                        message.what = MessageCodes.PONG
                        client.send(message)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
                else -> {
                    throw NotImplementedError("cannot process that message code..what even is that?")
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return messenger.binder
    }

    override fun onHandleIntent(intent: Intent) {

    }

}
