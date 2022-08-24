package com.example.fordecosport.blut;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
;

import com.example.fordecosport.AppStatus;

import java.io.IOException;


public class ConnectionThread extends Thread {
    private TransferDataThread receiveThread;
    protected BluetoothAdapter btAdapter;
    private BluetoothSocket mainSocket;
    public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private Runnable callback;

    @SuppressLint("MissingPermission")
    public ConnectionThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice device, Runnable callback) {
        this.btAdapter = bluetoothAdapter;
        this.callback = callback;
        try {
            try {
                mainSocket = device.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        try {
            btAdapter.cancelDiscovery();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        try {
            try {
                mainSocket.connect();
                receiveThread = new TransferDataThread(mainSocket);
                receiveThread.start();
            } catch (SecurityException e) {
            }
            Log.d("MyLog", "Connected");
            if (callback != null) callback.run();
            AppStatus.connectStatus = true;

        } catch (IOException e) {
            Log.d("MyLog", "Not connected");
            AppStatus.connectStatus = false;
        }
    }

    public void closeConnection() {
        try {
            mainSocket.close();
            AppStatus.connectStatus = false;
        } catch (IOException e) {
        }
    }


    public TransferDataThread getReceiveThread() {
        return receiveThread;
    }
}
