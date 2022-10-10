package com.example.fordecosport;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.fordecosport.blut.BluetoothConnectionHelper;
import com.example.fordecosport.domain.Event;
import com.example.fordecosport.domain.rest.LibApiVolley;

import java.util.Locale;

public class SyncIntentService extends IntentService {


    public SyncIntentService() {
        super("SyncIntentService");
    }
    public void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        });
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println(SyncIntentService.class);
        LibApiVolley libApiVolley = new LibApiVolley(this);
        String mac = this.getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE).getString(AppConstants.MAC_KEY, "");
        int user_id = 0 ;
        BluetoothConnectionHelper device = BluetoothConnectionHelper.instance(mac);
        device.connect(() -> {
            if (!AppStatus.connectStatus)  {
                showToast("Нет соединения");
                System.out.println("Нет соединения");
                return;
            }
            String action = intent.getAction();
            switch (action) {
                case "A":
                case "B":
                case "C":
                case "D":
                    device.sendMess(action);
                    pause(1);
                    device.sendMess(action.toLowerCase(Locale.ROOT));
                    libApiVolley.addEvent(new Event(user_id,(action.charAt(0) - "A".charAt(0))+"",Time.getTime()));
                    break;
                case "start":
                    device.sendMess("A");
                    pause(15);
                    libApiVolley.addEvent(new Event(user_id,"3",Time.getTime()));
                    device.sendMess("a");
                    break;
            }
            device.getConnectionThread().closeConnection();
        });
    }

    private void pause(int i) {
        try {
            Thread.sleep(100*i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}