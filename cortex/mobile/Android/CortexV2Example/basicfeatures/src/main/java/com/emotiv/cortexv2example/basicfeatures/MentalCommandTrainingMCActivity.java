package com.emotiv.cortexv2example.basicfeatures;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.emotiv.cortexv2example.activity.AutoAuthorizeActivity;
import com.emotiv.cortexv2example.controller.CortexResponseController;
import com.emotiv.cortexv2example.controller.SysController;
import com.emotiv.cortexv2example.controller.WarningController;
import com.emotiv.cortexv2example.cortexlib.CortexAPIRequester;
import com.emotiv.cortexv2example.cortexlib.CortexConnection;
import com.emotiv.cortexv2example.cortexlib.CortexLibManager;
import com.emotiv.cortexv2example.interfaces.CortexAPIInterface;
import com.emotiv.cortexv2example.interfaces.SysInterface;
import com.emotiv.cortexv2example.interfaces.WarningInterface;
import com.emotiv.cortexv2example.objects.HeadsetObject;
import com.emotiv.cortexv2example.objects.SessionObject;
import com.emotiv.cortexv2example.utils.Constant;
import java.util.ArrayList;
import java.util.List;

public class MentalCommandTrainingMCActivity extends AutoAuthorizeActivity implements CortexAPIInterface, SysInterface, WarningInterface, View.OnClickListener {

    private final String TAG = MentalCommandTrainingMCActivity.class.getName();
    List<String> streamArray = new ArrayList<String>();
    Button btnSubscribeTrainingMCEvent, btnCreateProfile, btnLoadProfile, btnTrainingMCNeutral, btnTrainingMCPush;

    private String curDetection = "";
    private String curAction = "";

    @Override
    protected void setCustomContentView() {
        setContentView(R.layout.mental_command_training_mc_activity);
    }
    private String getCortexToken() {
        Intent intent = getIntent();
        return intent.hasExtra("cortexToken") ? intent.getStringExtra("cortexToken") : "";
    }

    protected void initView() {
        cortexToken = getCortexToken();
        streamArray.add("sys");
        btnSubscribeTrainingMCEvent = (Button) findViewById(R.id.btnSubscribeTrainingMCEvent);
        btnSubscribeTrainingMCEvent.setOnClickListener(this);
        btnCreateProfile = (Button) findViewById(R.id.btnCreateProfile);
        btnCreateProfile.setOnClickListener(this);
        btnLoadProfile = (Button) findViewById(R.id.btnLoadProfile);
        btnLoadProfile.setOnClickListener(this);
        btnTrainingMCNeutral = (Button) findViewById(R.id.btnTrainingMCNeutral);
        btnTrainingMCNeutral.setOnClickListener(this);
        btnTrainingMCPush = (Button) findViewById(R.id.btnTrainingMCPush);
        btnTrainingMCPush.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();

        CortexResponseController.getInstance().setCortexAPIInterface(this);
        SysController.getInstance().setSysInterface(this);
        WarningController.getInstance().setWarningInterface(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubscribeTrainingMCEvent:
                mCortexApiRequester.subscribeData(cortexToken, SessionObject.getInstance().getCurrentActivedSession(), streamArray);
                break;

            case R.id.btnCreateProfile:
                mCortexApiRequester.setupProfile(cortexToken, Constant.TRAINING_PROFILE_NAME, HeadsetObject.getInstance().getHeadsetName(), "create", Constant.CREATE_PROFILE_REQUEST_ID);
                break;

            case R.id.btnLoadProfile:
                mCortexApiRequester.setupProfile(cortexToken, Constant.TRAINING_PROFILE_NAME, HeadsetObject.getInstance().getHeadsetName(), "load", Constant.LOAD_PROFILE_REQUEST_ID);
                break;

            case R.id.btnTrainingMCNeutral:
                mCortexApiRequester.training(cortexToken, "mentalCommand", SessionObject.getInstance().getCurrentActivedSession(), "neutral", "start", Constant.TRAINING_PROFILE_MC_REQUEST_ID);
                break;

            case R.id.btnTrainingMCPush:
                mCortexApiRequester.training(cortexToken, "mentalCommand", SessionObject.getInstance().getCurrentActivedSession(), "push", "start", Constant.TRAINING_PROFILE_MC_REQUEST_ID);
                break;
        }
    }

