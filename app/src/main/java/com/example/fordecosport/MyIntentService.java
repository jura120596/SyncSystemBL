package com.example.fordecosport;

import android.app.IntentService;
import android.content.Intent;

import com.example.fordecosport.blut.BluetoothConnectionHelper;
import com.example.fordecosport.domain.Event;
import com.example.fordecosport.domain.rest.LibApiVolley;

public class MyIntentService extends IntentService {
    private BluetoothConnectionHelper btConnect;


    public MyIntentService() {
        super("MyIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        LibApiVolley libApiVolley = new LibApiVolley(this);
        String mac = this.getSharedPreferences(AppConstants.MY_PREF, MODE_PRIVATE).getString(AppConstants.MAC_KEY, "");
        int user_id = 0 ;
        System.out.println(mac);
        btConnect = new BluetoothConnectionHelper(mac);
        btConnect.connect(new Runnable() {
            @Override
            public void run() {

                switch (intent.getAction()) {
                    case "A_button":
                        btConnect.sendMess("D");
                        libApiVolley.addEvent(new Event(user_id,"0",Time.getTime()));
                        btConnect.sendMess("d");
                        btConnect.getConnectionThread().closeConnection();
                        break;
                    case "B_button":
                        btConnect.sendMess("B");
                        libApiVolley.addEvent(new Event(user_id,"1",Time.getTime()));
                        btConnect.sendMess("b");
                        btConnect.getConnectionThread().closeConnection();
                        break;
                    case "C_button":
                        btConnect.sendMess("C");
                        libApiVolley.addEvent(new Event(user_id,"2",Time.getTime()));
                        btConnect.sendMess("c");
                        btConnect.getConnectionThread().closeConnection();
                        break;
                    case "D_button":
                        btConnect.sendMess("A");
                        libApiVolley.addEvent(new Event(user_id,"3",Time.getTime()));
                        btConnect.sendMess("a");
                        btConnect.getConnectionThread().closeConnection();
                        break;
                }
            }
        });
    }
}