package com.emotiv.cortexv2example.cortexlib;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.emotiv.cortexv2example.datastream.DataStream;
import com.emotiv.cortexv2example.utils.Constant;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class CortexAPIRequester {
    private CortexConnection mCortexConnection;
    public CortexAPIRequester(CortexConnection cortexConnection) {
        mCortexConnection = cortexConnection;
    }

    public void authenticate(@NonNull Activity activity, String clientId, int activityHandleCode) {
        try {
            mCortexConnection.authenticate(activity, clientId, activityHandleCode);
        } catch (Exception e) { e.printStackTrace(); }
}
    public String getAuthenticationCode(int requestCode, @NonNull Intent intent)
    {
        String result = "";
        try {
            result = mCortexConnection.getAuthenticationCode(requestCode, intent);
        } catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public void loginWithAuthenticationCode(String auCode) {
        try {
            JSONObject params = new JSONObject();
            params.put("clientId", Constant.CLIENT_ID);
            params.put("clientSecret", Constant.CLIENT_SECRET);
            params.put("code", auCode);
            mCortexConnection.sendRequest(Constant.ACCESS_STREAM, Constant.LOGIN_REQUEST_ID, "loginWithAuthenticationCode", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }


        public void logout(String user) {
        try {
            JSONObject params = new JSONObject();
            params.put("username", user);
            mCortexConnection.sendRequest(Constant.ACCESS_STREAM, Constant.LOGOUT_REQUEST_ID, "logout", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // to get User Logged In information
    public void getUserLogin() {
        mCortexConnection.sendRequest(Constant.ACCESS_STREAM, Constant.GET_USER_LOGGED_IN_REQUEST_ID, "getUserLogin", null, false);
    }

    // get an authorization token
    public void authorize() {
        try {
            JSONObject params = new JSONObject();
            params.put("clientId", Constant.CLIENT_ID);
            params.put("clientSecret", Constant.CLIENT_SECRET);
            params.put("debit", Constant.DEBIT_NUMBER);
            params.put("license", Constant.LICENSE_ID);
            mCortexConnection.sendRequest(Constant.ACCESS_STREAM, Constant.AUTHORIZE_REQUEST_ID, "authorize", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void acceptEULA() {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", DataStream.getCortexToken());
            mCortexConnection.sendRequest(Constant.ACCESS_STREAM, Constant.ACCEPT_EULA_REQUEST_ID, "acceptLicense", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // to get current user information. In addition, the eula acceptance information included
    public void geUserInformation() {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", DataStream.getCortexToken());
            mCortexConnection.sendRequest(Constant.ACCESS_STREAM, Constant.GET_USER_INFORMATION_REQUEST_ID, "getUserInformation", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // to get current license information. If user is online, the latest information from cloud
    // will be return, otherwise return information from local database
    public void getLicenseInformation() {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", DataStream.getCortexToken());
            mCortexConnection.sendRequest(Constant.ACCESS_STREAM, Constant.GET_LICENSE_INFORMATION_REQUEST_ID, "getLicenseInfo", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // list all the headsets connected to your computer
    public void queryHeadset() {
        mCortexConnection.sendRequest(Constant.HEADSET_STREAM, Constant.QUERY_HEADSET_REQUEST_ID, "queryHeadsets", null, false);
    }

    // to connect/disconnect a headset.
    // in addition, refresh headset to new updated information is applied to headset
    public void controlDevice(String command, String headsetID) {
        try {
            JSONObject params = new JSONObject();
            params.put("command", command);
            if (headsetID != "")
                params.put("headset", headsetID);
            mCortexConnection.sendRequest(Constant.HEADSET_STREAM, Constant.CONTROL_DEVICE_REQUEST_ID, "controlDevice", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // list all the virtual headsets
    public void queryVirtualHeadsets() {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", DataStream.getCortexToken());
            mCortexConnection.sendRequest(Constant.HEADSET_STREAM, Constant.QUERY_VIRTUAL_HEADSETS_REQUEST_ID, "queryVirtualHeadsets", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // list all the virtual headsets
    public void createVirtualHeadset() {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", DataStream.getCortexToken());
            params.put("type", "INSIGHT");
            params.put("motionRate", 32); // the eegRate is always 128
            // Config good cq data for channel Insight headset
            JSONObject cq_data = new JSONObject();
            cq_data.put("AF3", 4);
            cq_data.put("AF4", 4);
            cq_data.put("T7", 4);
            cq_data.put("T8", 4);
            cq_data.put("Pz", 4);
            params.put("cq", cq_data);
            mCortexConnection.sendRequest(Constant.HEADSET_STREAM, Constant.CREATE_VIRTUAL_HEADSET_REQUEST_ID, "createVirtualHeadset", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // change the power status of a virtual headset
    public void powerVirtualHeadset(String virtualHeadsetId, boolean powerOn) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", DataStream.getCortexToken());
            params.put("virtualHeadsetId", virtualHeadsetId);
            params.put("powerOn", powerOn);

            mCortexConnection.sendRequest(Constant.HEADSET_STREAM, Constant.UPDATE_VIRTUAL_HEADSET_REQUEST_ID, "updateVirtualHeadset", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // delete a virtual headset
    public void deleteVirtualHeadset(String virtualHeadsetId) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", DataStream.getCortexToken());
            params.put("virtualHeadsetId", virtualHeadsetId);

            mCortexConnection.sendRequest(Constant.HEADSET_STREAM, Constant.DELETE_VIRTUAL_HEADSET_REQUEST_ID, "deleteVirtualHeadset", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // trigger an eye event
    public void triggerVirtualHeadsetEvent(String virtualHeadsetId) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", DataStream.getCortexToken());
            params.put("virtualHeadsetId", virtualHeadsetId);
            params.put("eyeAction", "blink");

            mCortexConnection.sendRequest(Constant.HEADSET_STREAM, Constant.TRIGGER_VIRTUAL_HEADSET_EVENT_REQUEST_ID, "triggerVirtualHeadsetEvent", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // open a session, so we can then get data from a headset
    // you need a license to activate the session
    public void createSession(String cortexToken, String status, String headsetID) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("status", status);
            params.put("headset", headsetID);
            mCortexConnection.sendRequest(Constant.SESSION_STREAM, Constant.CREATE_SESSION_REQUEST_ID, "createSession", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // to update session: close or active
    public void updateSession(String cortexToken, String status, String sessionID) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("status", status);
            params.put("session", sessionID);
            mCortexConnection.sendRequest(Constant.SESSION_STREAM, Constant.UPDATE_SESSION_REQUEST_ID, "updateSession", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // to create a record
    public void createRecord(String cortexToken, String sessionID, String title, String description) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("session", sessionID);
            params.put("title", title);
            params.put("description", description);
            mCortexConnection.sendRequest(Constant.RECORD_STREAM, Constant.CREATE_RECORD_REQUEST_ID, "createRecord", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // to stop a recording
    public void stopRecord(String cortexToken, String sessionID) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("session", sessionID);
            mCortexConnection.sendRequest(Constant.RECORD_STREAM, Constant.STOP_RECORD_REQUEST_ID, "stopRecord", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // insert a marker, to mark an event in a session
    // you can use injectMarker alone, to mark an instant event
    // or you can use injectMarker and later injectStopMarker, to mark a period of time
    public void injectMarker(String cortexToken, String sessionID, String label, String value, String port, long time) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("session", sessionID);
            params.put("label", label);
            params.put("value", value);
            params.put("port", port);
            params.put("time", time);
            mCortexConnection.sendRequest(Constant.RECORD_STREAM, Constant.INJECT_MARKER_REQUEST_ID, "injectMarker", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // to subsribe data streams include: eeg, motion (mot), performance metric(met),power band(pow)
    // mentalCommand(com), facial expression(fac), training event(sys), contact quality(dev)
    public void subscribeData(String cortexToken, String sessionID, List<String> streamArray) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("session", sessionID);
            JSONArray arrayStream = new JSONArray();
            for (int i = 0 ; i < streamArray.size(); i++) {
                arrayStream.put(streamArray.get(i));
            }
            params.put("streams", arrayStream);
            mCortexConnection.sendRequest(Constant.SUBSCRIBE_STREAM, Constant.SUBSCRIBE_DATA_REQUEST_ID, "subscribe", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // to unsubscribe data
    public void unSubscribeData(String cortexToken, String sessionID, List<String> streamArray) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("session", sessionID);
            JSONArray arrayStream = new JSONArray();
            for (int i = 0 ; i < streamArray.size(); i++) {
                arrayStream.put(streamArray.get(i));
            }
            params.put("streams", arrayStream);
            mCortexConnection.sendRequest(Constant.SUBSCRIBE_STREAM, Constant.UNSUBSCRIBE_DATA_REQUEST_ID, "unsubscribe", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // to create, load, upload, rename or delete profile
    public void setupProfile(String cortexToken, String profileName, String headsetID, String status, int requestType) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("profile", profileName);
            params.put("headset", headsetID);
            params.put("status", status);
            mCortexConnection.sendRequest(Constant.TRAINING_PROFILE_STREAM, requestType, "setupProfile", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // to get current profile
    public void getCurrentProfile(String cortexToken, String headsetID) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("headset", headsetID);
            mCortexConnection.sendRequest(Constant.TRAINING_PROFILE_STREAM, Constant.GET_CURRENT_PROFILE_REQUEST_ID, "getCurrentProfile", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // training profile management for facial expression and mental command
    public void training(String cortexToken, String detection, String sessionID, String action, String status, int requestType) {
        try {
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("detection", detection);
            params.put("session", sessionID);
            params.put("action", action);
            params.put("status", status);
            mCortexConnection.sendRequest(Constant.TRAINING_PROFILE_STREAM, requestType, "training", params, true);
        } catch (Exception e) { e.printStackTrace(); }
    }
}