package com.example.fordecosport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.fordecosport.domain.Event;
import com.example.fordecosport.sqlite.DataBaseHelper;
import com.example.fordecosport.domain.rest.LibApiVolley;
import com.example.fordecosport.blut.BluetoothConnectionHelper;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "SYNC SYSTEM";
    public static final int BAR_ID = 125;
    private MenuItem menuItem, connectStatus;
    private BluetoothAdapter BTadapter;
    private final int REQUEST_BL = 15;
    private BluetoothConnectionHelper bTconnect;
    private Button bA, bB, bC, bD, button;
    private ActionBar actionBar;
    private SimpleDateFormat simpleDateFormat;
    private String dataTime;
    private DataBaseHelper dataBaseHelper;
    private ImageView carView;
    private int user_id = 0;
    private LibApiVolley libApiVolley;
    private boolean active = false;


    private void init() {
        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        BTadapter = BluetoothAdapter.getDefaultAdapter();
        SharedPreferences preferences = this.getSharedPreferences(AppConstants.MY_PREF, Context.MODE_PRIVATE);
        bTconnect = BluetoothConnectionHelper.instance(preferences.getString(AppConstants.MAC_KEY, ""));
        actionBar = getSupportActionBar();
        libApiVolley = new LibApiVolley(this);

        bA = findViewById(R.id.ButA);
        bB = findViewById(R.id.ButB);
        bC = findViewById(R.id.ButC);
        bD = findViewById(R.id.ButD);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppStatus.connectStatus)
                    bTconnect.connect(null);
            }
        });

        carView = findViewById(R.id.CarImageView);

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_CONNECTED:
                        connectStatus.setIcon(R.drawable.ic_circle_green);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                    case BluetoothAdapter.STATE_DISCONNECTED:
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        connectStatus.setIcon(R.drawable.ic_circle_red);
                        break;
                }
            } else {
                switch (action) {
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        connectStatus.setIcon(R.drawable.ic_circle_green);
                        AppStatus.connectStatus = true;
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        connectStatus.setIcon(R.drawable.ic_circle_red);
                        AppStatus.connectStatus = false;
                        scan(new Handler());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        setContentView(R.layout.activity_main);

        init();
        View.OnTouchListener touchListener = (view, e) -> {
            if (AppStatus.connectStatus) {
                boolean isDown = e.getAction() == MotionEvent.ACTION_DOWN;
                boolean isUp = e.getAction() == MotionEvent.ACTION_UP;
                if(isDown || isUp) switch (view.getId()) {
                    case (R.id.ButA):
                        bTconnect.sendMess(isDown ? "A" : "a");
                        libApiVolley.addEvent(new Event(user_id, "0", Time.getTime()));
                        break;
                    case (R.id.ButB):
                        bTconnect.sendMess(isDown ? "B" : "b");
                        libApiVolley.addEvent(new Event(user_id, "1", Time.getTime()));
                        break;
                    case (R.id.ButC):
                        bTconnect.sendMess(isDown ? "C" : "c");
                        libApiVolley.addEvent(new Event(user_id, "2", Time.getTime()));
                        break;
                    case (R.id.ButD):
                        bTconnect.sendMess(isDown ? "D" : "d");
                        libApiVolley.addEvent(new Event(user_id, "3", Time.getTime()));
                        break;
                }
            }
            return false;
        };

        bA.setOnTouchListener(touchListener);
        bB.setOnTouchListener(touchListener);
        bC.setOnTouchListener(touchListener);
        bD.setOnTouchListener(touchListener);
    }


    @Override
    protected void onStart() {
        super.onStart();
        AppStatus.openStatus = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(BAR_ID);
        active = true;
        if (!AppStatus.connectStatus) bTconnect.connect(() -> {
            if (!AppStatus.connectStatus) scan(new Handler());
            else runOnUiThread(()->{connectStatus.setIcon(R.drawable.ic_circle_green);});
        });
    }
    public void scan(Handler h) {
        h.postDelayed(() -> {
            Toast.makeText(this, "Ищу машину", Toast.LENGTH_SHORT).show();
            bTconnect.connect(()->{
                if (!AppStatus.connectStatus) {
                    if (active) scan(h);
                } else {
                    runOnUiThread(()->{connectStatus.setIcon(R.drawable.ic_circle_green);});
                }
            });
        }, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menuItem = menu.findItem(R.id.bt_id);
        connectStatus = menu.findItem(R.id.id_status);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.bt_id) {
            if (!BTadapter.isEnabled()) {
                enableBt();
            } else {
                try {
                    BTadapter.disable();
                    menuItem.setIcon(R.drawable.ic_baseline_bluetooth_24);
                } catch (SecurityException securityException) {
                    securityException.printStackTrace();
                }
            }
        } else if (item.getItemId() == R.id.bt_menu) {
            if (BTadapter.isEnabled()) {
                Intent intent = new Intent(MainActivity.this, DeviceSearchActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Включи Bluetooth", Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.id_connect) {
            if (!BTadapter.isEnabled()) {
                enableBt();
            } else {
                bTconnect.connect(null);
            }
        } else if (item.getItemId() == R.id.Event_Log) {
            Intent intent = new Intent(MainActivity.this, EventLogAct.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BL) {
            if (requestCode == RESULT_OK) {
                setBtIcon();
            }
        }
    }

    private void setBtIcon() {
        if (BTadapter.isEnabled()) {
            menuItem.setIcon(R.drawable.ic_baseline_bluetooth_disabled_24);
        } else {
            menuItem.setIcon(R.drawable.ic_baseline_bluetooth_24);
        }
    }

    private void enableBt() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            startActivityForResult(intent, REQUEST_BL);
        } catch (SecurityException e) {
        }
        setBtIcon();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;
        notificationManager();
        if (bTconnect != null && bTconnect.getConnectionThread() != null) bTconnect.getConnectionThread().closeConnection();
    }

    public void notificationManager() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent iA = new Intent(this, SyncIntentService.class).setAction("A");
        remoteViews.setOnClickPendingIntent(R.id.buttonA, PendingIntent.getService(this, 0, iA, PendingIntent.FLAG_IMMUTABLE));

        Intent iB = new Intent(this, SyncIntentService.class).setAction("B");
        remoteViews.setOnClickPendingIntent(R.id.buttonB, PendingIntent.getService(this, 1, iB, PendingIntent.FLAG_IMMUTABLE));

        Intent iC = new Intent(this, SyncIntentService.class).setAction("C");
        remoteViews.setOnClickPendingIntent(R.id.buttonC, PendingIntent.getService(this, 2, iC, PendingIntent.FLAG_IMMUTABLE));

        Intent iD = new Intent(this, SyncIntentService.class).setAction("D");
        remoteViews.setOnClickPendingIntent(R.id.buttonD, PendingIntent.getService(this, 3, iD, PendingIntent.FLAG_IMMUTABLE));

        Intent iS = new Intent(this, SyncIntentService.class).setAction("start");
        remoteViews.setOnClickPendingIntent(R.id.buttonStart, PendingIntent.getService(this, 4, iS, PendingIntent.FLAG_IMMUTABLE));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID).
                setContent(remoteViews).setSmallIcon(R.drawable.ic_car).setContentTitle("SYNC");
        createChannelIfNeeded(notificationManager);
        notificationManager.notify(null, BAR_ID, builder.build());
    }

    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }


}