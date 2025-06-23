package com.emotiv.cortexv2example.interfaces;

public interface CortexAPIInterface {
    // received an error message in response to a RPC request
    void onError(int errorCode, String errorMessage);
    void onLoginOk();
    void onLogoutOk();
    void onGetUserLoginOk();
    void onAuthorizeOk();
    void onAcceptEULAOk();
    void onGetUserInformationOk();
    void onGetLicenseInfoOk();
    void onQueryHeadsetOk();
    void onControlDeviceOk();
    void onCreateSessionOk();
    void onUpdateSessionOk();
    void onSubscribeDataOk();
    void onUnSubscribeDataOk();
    void onCreateRecordOk();
    void onStopRecordOk();
    void onInjectMarkerOk();
    void onCreateProfileOk();
    void onLoadProfileOk();
    void onSetupTrainingProfileOk();
    void onSaveTrainingProfileOk();
    void onQueryVirtualHeadsetsOk();
    void onVirtualHeadsetListUpdated();
    void onAccessTokenRefreshing();
}
