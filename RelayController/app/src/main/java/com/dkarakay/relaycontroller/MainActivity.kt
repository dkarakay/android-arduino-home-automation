package com.dkarakay.relaycontroller

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*

class MainActivity : AppCompatActivity() {

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private lateinit var mPairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1


    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            Toast.makeText(applicationContext, R.string.does_not_support_bluetooth, Toast.LENGTH_SHORT).show()
            return
        }
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        val refreshButton: Button = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener { pairedDeviceList() }


    }


    private fun pairedDeviceList() {
        mPairedDevices = mBluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if (!mPairedDevices.isEmpty()) {
            for (device: BluetoothDevice in mPairedDevices) {
                list.add(device)
                Log.i("device", "" + device)
            }
        } else {
            Toast.makeText(applicationContext, R.string.could_not_find_paired_device, Toast.LENGTH_SHORT).show()
        }

        val nameList: ArrayList<String> = ArrayList()
        for (item in list) {
            nameList.add(item.name)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList)
        val deviceListView: ListView = findViewById(R.id.deviceListView)
        deviceListView.adapter = adapter

        deviceListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (mBluetoothAdapter!!.isEnabled) {
                    Toast.makeText(applicationContext, R.string.bluetooth_opened, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, R.string.bluetooth_closed, Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, R.string.bluetooth_cancelled, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
