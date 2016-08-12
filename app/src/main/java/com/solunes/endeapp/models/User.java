package com.solunes.endeapp.models;

/**
 * Created by jhonlimaster on 11-08-16.
 */
public class User {
    private int id;
    private String username;
    private String password;

    public enum Columns {
        id, username, password
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
