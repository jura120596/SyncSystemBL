package com.example.fordecosport.blut;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.util.Log;

public class BluetoothConnectionHelper {
    public static BluetoothConnectionHelper instance = null;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;
    private boolean started = false;
    private ConnectionThread connectionThread;
    private String mac;

    public BluetoothConnectionHelper(String deviceMac) {
        mac = deviceMac;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static synchronized BluetoothConnectionHelper instance(String mac) {
        if (instance == null || !instance.started) {
            instance = new BluetoothConnectionHelper(mac);
        }
        return instance;
    }

    public BluetoothDevice connect(Runnable r) {
        if (!bluetoothAdapter.isEnabled() || mac.isEmpty()) return null;
        device = bluetoothAdapter.getRemoteDevice(mac);
        synchronized (this) {
            if (device == null || started) return device;
            started = true;
            connectionThread = new ConnectionThread(bluetoothAdapter, device, () -> {
                if (r != null) r.run();
                started = false;
            });
            connectionThread.start();
        }
        return device;
    }

    public void sendMess(String message) {
        if (connectionThread == null || connectionThread.getTransferThread() == null) return;
        try {
            connectionThread.getTransferThread().sendData(message.getBytes());
            Log.d("Message sended", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConnectionThread getConnectionThread() {
        return connectionThread;
    }
}

