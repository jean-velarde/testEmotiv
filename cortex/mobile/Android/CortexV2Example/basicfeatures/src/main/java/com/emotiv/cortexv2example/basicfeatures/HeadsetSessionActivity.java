package com.emotiv.cortexv2example.basicfeatures;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.emotiv.cortexv2example.activity.AutoAuthorizeActivity;
import com.emotiv.cortexv2example.adapter.HeadsetListAdapter;
import com.emotiv.cortexv2example.controller.CortexResponseController;
import com.emotiv.cortexv2example.controller.WarningController;
import com.emotiv.cortexv2example.objects.HeadsetObject;
import com.emotiv.cortexv2example.objects.SessionObject;
import com.emotiv.cortexv2example.utils.Constant;
import java.util.ArrayList;

public class HeadsetSessionActivity extends AutoAuthorizeActivity implements View.OnClickListener {

    ListView headsetListView;
    private HeadsetListAdapter headsetListAdapter;
    Button btnQueryHeadsets, btnDisconnectHeadset, btnCreateSession, btnUpdateSession;
    Button btnManageVirtualHeadsets, btnTriggerVirtualHeadsetEvent;
    ProgressBar progressBar;
    String workingHeadsetName = "";

    @Override
    protected void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        headsetListView = (ListView) findViewById(R.id.headsetListView);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        //Add Header View
        View headerView = inflater.inflate(R.layout.headset_list_header, null, false);
        headsetListView.addHeaderView(headerView);//Add view to list view as header view
        headsetListAdapter = new HeadsetListAdapter(HeadsetSessionActivity.this, new ArrayList<HeadsetObject>());
        headsetListView.setAdapter(headsetListAdapter);
        headsetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView counts the header is first row, but adapter doesn't contain the header
                // so must sub -1 here or app will be crashed.
                int posInAdapter = position - 1;
                if (posInAdapter >= 0) {
                    workingHeadsetName = headsetListAdapter.getItem(posInAdapter).getHeadsetName();
                    String headsetStatus = headsetListAdapter.getItem(posInAdapter).getHeadsetStatus();
                    if (!headsetStatus.equals("connected") && !headsetStatus.equals("connecting")) {
                        showLoading(true);
                        mCortexApiRequester.controlDevice("connect", workingHeadsetName);
                    }
                }
            }
        });
        btnQueryHeadsets = (Button) findViewById(R.id.btnQueryHeadsets);
        btnQueryHeadsets.setOnClickListener(this);
        btnDisconnectHeadset = (Button) findViewById(R.id.btnDisconnectHeadset);
        btnDisconnectHeadset.setOnClickListener(this);
        btnCreateSession = (Button) findViewById(R.id.btnCreateSession);
        btnCreateSession.setOnClickListener(this);
        btnUpdateSession = (Button) findViewById(R.id.btnUpdateSession);
        btnUpdateSession.setOnClickListener(this);
        btnManageVirtualHeadsets = (Button) findViewById(R.id.btnManageVirtualHeadsets);
        btnManageVirtualHeadsets.setOnClickListener(this);
        btnTriggerVirtualHeadsetEvent = (Button) findViewById(R.id.btnTriggerVirtualHeadsetEvent);
        btnTriggerVirtualHeadsetEvent.setOnClickListener(this);
    }

    @Override
    protected void setCustomContentView() {
        setContentView(R.layout.activity_headset_session);
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
            case R.id.btnQueryHeadsets:
                mCortexApiRequester.queryHeadset();
                break;
            case R.id.btnDisconnectHeadset:
                if (!workingHeadsetName.equals("")) {
                    mCortexApiRequester.controlDevice("disconnect", workingHeadsetName);
                }
                break;
            case R.id.btnCreateSession:
                mCortexApiRequester.createSession(cortexToken, "active", HeadsetObject.getInstance().getHeadsetName());
                break;
            case R.id.btnUpdateSession:
                mCortexApiRequester.updateSession(cortexToken, "close", SessionObject.getInstance().getCurrentActivedSession());
                break;
            case R.id.btnManageVirtualHeadsets:
                Intent intent = new Intent(HeadsetSessionActivity.this, HeadsetSessionVirtualHeadset.class);
                intent.putExtra("cortexToken", cortexToken);
                startActivity(intent);
                break;
            case R.id.btnTriggerVirtualHeadsetEvent:
                String virtualHeadsetId = (String) btnTriggerVirtualHeadsetEvent.getTag();
                mCortexApiRequester.triggerVirtualHeadsetEvent(virtualHeadsetId);
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
                showLoading(false);
                btnQueryHeadsets.setEnabled(true);
                btnDisconnectHeadset.setEnabled(true);
                btnCreateSession.setEnabled(true);
                btnUpdateSession.setEnabled(true);
                btnManageVirtualHeadsets.setEnabled(true);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                headsetListAdapter.clear();

                HeadsetObject hs = HeadsetObject.getInstance();
                if (hs.isConnected() && hs.isVirtualHeadset()) {
                    btnTriggerVirtualHeadsetEvent.setEnabled(true);
                    btnTriggerVirtualHeadsetEvent.setTag(hs.getVirtualHeadsetId());
                }
                else {
                    btnTriggerVirtualHeadsetEvent.setEnabled(false);
                    btnTriggerVirtualHeadsetEvent.setTag("");
                }

                headsetListAdapter.addAll(hs.getHeadsetList());
                headsetListAdapter.notifyDataSetChanged();
            }
        });
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
        showLoading(false);
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
                    Toast.makeText(HeadsetSessionActivity.this, warningMessage + " Please try again!", Toast.LENGTH_LONG).show();
                    mCortexApiRequester.queryHeadset();
                }
            });
        }
    }

    private void showLoading(final boolean isShowLoading) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(isShowLoading ? View.VISIBLE : View.GONE);
            }
        });
    }
}