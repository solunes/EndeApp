package com.solunes.endeapp.networking;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.solunes.endeapp.models.User;
import com.solunes.endeapp.utils.StringUtils;
import com.solunes.endeapp.utils.Urls;
import com.solunes.endeapp.utils.UserPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Hashtable;

/**
 * Created by jhonlimaster on 15-11-16.
 */

public class Token {

    private static final String TAG = "Token";
    public static final String KEY_TOKEN = "key_token";
    public static final String KEY_EXPIRATION_DATE = "key_expiration_date";

    public static void getToken(Context context, User user, CallbackToken callbackToken) {

        String token = UserPreferences.getString(context, KEY_TOKEN);
        String expirationDate = UserPreferences.getString(context, KEY_EXPIRATION_DATE);
        if (token == null) {
            tokenRequest(context, user,callbackToken);
        } else {
            Date date = StringUtils.formateStringFromDate(StringUtils.DATE_FORMAT, expirationDate);
            if (date.getTime() < System.currentTimeMillis()) {
                tokenRequest(context, user, callbackToken);
            } else {
                callbackToken.onSuccessToken();
            }
        }
    }

    private static void tokenRequest(final Context context, User user, final CallbackToken callbackToken) {
        Hashtable<String, String> params = new Hashtable<>();
        params.put("LecCod", user.getLecCod());
        params.put("password", user.getLecPas());
        new PostRequest(context, params, null, Urls.urlauthenticate(context), new CallbackAPI() {
            @Override
            public void onSuccess(String result, int statusCode) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    Log.e(TAG, "onSuccess: token " + result);
                    String token = jsonObject.getString("token");
                    String expirationDate = jsonObject.getString("expirationDate");
                    UserPreferences.putString(context, KEY_TOKEN, token);
                    UserPreferences.putString(context, KEY_EXPIRATION_DATE, expirationDate);
                    callbackToken.onSuccessToken();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show();
                callbackToken.onFailToken();
            }
        }).execute();
    }

    public interface CallbackToken{
        void onSuccessToken();
        void onFailToken();
    }
}