    @Override
    protected void onAutoAuthorizeDone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnSubscribeTrainingMCEvent.setEnabled(true);
                btnCreateProfile.setEnabled(true);
                btnLoadProfile.setEnabled(true);
                btnTrainingMCNeutral.setEnabled(true);
                btnTrainingMCPush.setEnabled(true);
            }
        });
    }

    @Override
    public void onError(int errorCode, String errorMessage) {

    }

    @Override
    public void onLoginOk() {

    }

    @Override
    public void onLogoutOk() {

    }

    @Override
    public void onGetUserLoginOk() {

    }

    @Override
    public void onAuthorizeOk() {

    }

    @Override
    public void onAcceptEULAOk() {

    }

    @Override
    public void onGetUserInformationOk() {

    }

    @Override
    public void onGetLicenseInfoOk() {

    }

    @Override
    public void onQueryHeadsetOk() {

    }

    @Override
    public void onControlDeviceOk() {

    }

    @Override
    public void onCreateSessionOk() {

    }

    @Override
    public void onUpdateSessionOk() {

    }

    @Override
    public void onCreateRecordOk() {

    }

    @Override
    public void onStopRecordOk() {

    }

    @Override
    public void onInjectMarkerOk() {

    }

    @Override
    public void onSubscribeDataOk() {

    }

    @Override
    public void onUnSubscribeDataOk() {

    }

    @Override
    public void onCreateProfileOk() {

    }

    @Override
    public void onLoadProfileOk() {

    }

    @Override
    public void onSetupTrainingProfileOk() {

    }

    @Override
    public void onSaveTrainingProfileOk() {

    }

    @Override
    public void onQueryVirtualHeadsetsOk() {

    }

    @Override
    public void onVirtualHeadsetListUpdated() {

    }

    @Override
    public void onTrainingStarted(String detection, String action, String event) {
        curDetection = detection;
        curAction = action;
        Log.i(TAG, "sys event: " + detection + " | " + action + " | " + event);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MentalCommandTrainingMCActivity.this, "Training started... Please wait for result from Cortex", Toast.LENGTH_LONG).show();
                btnSubscribeTrainingMCEvent.setEnabled(false);
                btnCreateProfile.setEnabled(false);
                btnLoadProfile.setEnabled(false);
                btnTrainingMCNeutral.setEnabled(false);
                btnTrainingMCPush.setEnabled(false);
            }
        });
    }

    @Override
    public void onTrainingFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MentalCommandTrainingMCActivity.this, "Training Failed", Toast.LENGTH_LONG).show();
                btnSubscribeTrainingMCEvent.setEnabled(true);
                btnCreateProfile.setEnabled(true);
                btnLoadProfile.setEnabled(true);
                btnTrainingMCNeutral.setEnabled(true);
                btnTrainingMCPush.setEnabled(true);
            }
        });
    }

    @Override
    public void onTrainingSucceeded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MentalCommandTrainingMCActivity.this, "Training Succeeded...", Toast.LENGTH_LONG).show();
                btnSubscribeTrainingMCEvent.setEnabled(true);
                btnCreateProfile.setEnabled(true);
                btnLoadProfile.setEnabled(true);
                btnTrainingMCNeutral.setEnabled(true);
                btnTrainingMCPush.setEnabled(true);
            }
        });
        mCortexApiRequester.training(cortexToken, curDetection, SessionObject.getInstance().getCurrentActivedSession(), curAction, "accept", Constant.ACCEPT_TRAINING_PROFILE_REQUEST_ID);
    }

    @Override
    public void onTrainingCompleted() {
        mCortexApiRequester.setupProfile(cortexToken, Constant.TRAINING_PROFILE_NAME, HeadsetObject.getInstance().getHeadsetName(), "save", Constant.SAVE_TRAINING_PROFILE_REQUEST_ID);
    }

    @Override
    public void onTrainingRejected() {

    }

    @Override
    public void onTrainingReset() {

    }

    @Override
    public void onEegDataRecieved(String eEgData) {

    }

    @Override
    public void onMotDataRecieved(String motData) {

    }

    @Override
    public void onMetDataRecieved(String metData) {

    }

    @Override
    public void onDevDataRecieved(String devData) {

    }

    @Override
    public void onComDataRecieved(String comData) {

    }

    @Override
    public void onFacDataRecieved(String facData) {

    }

    @Override
    public void onNewWarning(int warningCode, String warningMessage) {

    }

    @Override
    public void onAccessTokenRefreshing() {

    }
}