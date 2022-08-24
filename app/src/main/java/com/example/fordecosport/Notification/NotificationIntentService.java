package com.example.fordecosport.Notification;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.fordecosport.AppConstants;
import com.example.fordecosport.blut.BluetoothConnectionHelper;


public class NotificationIntentService extends IntentService {

    private BluetoothConnectionHelper btConnect;
    public NotificationIntentService() {
        super("NotificationIntent_Service");

    }

    @Override
    public void onHandleIntent(@Nullable Intent intent) {
        String mac = this.getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE).getString(AppConstants.MAC_KEY, "");
        System.out.println(mac);
        btConnect = new BluetoothConnectionHelper(mac);
        btConnect.connect(new Runnable() {
            @Override
            public void run() {

                switch (intent.getAction()) {
                    case "A_button":
                        btConnect.sendMess("A");

                        btConnect.getConnectionThread().closeConnection();
                        break;
                    case "B_button":
                        btConnect.sendMess("B");
                        btConnect.getConnectionThread().closeConnection();
                        break;
                    case "C_button":
                        btConnect.sendMess("C");
                        btConnect.getConnectionThread().closeConnection();
                        break;
                    case "D_button":
                        btConnect.sendMess("D");
                        btConnect.getConnectionThread().closeConnection();
                        break;
                }
            }
        });
    }
}
