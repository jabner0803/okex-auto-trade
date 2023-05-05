package com.phemex.client.listener;

public interface PhemexMessageListener {

    void onMessage(String json);
    void onError(Exception ex);
}
