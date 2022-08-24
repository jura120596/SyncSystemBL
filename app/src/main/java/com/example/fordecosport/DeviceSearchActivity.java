package com.example.fordecosport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.fordecosport.adapter.DeviceListAdapter;
import com.example.fordecosport.adapter.DeviceListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceSearchActivity extends AppCompatActivity {

    private final int BT_REQUEST_PERM = 123;
    private ListView listView;
    private DeviceListAdapter deviceListAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private List<DeviceListItem> list;
    boolean hasBluetoothPermission = false;
    private boolean isDiscovery = false;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt2);
        getBluetoothPermission();
        init();

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, filter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isDiscovery) {
                try {
                    bluetoothAdapter.cancelDiscovery();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                isDiscovery = false;
                getPairedDevices();
            } else {
                finish();
            }
        } else if (item.getItemId() == R.id.id_search) {
            if (isDiscovery) return true;
            actionBar.setTitle(R.string.discovering);
            list.clear();
            DeviceListItem itemTytle = new DeviceListItem();
            itemTytle.setItemType(DeviceListAdapter.TITLE_ITEM_TYPE);
            list.add(itemTytle);
            deviceListAdapter.notifyDataSetChanged();
            try {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2);
                    }
                }
                bluetoothAdapter.startDiscovery();
            }catch (Exception e ) {
                e.printStackTrace();
            }
            isDiscovery = true;
        }

        return true;
    }


    private void init() {
        actionBar = getSupportActionBar();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        list = new ArrayList<>();
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.listV);
        deviceListAdapter = new DeviceListAdapter(this, R.layout.bt_list_item, list);
        listView.setAdapter(deviceListAdapter);
        getPairedDevices();
        onItemClickListener();
    }

    private void onItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceListItem item = (DeviceListItem) adapterView.getItemAtPosition(i);
                if (item.getItemType().equals(DeviceListAdapter.DISCOVERY_ITEM_TYPE)) {
                    try {
                        item.getBluetoothDevice().createBond();
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void getPairedDevices() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            list.clear();
            for (BluetoothDevice device : pairedDevices) {
                DeviceListItem item = new DeviceListItem();
                item.setBluetoothDevice(device);
                list.add(item);
            }
            deviceListAdapter.notifyDataSetChanged();
            }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BT_REQUEST_PERM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasBluetoothPermission = true;
            } else {
                Toast.makeText(this, "No request", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, BT_REQUEST_PERM);
        } else {
            hasBluetoothPermission = true;
        }


    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                DeviceListItem item = new DeviceListItem();
                item.setBluetoothDevice(device);
                item.setItemType(DeviceListAdapter.DISCOVERY_ITEM_TYPE);
                list.add(item);
                deviceListAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                isDiscovery = false;
                getPairedDevices();
                actionBar.setTitle(R.string.app_name);
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        getPairedDevices();
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
