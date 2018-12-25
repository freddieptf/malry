package com.freddieptf.malry.providers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.collection.SimpleArrayMap
import androidx.fragment.app.Fragment
import com.freddieptf.malry.api.MessageCodes
import com.freddieptf.mangatest.R

/**
 * Created by freddieptf on 12/25/18.
 */
class ProviderFrag : Fragment(), ServiceConnection {

    companion object {
        private val TAG = ProviderFrag.javaClass.simpleName
        private val COMPONENT_EXTRA = "component_extra"
    }

    private val resultHandler = ResultHandler()
    private val client = Messenger(resultHandler)
    private val servers = SimpleArrayMap<ComponentName, Messenger>()

    private lateinit var tvConnected: TextView

    private inner class ResultHandler : Handler() {
        override fun handleMessage(message: Message) {
            Log.d(TAG, "message ${message.what}")
            when (message.what) {
                MessageCodes.PONG -> {
                    val componentName: ComponentName = message.data.getParcelable(COMPONENT_EXTRA)
                    tvConnected.text = tvConnected.text.toString() + "\n${componentName.className} pong"
                    val server = servers[componentName]
                    if (server != null) {
                        postDelayed(
                                {
                                    message.what = MessageCodes.PING
                                    message.data.putParcelable(COMPONENT_EXTRA, componentName)
                                    message.replyTo = client
                                    try {
                                        server.send(message)
                                        tvConnected.text = "${tvConnected.text.toString()}\nping ${componentName.className}"
                                    } catch (e: RemoteException) {
                                        e.printStackTrace()
                                    }
                                }, 1000
                        )
                    }
                }
                else -> {
                    throw NotImplementedError("can't handle this kind of message")
                }
            }
            super.handleMessage(message)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.external_providers_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvConnected = view.findViewById(R.id.ping_txt)

        val pm = activity!!.packageManager
        val intent = Intent()
        intent.action = "com.freddieptf.malry.source"
        val resolveInfos = pm.queryIntentServices(intent, 0)
        for (info in resolveInfos) {
            val componentName = ComponentName(info.serviceInfo.packageName, info.serviceInfo.name)
            val intent1 = Intent()
            intent1.component = componentName
            context!!.bindService(intent1, this, Context.BIND_AUTO_CREATE)
        }

    }

    override fun onServiceDisconnected(p0: ComponentName) {

    }

    override fun onServiceConnected(p0: ComponentName, iBinder: IBinder) {
        val messenger = Messenger(iBinder)
        servers.put(p0, messenger)
        val message = Message()
        message.what = MessageCodes.PING
        message.data.putParcelable(COMPONENT_EXTRA, p0)
        message.replyTo = client
        try {
            messenger.send(message)
            tvConnected.text = "ping ${p0.className}"
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }
}