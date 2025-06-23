package com.emotiv.cortexv2example.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.emotiv.CortexLibInterface;
import com.emotiv.cortexv2example.R;
import com.emotiv.cortexv2example.controller.CortexResponseController;
import com.emotiv.cortexv2example.cortexlib.CortexAPIRequester;
import com.emotiv.cortexv2example.cortexlib.CortexConnection;
import com.emotiv.cortexv2example.cortexlib.CortexLibManager;
import com.emotiv.cortexv2example.interfaces.CortexAPIInterface;

import java.util.ArrayList;

/**
 * This class is the base class for MainActivity when they want to work with EmotivCortexLib.aar
 * In this activity, we will request some permissions needed by CortexLib and start/stop CortexLib.
 */
public abstract class CortexLibActivity extends AppCompatActivity implements CortexAPIInterface, CortexLibInterface {
    String TAG = "CortexLibActivity";
    private static final int REQUEST_ID_PERMISSIONS = 1000;

    protected CortexConnection mCortexConnection = null;
    protected CortexAPIRequester mCortexApiRequester = null;
    protected CortexResponseController mCortexResponseController = null;
    private static boolean mCortexStarted = false;

    protected abstract void initView();
    protected void setCustomContentView() {
        setContentView(R.layout.activity_main);
    }
    protected abstract void onCortexLibStart();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomContentView();
        initView();

        requestPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CortexLibManager.closeConnection(mCortexConnection);
        mCortexConnection = null;
    }

    private void requestPermissions() {
        ArrayList<String> permissions = neededPermissions();

        if (!permissions.isEmpty()) {
            Log.i(TAG, "request permissions:" + permissions.toString());
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), REQUEST_ID_PERMISSIONS);
        } else {
            Log.i(TAG, "permissions are all granted");
            start();
        }
    }

    // return permissions which are needed by CortexLib but not granted yet
    private ArrayList<String> neededPermissions() {
        ArrayList<String> permissionNeeded = new ArrayList<String>();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /***Android 6.0 and higher need to request permission*****/
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        // permission to read/write on external storage where we store Cortex database/logs/crashLogs
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            /***Android 12.0 and higher need to request new bluetooth permission*****/
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }

        return permissionNeeded;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Log.w("EmotivCortexLibExample", "Users cannot process with Emotiv headsets if " + Manifest.permission.ACCESS_FINE_LOCATION +
                        " is not granted");
                return;
            }
            if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Log.w("EmotivCortexLibExample", "Users cannot process with Emotiv headsets if " + Manifest.permission.WRITE_EXTERNAL_STORAGE +
                        " is not granted");
                return;
            }
            if (permissions[i].equals(Manifest.permission.BLUETOOTH_SCAN)
                    && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Log.w("EmotivCortexLibExample", "Users cannot process with Emotiv headsets if " + Manifest.permission.BLUETOOTH_SCAN +
                        " is not granted");
                return;
            }
            if (permissions[i].equals(Manifest.permission.BLUETOOTH_CONNECT)
                    && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Log.w("EmotivCortexLibExample", "Users cannot process with Emotiv headsets if " + Manifest.permission.BLUETOOTH_CONNECT +
                        " is not granted");
                return;
            }
        }

        start();
    }

    private void start() {
        if (mCortexStarted) {
            onCortexStarted();
        }
        else {
            CortexLibManager.start(this);
        }
    }

    @Override
    public void onQueryVirtualHeadsetsOk() {

    }

    @Override
    public void onVirtualHeadsetListUpdated() {

    }

    @Override
    public void onCortexStarted() {
        mCortexStarted = true;
        mCortexResponseController = CortexResponseController.getInstance();
        mCortexResponseController.setCortexAPIInterface(this);
        mCortexConnection = CortexLibManager.createConnection(mCortexResponseController);
        mCortexApiRequester = new CortexAPIRequester(mCortexConnection);

        onCortexLibStart();
    }
}
