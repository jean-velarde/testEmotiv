package com.emotiv.cortexv2example.basicfeatures;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.emotiv.cortexv2example.adapter.VirtualHeadsetListAdapter;
import com.emotiv.cortexv2example.controller.CortexResponseController;
import com.emotiv.cortexv2example.cortexlib.CortexAPIRequester;
import com.emotiv.cortexv2example.cortexlib.CortexConnection;
import com.emotiv.cortexv2example.cortexlib.CortexLibManager;
import com.emotiv.cortexv2example.datastream.DataStream;
import com.emotiv.cortexv2example.interfaces.CortexAPIInterface;
import com.emotiv.cortexv2example.interfaces.VirtualHeadsetInterface;
import com.emotiv.cortexv2example.objects.VirtualHeadsetObject;

import java.util.ArrayList;

public class HeadsetSessionVirtualHeadset extends AppCompatActivity implements CortexAPIInterface, View.OnClickListener, VirtualHeadsetInterface {
    String cortexToken = "";

    ListView virtualHeadsetListView;
    VirtualHeadsetListAdapter virtualHeadsetListAdapter;
    Button btnQueryVirtualHeadsets, btnCreateVirtualHeadset;

    CortexConnection mCortexConnection = null;
    CortexAPIRequester mCortexApiRequester = null;
    CortexResponseController mCortexResponseController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headset_session_virtual_headset);

        mCortexResponseController = CortexResponseController.getInstance();
        mCortexResponseController.setCortexAPIInterface(this);
        mCortexConnection = CortexLibManager.createConnection(mCortexResponseController);
        mCortexApiRequester = new CortexAPIRequester(mCortexConnection);

        initView();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        cortexToken = intent.getStringExtra("cortexToken");
    }

    @Override
    protected void onResume() {
        super.onResume();
        CortexResponseController.getInstance().setCortexAPIInterface(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CortexLibManager.closeConnection(mCortexConnection);
    }

    private void initView() {
        virtualHeadsetListView = (ListView) findViewById(R.id.virtualHeadsetListView);
        virtualHeadsetListAdapter = new VirtualHeadsetListAdapter(HeadsetSessionVirtualHeadset.this, new ArrayList<VirtualHeadsetObject>());
        virtualHeadsetListAdapter.setVirtualHeadsetInterface(this);
        virtualHeadsetListView.setAdapter(virtualHeadsetListAdapter);

        btnQueryVirtualHeadsets = (Button) findViewById(R.id.btnQueryVirtualHeadsets);
        btnQueryVirtualHeadsets.setOnClickListener(this);
        btnCreateVirtualHeadset = (Button) findViewById(R.id.btnCreateVirtualHeadset);
        btnCreateVirtualHeadset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnQueryVirtualHeadsets:
                mCortexApiRequester.queryVirtualHeadsets();
                break;
            case R.id.btnCreateVirtualHeadset:
                mCortexApiRequester.createVirtualHeadset();
                break;
        }
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
    public void onSubscribeDataOk() {

    }

    @Override
    public void onUnSubscribeDataOk() {

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                virtualHeadsetListAdapter.clear();
                virtualHeadsetListAdapter.addAll(DataStream.getInstance().getVirtualHeadsetList());
                virtualHeadsetListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onVirtualHeadsetListUpdated() {
        mCortexApiRequester.queryVirtualHeadsets();
    }

    @Override
    public void onAccessTokenRefreshing() {

    }

    @Override
    public void onVirtualHeadserPowerChanged(String virtualHeadsetId, boolean powerOn) {
        mCortexApiRequester.powerVirtualHeadset(virtualHeadsetId, powerOn);
    }

    @Override
    public void onVirtualHeadserDeleted(String virtualHeadsetId) {
        mCortexApiRequester.deleteVirtualHeadset(virtualHeadsetId);
    }
}