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
    private final BluetoothDevice device;
    private TransferDataThread transferThread;
    protected BluetoothAdapter btAdapter;
    private BluetoothSocket mainSocket;
    public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private Runnable callback;

    @SuppressLint("MissingPermission")
    public ConnectionThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice device, Runnable callback) {
        this.btAdapter = bluetoothAdapter;
        this.callback = callback;
        this.device = device;
        try {
            mainSocket = this.device.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
        } catch (IOException|SecurityException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        try {
            if (btAdapter.isDiscovering()) {
                System.out.println("end discovery");
                btAdapter.cancelDiscovery();
            }
        } catch (SecurityException e) {
            System.out.println("Discovery stop error: " + e.getMessage());
        }
        try {
            mainSocket.connect();
            try {
                transferThread = new TransferDataThread(mainSocket);
                transferThread.start();
                Log.d("MyLog", "Connected");
                AppStatus.connectStatus = true;
                if (callback != null) {
                    System.out.println("Run callback after try :");
                    callback.run();
                }
            } catch (SecurityException e) {
                Log.d("MyLog", "Not connected " + e.getMessage());
                AppStatus.connectStatus = false;
            }
        } catch (IOException e) {
            System.out.println("Main socket connect error: " + e.getMessage());
            System.out.println(mainSocket);
        }
    }

    public void closeConnection() {
        try {
            AppStatus.connectStatus = false;
            if (transferThread != null) transferThread.closeStreams();
            mainSocket.close();
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.out.println("Ошибка закрытия");
        }
    }


    public TransferDataThread getTransferThread() {
        return transferThread;
    }
}
