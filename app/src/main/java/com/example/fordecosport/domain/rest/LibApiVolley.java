package com.example.fordecosport.domain.rest;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fordecosport.EventLogAct;
import com.example.fordecosport.domain.Event;
import com.example.fordecosport.domain.Mapper.EventMapper;
import com.example.fordecosport.NoDb;
import com.example.fordecosport.domain.Mapper.UserMapper;
import com.example.fordecosport.domain.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LibApiVolley implements LibApi {
    public static final String API_TEST = "API_TEST";
    private final Context context;
    public static final String BASE_URL = "https://slavacomm.herokuapp.com";
    private Response.ErrorListener errorListener;
    private final int USER_ID= 0;

    public LibApiVolley(Context context) {
        this.context = context;
        errorListener = error -> { error.printStackTrace();};
    }

    @Override
    public void fillEvent() {
        String url = BASE_URL + "/event";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    NoDb.ALL_EVENT_LIST.clear();
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject jsonObject = response.getJSONObject(i);
                        Event event = EventMapper.eventFromJson(jsonObject);
                        if (event.getUser_id()==USER_ID){
                        NoDb.ALL_EVENT_LIST.add(event);}
                    }
                    if (context instanceof EventLogAct) {
                        ((EventLogAct)context).updateEvents();
                    }
                    Log.d(API_TEST, NoDb.ALL_EVENT_LIST.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, errorListener);
        requestQueue.add(arrayRequest);

    }

    @Override
    public void fillUser() {
        String url = BASE_URL + "/user";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        User user = UserMapper.eventFromJson(jsonObject);
                        NoDb.ALL_USER_LIST.add(user);
                    }
                    Log.d(API_TEST, NoDb.ALL_USER_LIST.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, errorListener);
        requestQueue.add(arrayRequest);

    }

    @Override
    public void fillUserByEmail(String mail) {

        String url = BASE_URL + "/user/" + mail;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    User user = UserMapper.eventFromJson(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, errorListener);
        requestQueue.add(arrayRequest);
    }


    @Override
    public void addUser(User user) {
        String url = BASE_URL + "/user";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                fillUser();
                Log.d(API_TEST, response);
            }
        }, errorListener
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("password", user.getPassword());
                params.put("mail", user.getE_mail());

                return params;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public void addEvent(Event event) {
        String url = BASE_URL + "/event";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                fillEvent();
                Log.d(API_TEST, response);
            }
        }, errorListener
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(event.getUser_id()));
                params.put("event", event.getEvent());
                params.put("time", event.getTime());

                return params;
            }
        };
        requestQueue.add(request);


    }

    @Override
    public void updateUser(int id, String newPassword, String newMail) {


    }
}
