package com.emotiv.cortexv2example.basicfeatures;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emotiv.cortexv2example.activity.CortexLibActivity;
import com.emotiv.cortexv2example.controller.CortexResponseController;
import com.emotiv.cortexv2example.utils.Constant;

public class AuthorizeActivity extends CortexLibActivity implements View.OnClickListener {
    Button btnGetUserLogin, btnAuthorize, btnAcceptEULA, btnGetUserInformation, btnGetLicenseInfo, btnLogin, btnLogout;
    TextView tvEmotivID;
    TextView tvLicenseTermUrl;
    TextView tvLicenseTermDescription;
    ProgressBar progressBar;
    private String currentUser = "";

    protected void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnGetUserLogin = (Button) findViewById(R.id.btnGetUserLogin);
        btnGetUserLogin.setOnClickListener(this);
        btnAuthorize = (Button) findViewById(R.id.btnAuthorize);
        btnAuthorize.setOnClickListener(this);
        btnAcceptEULA = (Button) findViewById(R.id.btnAcceptEULA);
        btnAcceptEULA.setOnClickListener(this);
        btnGetUserInformation = (Button) findViewById(R.id.btnGetUserInformation);
        btnGetUserInformation.setOnClickListener(this);
        btnGetLicenseInfo = (Button) findViewById(R.id.btnGetLicenseInfo);
        btnGetLicenseInfo.setOnClickListener(this);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnLogout = (Button)findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        tvEmotivID = (TextView) findViewById(R.id.tvEmotivID);
        tvLicenseTermUrl = (TextView) findViewById(R.id.tvLicenseTermUrl);
        tvLicenseTermDescription = (TextView) findViewById(R.id.tvLicenseTermDescription);
    }

    @Override
    protected void setCustomContentView() {
        setContentView(R.layout.activity_authorize);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetUserLogin:
                mCortexApiRequester.getUserLogin();
                break;
            case R.id.btnAuthorize:
                mCortexApiRequester.authorize();
                break;
            case R.id.btnAcceptEULA:
                mCortexApiRequester.acceptEULA();
                break;
            case R.id.btnGetUserInformation:
                mCortexApiRequester.geUserInformation();
                break;
            case R.id.btnGetLicenseInfo:
                mCortexApiRequester.getLicenseInformation();
                break;
            case R.id.btnLogin:
                mCortexApiRequester.authenticate(this, Constant.CLIENT_ID, Constant.AUTHEN_HANDLE_CODE);
                break;
            case R.id.btnLogout:
                mCortexApiRequester.logout(currentUser);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CortexResponseController.getInstance().setCortexAPIInterface(this);
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

    @Override
    public void onError(int errorCode, String errorMessage) {

    }

    @Override
    public void onLoginOk() {
        currentUser = mCortexResponseController.getDataStream().getAccessObject().getEmotivID();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onEmotivUser(true);
            }
        });
    }

    @Override
    public void onLogoutOk() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onEmotivUser(false);
            }
        });
    }

    private void onEmotivUser(boolean hasUser) {
        btnLogin.setEnabled(!hasUser);

        btnAuthorize.setEnabled(hasUser);
        btnGetUserInformation.setEnabled(hasUser);
        btnGetLicenseInfo.setEnabled(hasUser);
        btnLogout.setEnabled(hasUser);
    }

    protected void onCortexLibStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                btnGetUserLogin.setEnabled(true);
                btnAuthorize.setEnabled(false);
                btnGetUserInformation.setEnabled(false);
                btnGetLicenseInfo.setEnabled(false);
                btnLogin.setEnabled(false);
                btnLogout.setEnabled(false);
            }
        });
    }

    @Override
    public void onGetUserLoginOk() {
        currentUser = mCortexResponseController.getDataStream().getAccessObject().getEmotivID();
        if (currentUser.isEmpty()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onEmotivUser(false);
                }
            });
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onEmotivUser(true);
                }
            });
        }
    }

    @Override
    public void onAuthorizeOk() {

        // get acceptance EULA and license term URL
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean acceptanceEULA = CortexResponseController.getInstance().getDataStream().getAccessObject().isAcceptanceEULA();
                String licenseTermUrl = CortexResponseController.getInstance().getDataStream().getAccessObject().getLicenseTermUrl();
                if (!acceptanceEULA) {
                    btnAcceptEULA.setEnabled(true);
                    tvLicenseTermDescription.setText("Review the EMOTIV EULA using the link below. To develop an application that works with Emotiv Cortex, click the 'acceptEULA' button above once it's enabled.");
                    tvLicenseTermUrl.setText(licenseTermUrl);
                } else {
                    btnAcceptEULA.setEnabled(false);
                    tvLicenseTermDescription.setText("EMOTIV EULA accepted.");
                    tvLicenseTermUrl.setText("");
                }
            }
        });

    }

    @Override
    public void onGetUserInformationOk() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvEmotivID.setText("EmotivID signed in: " + CortexResponseController.getInstance().getDataStream().getAccessObject().getEmotivID());
            }
        });
    }

    @Override
    public void onAcceptEULAOk() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnAcceptEULA.setEnabled(false);
                tvLicenseTermDescription.setText("EMOTIV EULA accepted.");
                tvLicenseTermUrl.setText("");
            }
        });
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
    public void onAccessTokenRefreshing() {

    }
}