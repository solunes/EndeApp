package com.solunes.endeapp.networking;

public interface CallbackAPI {
    void onSuccess(String result, int statusCode);
    void onFailed(String reason, int statusCode);
}