package com.example.fordecosport.adapter;

import android.app.LauncherActivity;
import android.bluetooth.BluetoothDevice;

public class DeviceListItem extends LauncherActivity.ListItem {
    private BluetoothDevice bluetoothDevice;
    private String itemType = DeviceListAdapter.DEF_ITEM_TYPE;

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }


}
