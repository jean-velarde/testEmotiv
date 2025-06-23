package com.emotiv.cortexv2example.interfaces;

public interface VirtualHeadsetInterface {
    void onVirtualHeadserPowerChanged(String virtualHeadsetId, boolean powerOn);
    void onVirtualHeadserDeleted(String virtualHeadsetId);
}
