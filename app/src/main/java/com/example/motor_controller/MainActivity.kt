package com.example.motor_controller


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MainActivity : AppCompatActivity() {

    private var SwitchState ="unChecked"
    private val TAG= "MainActivity"
    private lateinit var mqttAndroidClient: MqttAndroidClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var ToggleButton= findViewById<ToggleButton>(R.id.ToggleButton)
        var publish = findViewById<Button>(R.id.publish)
        val connectt= findViewById<Button>(R.id.connectt)
        publish.setOnClickListener {
            if (SwitchState.equals("Checked")) {
                ToggleButton.setChecked(true)
                ToggleButton.setSelected(true)
                SwitchState = "unChecked"
                val toast = Toast.makeText(applicationContext, "It's already running", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        connect(this)
        receiveMessages()


        connectt.setOnClickListener {



        }


//        val layout = findViewById<View>(R.id.r_layout) as RelativeLayout
//        val tb = ToggleButton(this)
//        tb.textOff = "OFF"
//        tb.textOn = "ON"
//        tb.isChecked = true
//        layout.addView(tb)

        ToggleButton.setOnClickListener {
            if (SwitchState.equals("Checked")) {
               ToggleButton.setChecked(true)
                ToggleButton.setSelected(true)
                SwitchState = "unChecked"
                val toast = Toast.makeText(applicationContext, "It's already running", Toast.LENGTH_SHORT)
                toast.show()
            }
            else {
                if (ToggleButton.isChecked.equals(true)) {

                    ToggleButton.setChecked(true)
                    ToggleButton.setSelected(true)
                    publish("test", "TurnOff")
                    val toast = Toast.makeText(applicationContext, "checked", Toast.LENGTH_SHORT)
                    toast.show()
                }
                else{
                    publish("test", "TurnOn")


                ToggleButton.setChecked(false)
                    ToggleButton.setSelected(false)

                    val toast = Toast.makeText(applicationContext, "UnChecked", Toast.LENGTH_SHORT)
                    toast.show()
                }

            }
            }



    }

    fun connect(applicationContext: Context) {

        mqttAndroidClient = MqttAndroidClient(applicationContext, "tcp://192.168.0.106:1883", "Android")
        try {
            val token = mqttAndroidClient.connect()
            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken)                        {
                    Log.i("Connection", "success ")
                    val toast = Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT)
                    toast.show()
                    subscribe("SwitchState")
                    //connectionStatus = true
                    // Give your callback on connection established here
                }
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    //connectionStatus = false
                    Log.i("Connection", "failure")
                    val toast = Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT)
                    toast.show()
                    // Give your callback on connection failure here

                    exception.printStackTrace()
                }
            }
        } catch (e: MqttException) {
            // Give your callback on connection failure here
            e.printStackTrace()
        }
    }


    fun subscribe(topic: String) {
        val qos = 2 // Mention your qos value
        try {
            mqttAndroidClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Give your callback on Subscription here
                }

                override fun onFailure(
                        asyncActionToken: IMqttToken,
                        exception: Throwable
                ) {
                    // Give your subscription failure callback here
                }
            })
        } catch (e: MqttException) {
            // Give your subscription failure callback here
        }
    }

    fun publish(topic: String, data: String) {
        val encodedPayload : ByteArray
        try {
            encodedPayload = data.toByteArray(charset("UTF-8"))
            val message = MqttMessage(encodedPayload)
            message.qos = 2
            message.isRetained = false
            mqttAndroidClient.publish(topic, message)
        } catch (e: Exception) {
            // Give Callback on error here
        } catch (e: MqttException) {
            // Give Callback on error here
        }
    }


    fun receiveMessages(){
        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                //connectionStatus = false
                // Give your callback on failure here
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                try {
                    val data = String(message.payload, charset("UTF-8"))

                    Log.d(TAG, data)
                    SwitchState = data


                    // data is the desired received message
                    // Give your callback on message received here
                } catch (e: Exception) {
                    // Give your callback on error here
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Acknowledgement on delivery complete
            }
        })
    }
}