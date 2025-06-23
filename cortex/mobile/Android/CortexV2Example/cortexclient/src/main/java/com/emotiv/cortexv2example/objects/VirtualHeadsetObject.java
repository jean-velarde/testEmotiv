package com.emotiv.cortexv2example.objects;

import java.util.HashMap;

public class VirtualHeadsetObject {
    String mUuid;
    String mOwnerId;
    String mName;
    String mSerial;
    String mType;
    boolean mPowerOn;
    String mConnectionType;

    public String getUuid() { return mUuid; }
    public void setUuid(String uuid) { mUuid = uuid; }

    public String getOwnerId() { return mOwnerId; }
    public void setOwnerId(String ownerId) { mOwnerId = ownerId; }

    public String getName() { return mName; }
    public void setName(String name) { mName = name; }

    public String getSerial() { return mSerial; }
    public void setSerial(String serial) { mSerial = serial; }

    public String getType() { return mType; }
    public void setType(String type) { mType = type; }

    public boolean getPowerOn() { return mPowerOn; }
    public void setPowerOn(boolean powerOn) { mPowerOn = powerOn; }

    public String getConnectionType() { return mConnectionType; }
    public void setConnectionType(String connectionType) { mConnectionType = connectionType; }
}