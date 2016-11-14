package com.solunes.endeapp.networking;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.solunes.endeapp.activities.AdminActivity;
import com.solunes.endeapp.utils.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;


public class GetRequest extends AsyncTask<String, Void, String> {
    private String TAG = GetRequest.class.getSimpleName();
    private Hashtable<String, String> headers;
    private String urlEndpoint;
    private int statusCode;
    private String token;
    private CallbackAPI callbackAPI;


    public GetRequest(Context context, String urlEndpoint, CallbackAPI callbackAPI) {
        this.headers = new Hashtable<>();
        this.urlEndpoint = urlEndpoint;
        this.callbackAPI = callbackAPI;
        this.token = UserPreferences.getString(context, AdminActivity.KEY_TOKEN);
    }

    @Override
    protected String doInBackground(String... urls) {
        HttpURLConnection urlConnection = null;
        Log.e(TAG, "endPoint: " + urlEndpoint);

        try {
            if (token != null) {
                urlEndpoint += "?token=" + token;
            }
            urlConnection = (HttpURLConnection) new URL(urlEndpoint).openConnection();
            int TIMEOUT_VALUE = 10000;
            urlConnection.setReadTimeout(TIMEOUT_VALUE);
            urlConnection.setConnectTimeout(TIMEOUT_VALUE);
            urlConnection.setRequestMethod("GET");


            urlConnection.setDoInput(true);
            urlConnection.connect();
            statusCode = urlConnection.getResponseCode();

            if (isSuccessStatusCode()) {
                return getStringFromStream(urlConnection.getInputStream());
            } else {
                return getStringFromStream(urlConnection.getErrorStream());
            }

        } catch (IOException e) {
            Log.e(TAG, "Exception " + urlEndpoint + " --------->>>>: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            callbackAPI.onFailed("", 404);
        }

        if (isSuccessStatusCode()) {
            callbackAPI.onSuccess(result, getStatusCode());
        } else {
            callbackAPI.onFailed(result, getStatusCode());
        }

    }

    public String getStringFromStream(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return stringBuilder.toString();
    }

    public boolean isSuccessStatusCode() {
        return (getStatusCode() >= 200 && getStatusCode() <= 250);
    }
}
