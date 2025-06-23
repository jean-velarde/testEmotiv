package com.emotiv.cortexv2example.basicfeatures;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.emotiv.cortexv2example.activity.AutoAuthorizeActivity;
import com.emotiv.cortexv2example.controller.CortexResponseController;
import com.emotiv.cortexv2example.controller.WarningController;
import com.emotiv.cortexv2example.interfaces.WarningInterface;
import com.emotiv.cortexv2example.objects.HeadsetObject;
import com.emotiv.cortexv2example.objects.SessionObject;
import com.emotiv.cortexv2example.utils.Constant;
import java.util.ArrayList;
import java.util.List;

public class FacialExpressionActivity extends AutoAuthorizeActivity implements WarningInterface, View.OnClickListener {
    Button  btnCreateSession, btnSubscribeFE, btnUnSubscribeFE;
    List<String> streamArray = new ArrayList<String>();

    @Override
    protected void initView() {
        streamArray.add("fac");
        btnCreateSession = (Button) findViewById(R.id.btnCreateSession);
        btnCreateSession.setOnClickListener(this);
        btnSubscribeFE = (Button) findViewById(R.id.btnSubscribeFE);
        btnSubscribeFE.setOnClickListener(this);
        btnUnSubscribeFE = (Button) findViewById(R.id.btnUnSubscribeFE);
        btnUnSubscribeFE.setOnClickListener(this);
    }

    @Override
    protected void setCustomContentView() {
        setContentView(R.layout.activity_facial_expression);
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
            case R.id.btnSubscribeFE:
                mCortexApiRequester.subscribeData(cortexToken, SessionObject.getInstance().getCurrentActivedSession(), streamArray);
                break;
            case R.id.btnUnSubscribeFE:
                mCortexApiRequester.unSubscribeData(cortexToken, SessionObject.getInstance().getCurrentActivedSession(), streamArray);
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
                btnSubscribeFE.setEnabled(true);
                btnUnSubscribeFE.setEnabled(true);
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
                    Toast.makeText(FacialExpressionActivity.this, warningMessage + " Please try again!", Toast.LENGTH_LONG).show();
                    mCortexApiRequester.queryHeadset();
                }
            });
        }
    }
}