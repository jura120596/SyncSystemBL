package com.example.fordecosport;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Time {
    public static String getTime(){
        String dataTime = (new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")).format(Calendar.getInstance().getTime());
        return dataTime;
    }

}
