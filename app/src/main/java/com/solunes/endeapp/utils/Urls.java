package com.solunes.endeapp.utils;

import android.content.Context;

import com.solunes.endeapp.activities.AdminActivity;

/**
 * Created by jhonlimaster on 24-10-16.
 */

public class Urls {

    public static String endpointBase(Context context) {
        String url = UserPreferences.getString(context, AdminActivity.KEY_DOMAIN);
        return "http://"+url;
    }

    public static String urlDescarga(Context context) {
        return endpointBase(context) + "/api/descarga/";
    }

    public static String urlSubida(Context context) {
        return endpointBase(context) + "/api/subida";
    }

    public static String urlParametros(Context context) {
        return endpointBase(context) + "/api/parametros-fijos";
    }
}
