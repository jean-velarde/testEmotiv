package com.emotiv.cortexv2example.basicfeatures;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.emotiv.cortexv2example.activity.AutoAuthorizeActivity;
import com.emotiv.cortexv2example.adapter.HeadsetListAdapter;
import com.emotiv.cortexv2example.controller.CortexResponseController;
import com.emotiv.cortexv2example.controller.WarningController;
import com.emotiv.cortexv2example.interfaces.WarningInterface;
import com.emotiv.cortexv2example.objects.HeadsetObject;
import com.emotiv.cortexv2example.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AutoAuthorizeActivity implements View.OnClickListener {

    ProgressBar progressBar;
    ListView headsetListView;
    private HeadsetListAdapter headsetListAdapter;
    Button btnQueryHeadsets, btnDisconnectHeadset;
    String workingHeadsetName = "";

    @Override
    protected void initView() {
        List<Data> data = fill_with_data();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnQueryHeadsets = (Button) findViewById(R.id.btnRefresh);
        btnQueryHeadsets.setOnClickListener(this);
        btnDisconnectHeadset = (Button) findViewById(R.id.btnDisconnect);
        btnDisconnectHeadset.setOnClickListener(this);

        headsetListView = (ListView) findViewById(R.id.headsetListView);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        //Add Header View
        View headerView = inflater.inflate(R.layout.headset_list_header, null, false);
        headsetListView.addHeaderView(headerView);//Add view to list view as header view

        headsetListAdapter = new HeadsetListAdapter(MainActivity.this, new ArrayList<HeadsetObject>());
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_sub_activity);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(data, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        CortexResponseController.getInstance().setCortexAPIInterface(this);
        WarningController.getInstance().setWarningInterface(this);
    }

    public List<Data> fill_with_data() {

        List<Data> data = new ArrayList<>();

        data.add(new Data("Login & Authorize", R.drawable.ic_emotiv_launcher, AuthorizeActivity.class));
        data.add(new Data("Band Power", R.drawable.ic_emotiv_launcher, BandPowerActivity.class));
        data.add(new Data("EEG", R.drawable.ic_emotiv_launcher, EEGActivity.class));
        data.add(new Data("Facial Expression", R.drawable.ic_emotiv_launcher, FacialExpressionActivity.class));
        data.add(new Data("Facial Expression Training", R.drawable.ic_emotiv_launcher, FacialExpressionTrainingActivity.class));
        data.add(new Data("Headset Manager", R.drawable.ic_emotiv_launcher, HeadsetSessionActivity.class));
        data.add(new Data("Mental Command", R.drawable.ic_emotiv_launcher, MentalCommandActivity.class));
        data.add(new Data("Mental Command Training", R.drawable.ic_emotiv_launcher,  MentalCommandTrainingActivity.class));
        data.add(new Data("Motion", R.drawable.ic_emotiv_launcher, MotionActivity.class));
        data.add(new Data("Performance Metrics", R.drawable.ic_emotiv_launcher, PerformanceMetricsActivity.class));
        data.add(new Data("Record Marker", R.drawable.ic_emotiv_launcher, RecordMarkerActivity.class));

        return data;
    }

    @Override
    protected void onAutoAuthorizeDone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoading(false);
                btnQueryHeadsets.setEnabled(true);
                btnDisconnectHeadset.setEnabled(true);
            }
        });
    }

    @Override
    public void onQueryHeadsetOk() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                headsetListAdapter.clear();
                headsetListAdapter.addAll(HeadsetObject.getInstance().getHeadsetList());
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRefresh:
                // call refresh scan before query headset
                // more details https://emotiv.gitbook.io/cortex-api/headset/controldevice#headset-scanning-flow
                mCortexApiRequester.controlDevice("refresh", "");
                mCortexApiRequester.queryHeadset();
                break;
            case R.id.btnDisconnect:
                if (!workingHeadsetName.equals("")) {
                    mCortexApiRequester.controlDevice("disconnect", workingHeadsetName);
                }
                break;
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
        }
    }

    @Override
    public void onError(int errorCode, String errorMessage) {

    }

    @Override
    public void onLogoutOk() {

    }

    @Override
    public void onGetUserInformationOk() {

    }

    @Override
    public void onGetLicenseInfoOk() {

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

}