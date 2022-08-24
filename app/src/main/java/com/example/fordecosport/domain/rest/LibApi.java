package com.example.fordecosport.domain.rest;

import com.example.fordecosport.domain.Event;
import com.example.fordecosport.domain.User;

public interface LibApi {
    void fillEvent();

    void fillUser();

    void fillUserByEmail(String mail);

    void addUser(User user);

    void addEvent(Event event);

    void updateUser(int id, String newPassword, String newMail);

}
