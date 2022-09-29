package com.dlwngud.socket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dlwngud.socket.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessaging
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException


private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 소켓 통신을 위한 Socket 객체 생성
    lateinit var mSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // .on = 메시지를 받았을 때 동작 수행.
        // .emit = 메시지를 전송하는 동작 수행.
        try {
            mSocket = IO.socket("http://192.168.0.5:9999")
            mSocket.connect()
            Log.d("Connected", "OK")
        } catch (e: URISyntaxException) {
            Log.d("ERR", e.toString())
        }
        // Socket.EVENT_CONNECT라는 메세지를 받을 경우 onConnect를 수행
        // Socket.EVNET_CONNECT는 서버와 클라이언트와 연결되었을 때, 서버로부터 자동적으로 전송되는 메세지
        mSocket.on(Socket.EVENT_CONNECT, onConnect)

        binding.btn.setOnClickListener {
            val data = "drive"
            mSocket.emit("message", data)
        }

        // parking이라는 메세지가 들어올때
        mSocket.on("parking", onMessage)

    }

    val onConnect = Emitter.Listener {
        mSocket.emit("emitReceive", "OK")
    }

    private val onMessage = Emitter.Listener { args ->
        val data = args[0].toString()
        Log.d("parking", data)

        runOnUiThread {
            binding.tv.text = data
            Toast.makeText(this@MainActivity,data,Toast.LENGTH_SHORT).show()
        }
    }
}