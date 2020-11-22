package com.dkarakay.relaycontroller

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.*

class ControlActivity : AppCompatActivity() {

    companion object {
        var mUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var mBluetoothSocket: BluetoothSocket? = null
        lateinit var mProgress: ProgressDialog
        lateinit var mBluetoothAdapter: BluetoothAdapter
        var mIsConnected: Boolean = false
        lateinit var mAddress: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        mAddress = intent.getStringExtra(MainActivity.EXTRA_ADDRESS).toString()

        ConnectToDevice(this).execute()

        val ledOnButton: Button = findViewById(R.id.ledOnButton)
        val ledOffButton: Button = findViewById(R.id.ledOffButton)
        val disconnectDeviceButton: Button = findViewById(R.id.disconnectDeviceButton)

        ledOnButton.setOnClickListener { sendCommand("1") }
        ledOffButton.setOnClickListener { sendCommand("0") }
        disconnectDeviceButton.setOnClickListener { disconnect() }
    }

    private fun sendCommand(input: String) {
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket!!.close()
                mBluetoothSocket = null
                mIsConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context = c

        override fun onPreExecute() {
            super.onPreExecute()
            mProgress = ProgressDialog.show(
                context,
                context.getString(R.string.connecting),
                context.getString(R.string.connecting)
            )
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (mBluetoothSocket == null || !mIsConnected) {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = mBluetoothAdapter.getRemoteDevice(mAddress)
                    mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(mUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    mBluetoothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                mIsConnected = true
            }
            mProgress.dismiss()
        }
    }
}