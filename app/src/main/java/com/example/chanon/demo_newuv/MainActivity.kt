package com.example.chanon.demo_newuv

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import io.netpie.microgear.Microgear
import io.netpie.microgear.MicrogearEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val microgear = Microgear(this)
    private val appid: String = "uvmaps" //val is const var is normal
    private val key: String = "U5R4jJnZAl7ZU0J"
    private val secret: String = "IQcWCYWf3M7IHDuebPut6yrK1"
    private val alias: String = "android"
    var netpiestr: String = ""

    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val bundle = msg.data //get data and put msg to textview via function in class microgearcallback
            val string = bundle.getString("myKey") //recieve from onMessage
//            val prostr = bundle.getString("progress")
//            val parts = string.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//            val p1 = parts[0] //UV
//            val p2 = parts[1] //temp
//            val p3 = parts[2] //humid
            //val myTextView = findViewById<TextView>(R.id.DataTextView)
            //myTextView.append(string+"\n"); //สำหรับการเว้นบรรทัดไปเรื่อยๆ
            //DataTextView.text = string
            netpiestr = string
            DataTextView.text = netpiestr
//            UVprogressBar.setProgress(2,true)
//            TempprogressBar.setProgress(31,true)
//            HumidprogressBar.setProgress(67,true)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val callback = MicrogearCallBack()
        microgear.connect(appid, key, secret, alias)
        microgear.setCallback(callback)
        microgear.subscribe("uvmaps_m1") //get data from sensor
//        DataTextView.text = "dsaf;kjf;lsdahfk"
        textView5.text = "position"
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
    override fun onDestroy() {
        super.onDestroy()
        microgear.disconnect()
    }
    override fun onResume() {
        super.onResume()
        microgear.bindServiceResume()
    }
    internal inner class MicrogearCallBack : MicrogearEventListener {
        override fun onConnect() {
            val msg = handler.obtainMessage()
            val bundle = Bundle()
            bundle.putString("myKey", "Now I'm connected with netpie\n")
            msg.data = bundle
            handler.sendMessage(msg)
            Log.i("Connected", "Now I'm connected with netpie")
        }

        @SuppressLint("NewApi")
        override fun onMessage(topic: String, message: String) {
            val msg = handler.obtainMessage()
            val bundle = Bundle()

//            val msg2 = handler.obtainMessage()
//            val bundlepro = Bundle()
            //bundle.putString("myKey", topic+" : "+message);
            bundle.putString("myKey", message) //config to show just data that nodemcu microgear publish
            //split bundle
            val newMsg = message
            //String state = "";
            val parts = newMsg.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val p1 = parts[0] //NowString()
            val p2 = parts[1] //latitude
            val p3 = parts[2] //longitude
            val p4 = parts[3] //velocity
            val p5 = parts[4] //uv
            val p6 = parts[5] //temperature
            //if(Objects.equals(p6, "1")){ state = "ON"; }
            //else if(Objects.equals(p6, "0")){ state = "OFF"; }
            //if(p6 != "0" || p6 != "1"){ microgear.chat("plantone","OFF"); state = "MAN";}
            val p7 = parts[6] //strh
            //String p8 = parts[7]; //uptime
            bundle.putString("myKey", "ค่าความเข้มรังสียูวี " + p5 + " mW/cm^2" + "\nอุณหภูมิในอากาศ " + p6 + " °C" + "\nความชื้นในอากาศ " + p7 + " %"
                    //+ "\nความเร็ว " + p4 + " Km/h" + "\n|Latitude" + p2 + ":" + "\tLongitude" + p3 + "|"
                    + "\n|TIME: " + p1 + "|")
            //bundle.putString("progress",p5+","+p6+","+p7)
            msg.data = bundle
            handler.sendMessage(msg)

//            bundlepro.putString("progress",p5+","+p6+","+p7)
//            msg2.data = bundlepro
//            handler.sendMessage(msg2)

//            UVprogressBar.setProgress(p5.toInt(),true)
//            TempprogressBar.setProgress(p6.toInt(),true)
//            HumidprogressBar.setProgress(p7.toInt(),true)
            var uvv: Int = p5.toInt()
            var tpp: Int = p6.toInt()
            var hum: Int = p7.toInt()

            UVprogressBar.setProgress(uvv,true)
            TempprogressBar.setProgress(tpp,true)
            HumidprogressBar.setProgress(hum,true)
            Log.i("Message", topic + " : " + message)
        }

        override fun onPresent(token: String) {
            val msg = handler.obtainMessage()
            val bundle = Bundle()
            bundle.putString("myKey", "New friend Connect :" + token + "\n")
            msg.data = bundle
            //handler.sendMessage(msg);
            Log.i("present", "New friend Connect :" + token)
        }

        override fun onAbsent(token: String) {
            val msg = handler.obtainMessage()
            val bundle = Bundle()
            bundle.putString("myKey", "Friend lost :" + token + "\n")
            msg.data = bundle
            //handler.sendMessage(msg);
            Log.i("absent", "Friend lost :" + token)
        }

        override fun onDisconnect() {
            val msg = handler.obtainMessage()
            val bundle = Bundle()
            bundle.putString("myKey", "Disconnected" + "\n")
            msg.data = bundle
            //handler.sendMessage(msg);
            Log.i("disconnect", "Disconnected")
        }

        override fun onError(error: String) {
            val msg = handler.obtainMessage()
            val bundle = Bundle()
            bundle.putString("myKey", "Exception : " + error + "\n")
            msg.data = bundle
            //handler.sendMessage(msg);
            Log.i("exception", "Exception : " + error)
        }

        override fun onInfo(info: String) {
            val msg = handler.obtainMessage()
            val bundle = Bundle()
            bundle.putString("myKey", "Exception : " + info + "\n")
            msg.data = bundle
            //handler.sendMessage(msg);
            Log.i("info", "Info : " + info)
        }
    }
}
