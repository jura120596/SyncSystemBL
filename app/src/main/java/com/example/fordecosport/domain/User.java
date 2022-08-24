package com.example.fordecosport.domain;

public class User {
    private int id;
    private String password;
    private String e_mail;


    public User(int id, String password, String e_mail) {
        this.id = id;
        this.password = password;
        this.e_mail = e_mail;
    }

    public User(String password, String e_mail) {
        this.password = password;
        this.e_mail = e_mail;
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getE_mail() {
        return e_mail;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", e_mail='" + e_mail + '\'' +
                '}';
    }
}
