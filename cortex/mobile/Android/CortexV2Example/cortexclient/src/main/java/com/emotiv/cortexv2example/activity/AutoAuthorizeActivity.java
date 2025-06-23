package com.emotiv.cortexv2example.activity;

import android.content.Intent;
import android.util.Log;

import com.emotiv.cortexv2example.controller.CortexResponseController;
import com.emotiv.cortexv2example.interfaces.WarningInterface;
import com.emotiv.cortexv2example.utils.Constant;

/**
 * This class is the base class for MainActivity in some apps to get data/training.
 * In this activity, we will auto login, authorize which these apps needs before connecting to and
 * getting data from Emotiv headsets.
 */
public abstract class AutoAuthorizeActivity extends CortexLibActivity implements WarningInterface {
    protected String cortexToken = "";

    protected abstract void onAutoAuthorizeDone();

    @Override
    protected void onCortexLibStart() {
        mCortexApiRequester.getUserLogin();
    }

    @Override
    public void onGetUserLoginOk() {
        String emotivId = mCortexResponseController.getDataStream().getAccessObject().getEmotivID();
        if (emotivId.isEmpty()) {
            mCortexApiRequester.authenticate(this, Constant.CLIENT_ID, Constant.AUTHEN_HANDLE_CODE);
        }
        else {
            onLoginOk();
        }
    }

    @Override
    public void onLoginOk() {
        mCortexApiRequester.authorize();
    }

    @Override
    public void onAcceptEULAOk() {

    }

    @Override
    public void onNewWarning(final int warningCode, final String warningMessage) {
    }

    @Override
    public void onAuthorizeOk() {
        cortexToken = CortexResponseController.getInstance().getDataStream().getAccessObject().getCortexAccessToken();
        onAutoAuthorizeDone();
    }

    @Override
    public void onAccessTokenRefreshing() {
        mCortexApiRequester.authorize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == Constant.AUTHEN_HANDLE_CODE)
        {
            String code = mCortexApiRequester.getAuthenticationCode(requestCode, intent);
            Log.e("authenticate", "Authentication code:"+ code);
            if(!code.isEmpty())
                mCortexApiRequester.loginWithAuthenticationCode(code);
        }
    }
}
