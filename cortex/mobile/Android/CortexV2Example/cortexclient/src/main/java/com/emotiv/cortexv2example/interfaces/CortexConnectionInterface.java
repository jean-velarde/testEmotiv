package com.emotiv.cortexv2example.interfaces;

import org.json.JSONObject;

public interface CortexConnectionInterface {
    void onReceivedMessage(String msg, int streamId);
    void onReceivedWarningMessage(String msg);
    void onReceivedNewData(String msg);
    void onError(JSONObject error);
}
