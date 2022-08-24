package com.example.fordecosport.domain.Mapper;

import com.example.fordecosport.domain.Event;

import org.json.JSONException;
import org.json.JSONObject;

public class EventMapper {
    public static Event eventFromJson(JSONObject jsonObject) {
        Event event = null;
        try {
            event = new Event(jsonObject.getLong("id"),
                    jsonObject.getInt("user_id"), jsonObject.getString
                    ("event"), jsonObject.getString("time")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }


}
