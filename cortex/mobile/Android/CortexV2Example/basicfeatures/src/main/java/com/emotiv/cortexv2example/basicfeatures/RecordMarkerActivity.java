package com.emotiv.cortexv2example.basicfeatures;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.emotiv.cortexv2example.activity.AutoAuthorizeActivity;
import com.emotiv.cortexv2example.controller.CortexResponseController;
import com.emotiv.cortexv2example.controller.WarningController;
import com.emotiv.cortexv2example.objects.HeadsetObject;
import com.emotiv.cortexv2example.objects.SessionObject;
import com.emotiv.cortexv2example.utils.Constant;

public class RecordMarkerActivity extends AutoAuthorizeActivity implements View.OnClickListener {
    Button btnCreateSession, btnCreateRecord, btnInjectMarker, btnStopRecord;

    @Override
    protected void initView() {
        btnCreateSession = (Button) findViewById(R.id.btnCreateSession);
        btnCreateSession.setOnClickListener(this);

        btnCreateRecord = (Button) findViewById(R.id.btnCreateRecord);
        btnCreateRecord.setOnClickListener(this);
        btnInjectMarker = (Button) findViewById(R.id.btnInjectMarker);
        btnInjectMarker.setOnClickListener(this);
        btnStopRecord = (Button) findViewById(R.id.btnStopRecord);
        btnStopRecord.setOnClickListener(this);
    }

    @Override
    protected void setCustomContentView() {
        setContentView(R.layout.activity_record_marker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CortexResponseController.getInstance().setCortexAPIInterface(this);
        WarningController.getInstance().setWarningInterface(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateSession:
                mCortexApiRequester.createSession(cortexToken, "active", HeadsetObject.getInstance().getHeadsetName());
                break;
            case R.id.btnCreateRecord:
                mCortexApiRequester.createRecord(cortexToken, SessionObject.getInstance().getCurrentActivedSession(), Constant.RECORD_NAME, "example for recording");
                break;
            case R.id.btnInjectMarker:
                mCortexApiRequester.injectMarker(cortexToken, SessionObject.getInstance().getCurrentActivedSession(), "v2example", "v2", "USB", System.currentTimeMillis());
                break;
            case R.id.btnStopRecord:
                mCortexApiRequester.stopRecord(cortexToken, SessionObject.getInstance().getCurrentActivedSession());
                break;
        }
    }

    @Override
    public void onError(int errorCode, String errorMessage) {

    }

    @Override
    public void onLogoutOk() {

    }

    @Override
    protected void onAutoAuthorizeDone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnCreateSession.setEnabled(true);
                btnCreateRecord.setEnabled(true);
                btnInjectMarker.setEnabled(true);
                btnStopRecord.setEnabled(true);
            }
        });
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
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mCortexApiRequester.queryHeadset();
            }
        }, 2000);
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
    public void onNewWarning(final int warningCode, final String warningMessage) {
        super.onNewWarning(warningCode, warningMessage);
        if (warningCode == Constant.HEADSET_IS_CONNECTED) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCortexApiRequester.queryHeadset();
                }
            }, 2000);

        } else if (warningCode == Constant.HEADSET_CONNECTION_TIME_OUT) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RecordMarkerActivity.this, warningMessage + " Please try again!", Toast.LENGTH_LONG).show();
                    mCortexApiRequester.queryHeadset();
                }
            });
        }
    }
}