package com.pedroduarte.blist

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.lang.Exception
import java.util.UUID

class ListBlueActivity : AppCompatActivity() {

    companion object{
        var myUID:UUID = UUID.fromString("41c3e7b0-8ed4-11ee-b9d1-0242ac120002");
        var bluetoothSocket: BluetoothSocket?=null
        lateinit var m_progress: ProgressDialog
        lateinit var bluetoothAdapter: BluetoothAdapter
        var IsConnected = false
        lateinit var ADDRESS:String
    }

    private lateinit var btn_send: Button
    var numero = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_blue)

       ADDRESS= intent.getStringExtra(MainActivity.extra_Address).toString()

        ConnectToDevice(this).execute()

        btn_send = findViewById(R.id.button)





        btn_send.setOnClickListener {
            numero +=1;
            sendComman("ola "+numero.toString())
        }

    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }

    private fun sendComman(input: String){
        if(bluetoothSocket!=null){
            try {
                bluetoothSocket!!.outputStream.write(input.toByteArray())
            }catch (erro: Exception){
                println("Error: "+erro.printStackTrace())
            }
        }else{
            Toast.makeText(applicationContext,"Error no sendCommand socket",Toast.LENGTH_SHORT).show()
        }
    }

    private  fun disconnect(){
        if(bluetoothSocket!=null){
            try {
                bluetoothSocket!!.close()
                bluetoothSocket=null
                IsConnected = false
            }catch (erro: Exception){
                println("disconnect error: "+erro.printStackTrace())
            }
        }
    }

    private  class ConnectToDevice(context: Context) : AsyncTask<Void , Void, String>(){

        private var connectSuccess = true
        private lateinit var contexT: Context

        //contructor
        init {
                this.contexT = context
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(contexT, "Connections ...","please wait ")
        }

        override fun doInBackground(vararg params: Void?): String? {
            try {
                    if(bluetoothSocket==null || !IsConnected){
                        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                        val bluetoodevice = bluetoothAdapter.getRemoteDevice(ADDRESS)
                        if (ActivityCompat.checkSelfPermission(
                                contexT,
                                Manifest.permission.BLUETOOTH_CONNECT,
                            ) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(
                                contexT,
                                Manifest.permission.BLUETOOTH_SCAN,
                            ) != PackageManager.PERMISSION_GRANTED
                            &&ActivityCompat.checkSelfPermission(
                                contexT,
                                Manifest.permission.BLUETOOTH,
                            ) != PackageManager.PERMISSION_GRANTED
                            &&ActivityCompat.checkSelfPermission(
                                contexT,
                                Manifest.permission.BLUETOOTH_ADMIN,
                            ) != PackageManager.PERMISSION_GRANTED

                        ) {
                         Toast.makeText(contexT,"Error no bluetooth",Toast.LENGTH_SHORT).show()
                        }
                        bluetoothSocket = bluetoodevice.createInsecureRfcommSocketToServiceRecord(myUID)
                        bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("blueList", myUID)

                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                        bluetoothSocket!!.connect()
                    }
            }catch (erro: IOException){
                connectSuccess = false
                println("erro do in background: "+erro.printStackTrace())
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Toast.makeText(contexT, "Erro ao conectar !",Toast.LENGTH_SHORT).show()
            }else{
                IsConnected = true
            }
            m_progress.dismiss()
        }
    }

}