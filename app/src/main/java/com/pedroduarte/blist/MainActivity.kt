package com.pedroduarte.blist

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var btn_scan : Button;
    private lateinit var list_san: ListView;

    //turn on bluettooth
    lateinit var m_bluetoothAdapter: BluetoothAdapter
    lateinit var m_pairedDevices : Set<BluetoothDevice>
    val requestEnableBluetooth = 1

    companion object{
        val extra_Address: String = "device_address"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_scan = findViewById(R.id.scan)
        list_san = findViewById(R.id.available_devices)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(m_bluetoothAdapter==null){
            Toast.makeText(applicationContext,"Device dont have bluetooth !", Toast.LENGTH_LONG).show()
        return;
        }

        BLuetoothEnable()


            btn_scan.setOnClickListener {
                BLuetoothEnable()
            }
    }

    private fun BLuetoothEnableTask(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            m_pairedDevices= m_bluetoothAdapter.bondedDevices
            var list = ArrayList<String>()
            if(!m_pairedDevices.isEmpty()){
                for(device: BluetoothDevice in m_pairedDevices){
                    list.add(device.name+"___"+device.address)
                }
            }else{
                Toast.makeText(applicationContext,"no devices paired found ",Toast.LENGTH_SHORT).show()

            }




         val adapter: ArrayAdapter<*> = object : ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                list
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)

                    view.findViewById<TextView>(android.R.id.text1).setText(list.get(position).split("___")[0])
                    view.findViewById<TextView>(android.R.id.text2).setText(list.get(position).split("___")[1])

                    return view
                }
            }

            list_san.adapter = adapter
            list_san.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                var device = list[position]
                val intent = Intent(applicationContext, ListBlueActivity::class.java)
                intent.putExtra(extra_Address,device.split("___")[1]);
                startActivity(intent)
            }




            return
        }else{
            Toast.makeText(applicationContext,"Error Blue 2",Toast.LENGTH_SHORT).show()

        }

    }
    private fun BLuetoothEnable(){

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            BLuetoothEnableTask();
        } else {
            // Permissions are not granted, request them
            Toast.makeText(applicationContext,"Error Blue 3",Toast.LENGTH_SHORT).show()

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADMIN,

                ),
                requestEnableBluetooth
            )
        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestEnableBluetooth) {
            // Check if the permissions were granted
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                BLuetoothEnableTask();

            } else {
                Toast.makeText(applicationContext,"Error Blue 1",Toast.LENGTH_SHORT).show()
                // Permissions denied, handle accordingly (e.g., show a message, disable Bluetooth features)
            }
        }
    }
}