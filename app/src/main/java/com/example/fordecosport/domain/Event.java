package com.example.fordecosport.domain;

public class Event {

    private long id;
    private int user_id;
    private String event;
    private String time;


    public Event(long id, int user_id, String event, String time) {
        this.id = id;
        this.user_id = user_id;
        this.event = event;
        this.time = time;
    }

    public Event(int user_id, String event, String time) {
        this.user_id = user_id;
        this.event = event;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", event='" + event + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getEvent() {
        return event;
    }

    public String getTime() {
        return time;
    }
}
