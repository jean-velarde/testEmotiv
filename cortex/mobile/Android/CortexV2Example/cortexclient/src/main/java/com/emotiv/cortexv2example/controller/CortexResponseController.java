package com.emotiv.cortexv2example.controller;

import android.util.Log;

import com.emotiv.cortexv2example.datastream.DataStream;
import com.emotiv.cortexv2example.interfaces.CortexAPIInterface;
import com.emotiv.cortexv2example.utils.Constant;
import com.emotiv.cortexv2example.interfaces.CortexConnectionInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class CortexResponseController implements CortexConnectionInterface {
    private static CortexResponseController mCortexResponseController;
    private final String TAG = CortexResponseController.class.getName();
    private CortexAPIInterface mCortexAPIInterface;
    DataStream dataStream;

    public CortexResponseController() {
        dataStream = DataStream.getInstance();
    }

    public static CortexResponseController getInstance() {
        if (mCortexResponseController == null) {
            mCortexResponseController = new CortexResponseController();
        }
        return mCortexResponseController;
    }

    public void setCortexAPIInterface(CortexAPIInterface cortexAPIInterface) {
        this.mCortexAPIInterface = cortexAPIInterface;
    }

    public DataStream getDataStream() {
        return dataStream;
    }

    // handle message received from Cortex
    private void handleResponse(String msg, int streamId) {
        try {
            JSONObject jsonObj = new JSONObject(msg);
            int requestID = jsonObj.getInt("id");
            if (dataStream.parseStreamData(msg)) {
                dataStream.parseStreamData(msg, requestID);

                if (streamId == Constant.ACCESS_STREAM) {
                    switch (requestID) {
                        case Constant.GET_USER_LOGGED_IN_REQUEST_ID:
                            mCortexAPIInterface.onGetUserLoginOk();
                            break;
                        case Constant.AUTHORIZE_REQUEST_ID:
                            mCortexAPIInterface.onAuthorizeOk();
                            break;
                        case Constant.GET_USER_INFORMATION_REQUEST_ID:
                            mCortexAPIInterface.onGetUserInformationOk();
                            break;
                        case Constant.GET_LICENSE_INFORMATION_REQUEST_ID:
                            mCortexAPIInterface.onGetLicenseInfoOk();
                            break;
                        case Constant.LOGIN_REQUEST_ID:
                            mCortexAPIInterface.onLoginOk();
                            break;
                        case Constant.LOGOUT_REQUEST_ID:
                            mCortexAPIInterface.onLogoutOk();
                            break;
                        case Constant.ACCEPT_EULA_REQUEST_ID:
                             mCortexAPIInterface.onAcceptEULAOk();
                            break;
                    }
                } else if (streamId == Constant.HEADSET_STREAM) {
                    switch (requestID) {
                        case Constant.QUERY_HEADSET_REQUEST_ID:
                            mCortexAPIInterface.onQueryHeadsetOk();
                            break;
                        case Constant.CONTROL_DEVICE_REQUEST_ID:
                            mCortexAPIInterface.onControlDeviceOk();
                            break;
                        case Constant.QUERY_VIRTUAL_HEADSETS_REQUEST_ID:
                            mCortexAPIInterface.onQueryVirtualHeadsetsOk();
                            break;
                        case Constant.CREATE_VIRTUAL_HEADSET_REQUEST_ID:
                        case Constant.UPDATE_VIRTUAL_HEADSET_REQUEST_ID:
                        case Constant.DELETE_VIRTUAL_HEADSET_REQUEST_ID:
                            mCortexAPIInterface.onVirtualHeadsetListUpdated();
                            break;
                    }
                } else if (streamId == Constant.SESSION_STREAM) {
                    switch (requestID) {
                        case Constant.CREATE_SESSION_REQUEST_ID:
                            mCortexAPIInterface.onCreateSessionOk();
                            break;
                        case Constant.UPDATE_SESSION_REQUEST_ID:
                            mCortexAPIInterface.onUpdateSessionOk();
                            break;
                    }
                } else if (streamId == Constant.SUBSCRIBE_STREAM) {
                    switch (requestID) {
                        case Constant.SUBSCRIBE_DATA_REQUEST_ID:
                            mCortexAPIInterface.onSubscribeDataOk();
                            break;
                        case Constant.UNSUBSCRIBE_DATA_REQUEST_ID:
                            mCortexAPIInterface.onUnSubscribeDataOk();
                            break;
                    }
                } else if (streamId == Constant.TRAINING_PROFILE_STREAM) {
                    switch (requestID) {
                        case Constant.CREATE_PROFILE_REQUEST_ID:
                            mCortexAPIInterface.onCreateProfileOk();
                            break;
                        case Constant.LOAD_PROFILE_REQUEST_ID:
                            mCortexAPIInterface.onLoadProfileOk();
                            break;
                        case Constant.TRAINING_PROFILE_MC_REQUEST_ID:
                        case Constant.TRAINING_PROFILE_FE_REQUEST_ID:
                            mCortexAPIInterface.onSetupTrainingProfileOk();
                            break;
                        case Constant.SAVE_TRAINING_PROFILE_REQUEST_ID:
                            mCortexAPIInterface.onSaveTrainingProfileOk();
                            break;
                    }
                } else if (streamId == Constant.RECORD_STREAM) {
                    switch (requestID) {
                        case Constant.CREATE_RECORD_REQUEST_ID:
                            mCortexAPIInterface.onCreateRecordOk();
                            break;
                        case Constant.STOP_RECORD_REQUEST_ID:
                            mCortexAPIInterface.onStopRecordOk();
                            break;
                        case Constant.INJECT_MARKER_REQUEST_ID:
                            mCortexAPIInterface.onInjectMarkerOk();
                            break;
                    }
                }

            } else {
                if (mCortexAPIInterface != null) {
                    Log.e(TAG, "Error code: " + dataStream.getErrorCode() + " Error msg: " + dataStream.getErrorString());
                    mCortexAPIInterface.onError(dataStream.getErrorCode(), dataStream.getErrorString());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // handle warning message received from Cortex
    private void handleWarning(String msg) {
        WarningController warningController = WarningController.getInstance();
        warningController.parseWarningMessage(msg);
    }

    // handle training/data message recieved from Cortex
    private void handleData(String msg) {
        SysController sysController = SysController.getInstance();
        sysController.parseSysMessage(msg);
    }

    @Override
    public void onReceivedMessage(String msg, int streamId) {
        Log.i(TAG, "new message: " + msg);
        handleResponse(msg, streamId);
    }

    @Override
    public void onReceivedWarningMessage(String msg) {
        Log.i(TAG, "new warning: " + msg);
        handleWarning(msg);
    }

    @Override
    public void onReceivedNewData(String msg) {
        Log.i(TAG, "new data: " + msg);
        handleData(msg);
    }

    @Override
    public void onError(JSONObject error) {
        try {
            int errorCode = error.getInt("code");
            Log.i(TAG, "onError: code: " + errorCode + " message: " + error.getString("message"));
            // See error code list on Cortex API documentation
            if (errorCode == -32130) { // The cloud token is refreshing
                mCortexAPIInterface.onAccessTokenRefreshing();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}