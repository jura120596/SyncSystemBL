package com.example.fordecosport.blut;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.fordecosport.AppConstants;

public class BluetoothConnectionHelper {
    private SharedPreferences preferences;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice device;

    private ConnectionThread connectionThread;

    private String mac;

    public BluetoothConnectionHelper(Context context) {
        preferences = context.getSharedPreferences(AppConstants.MY_PREF, Context.MODE_PRIVATE);
        mac = preferences.getString(AppConstants.MAC_KEY, "");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothConnectionHelper(String deviceMac) {
        mac = deviceMac;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothDevice connect(Runnable r) {
        if (!bluetoothAdapter.isEnabled() || mac.isEmpty()) return null;
        device = bluetoothAdapter.getRemoteDevice(mac);
        if (device == null) return device;
        connectionThread = new ConnectionThread(bluetoothAdapter, device, r);
        connectionThread.start();
        return device;
    }

    public void sendMess(String message) {
        if (connectionThread != null) {
            if (connectionThread.getReceiveThread() != null) {
                try {
                    connectionThread.getReceiveThread().sendData(message.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ConnectionThread getConnectionThread() {
        return connectionThread;
    }
}

