package com.solunes.endeapp.utils;

import android.content.Context;

import com.solunes.endeapp.activities.AdminActivity;
import com.solunes.endeapp.networking.Token;

/**
 * Created by jhonlimaster on 12-02-17.
 */

public class AppPreferences {
    private String key_token;
    private String key_expiration_date;
    private String key_domain;
    private long update_rate;
    private long download;
    private long send;
    private int last_pager_position;
    private int user_id;
    private int update_rate_month;
    private boolean login;
    private boolean was_upload;

    public void sendPreferences(Context context){
        UserPreferences.putString(context, "key_token", key_token);
        UserPreferences.putString(context, "key_expiration_date", key_expiration_date);
        UserPreferences.putString(context, "key_domain", key_domain);
        UserPreferences.putLong(context, "update_rate", update_rate);
        UserPreferences.putLong(context, "download", download);
        UserPreferences.putLong(context, "send", send);
        UserPreferences.putInt(context, "last_pager_position", last_pager_position);
        UserPreferences.putInt(context, "user_id", user_id);
        UserPreferences.putInt(context, "update_rate_month", update_rate_month);
        UserPreferences.putBoolean(context, "login", login);
        UserPreferences.putBoolean(context, "was_upload", was_upload);
    }
}
