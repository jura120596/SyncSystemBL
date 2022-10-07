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


    private void init() {
        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        BTadapter = BluetoothAdapter.getDefaultAdapter();
        bTconnect = new BluetoothConnectionHelper(this);
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
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            private View view;
            private MotionEvent motionEvent;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                this.view = view;
                this.motionEvent = motionEvent;

                if (AppStatus.connectStatus) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        switch (view.getId()) {
                            case (R.id.ButA):
                                bTconnect.sendMess("D");
                                Log.d("MyLog", "Send D");
                                libApiVolley.addEvent(new Event(user_id, "0", Time.getTime()));
                                break;
                            case (R.id.ButB):
                                bTconnect.sendMess("B");
                                libApiVolley.addEvent(new Event(user_id, "1", Time.getTime()));

                                Log.d("MyLog", "Send B");
                                break;
                            case (R.id.ButC):
                                bTconnect.sendMess("C");
                                libApiVolley.addEvent(new Event(user_id, "2", Time.getTime()));

                                Log.d("MyLog", "Send C");
                                break;
                            case (R.id.ButD):
                                bTconnect.sendMess("A");
                                libApiVolley.addEvent(new Event(user_id, "3", Time.getTime()));
                                Log.d("MyLog", "Send A");
                                break;
                        }
                    }

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        switch (view.getId()) {
                            case (R.id.ButA):
                                bTconnect.sendMess("d");
                                Log.d("MyLog", "Send d");
                                break;
                            case (R.id.ButB):
                                bTconnect.sendMess("b");
                                Log.d("MyLog", "Send b");
                                break;
                            case (R.id.ButC):
                                bTconnect.sendMess("c");
                                Log.d("MyLog", "Send c");
                                break;
                            case (R.id.ButD):
                                bTconnect.sendMess("a");
                                Log.d("MyLog", "Send a");
                                break;
                        }
                    }
                }
                return false;
            }

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
        if (!AppStatus.connectStatus) bTconnect.connect(null);
        notificationManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AppStatus.connectStatus) scan(new Handler());
    }
    public void scan(Handler h) {
        h.postDelayed(() -> {
            Toast.makeText(this, "Ищу машину", Toast.LENGTH_SHORT).show();
            bTconnect.connect(()->{
                if (!AppStatus.connectStatus) {
                    scan(h);
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
        if (AppStatus.connectStatus) bTconnect.getConnectionThread().closeConnection();
    }

    public void notificationManager() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent A_Intent = new Intent(this, MyIntentService.class);
        A_Intent.setAction("A_button");
        remoteViews.setOnClickPendingIntent(R.id.buttonA, PendingIntent.getService(this, 0, A_Intent, PendingIntent.FLAG_IMMUTABLE));

        Intent B_Inent = new Intent(this, MyIntentService.class);
        B_Inent.setAction("B_button");
        remoteViews.setOnClickPendingIntent(R.id.buttonB, PendingIntent.getService(this, 1, B_Inent, PendingIntent.FLAG_IMMUTABLE));

        Intent C_Inent = new Intent(this, MyIntentService.class);
        C_Inent.setAction("C_button");
        remoteViews.setOnClickPendingIntent(R.id.buttonC, PendingIntent.getService(this, 2, C_Inent, PendingIntent.FLAG_IMMUTABLE));

        Intent D_Inent = new Intent(this, MyIntentService.class);
        D_Inent.setAction("D_button");
        remoteViews.setOnClickPendingIntent(R.id.buttonD,
                PendingIntent.getService(this, 3, D_Inent, PendingIntent.FLAG_IMMUTABLE));


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID).
                setContent(remoteViews).setSmallIcon(R.drawable.ic____________1).setContentTitle("SYNC");
        createChannelIfNeeded(notificationManager);
        notificationManager.notify(null, 125, builder.build());
    }

    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }


}