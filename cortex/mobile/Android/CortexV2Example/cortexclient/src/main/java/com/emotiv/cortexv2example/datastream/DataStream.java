package com.emotiv.cortexv2example.datastream;

import com.emotiv.cortexv2example.objects.AccessObject;
import com.emotiv.cortexv2example.objects.HeadsetObject;
import com.emotiv.cortexv2example.objects.SessionObject;
import com.emotiv.cortexv2example.objects.TrainingObject;
import com.emotiv.cortexv2example.objects.VirtualHeadsetObject;
import com.emotiv.cortexv2example.utils.Constant;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DataStream extends BaseStream {

    public static DataStream dataStream;
    AccessObject mAccessObject;
    HeadsetObject mHeadsetObject;
    SessionObject mSessionObject;
    TrainingObject mTrainingObject;
    List<VirtualHeadsetObject> mVirtualHeadsetList;

    public DataStream() {
        mAccessObject = AccessObject.getInstance();
        mHeadsetObject = HeadsetObject.getInstance();
        mSessionObject = SessionObject.getInstance();
        mTrainingObject = TrainingObject.getInstance();
        mVirtualHeadsetList = new ArrayList<VirtualHeadsetObject>();
    }

    public static DataStream getInstance() {
        if (dataStream == null) {
            dataStream = new DataStream();
        }
        return dataStream;
    }

    public static String getCortexToken() {
        return getInstance().getAccessObject().getCortexAccessToken();
    }


    public AccessObject getAccessObject() {
        return mAccessObject;
    }

    public HeadsetObject getHeadsetObject() {
        return mHeadsetObject;
    }

    public TrainingObject getmTrainingObject() {
        return mTrainingObject;
    }

    public List<VirtualHeadsetObject> getVirtualHeadsetList() {
        return mVirtualHeadsetList;
    }

    // handle the response to a RPC request
    public void parseStreamData(String jsonString, int requestType) {
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            if (jsonObj.has("result") && !jsonObj.isNull("result")) {
                if (requestType == Constant.GET_USER_LOGGED_IN_REQUEST_ID) {
                    JSONArray userNameArray = jsonObj.getJSONArray("result");
                    if (userNameArray.length() > 0) {
                        JSONObject userObj = userNameArray.getJSONObject(0);
                        mAccessObject.setEmotivID(userObj.getString("username"));
                        mAccessObject.setLogin(true);
                    }
                }
                else if(requestType == Constant.LOGIN_REQUEST_ID)
                {
                    JSONObject loginJson = jsonObj.getJSONObject("result");
                    String userObj = loginJson.getString("username");
                    mAccessObject.setEmotivID(userObj);
                    mAccessObject.setLogin(true);
                }
                else if (requestType == Constant.LOGOUT_REQUEST_ID) {
                    mAccessObject.reset();
                }
                else if (requestType == Constant.AUTHORIZE_REQUEST_ID) {
                    JSONObject authorizeJson = jsonObj.getJSONObject("result");
                    if (authorizeJson.has("warning")) {
                        JSONObject warningObj = authorizeJson.getJSONObject("warning");
                        int code = warningObj.optInt("code", -1);
                        if (code == 6) {
                            String licenseUrl = warningObj.optString("licenseUrl", "");
                            mAccessObject.setAcceptanceEULA(false, licenseUrl);
                        }
                    }

                    mAccessObject.setCortexAccessToken(authorizeJson.getString("cortexToken"));
                }
                else if (requestType == Constant.ACCEPT_EULA_REQUEST_ID) {
                    mAccessObject.setAcceptanceEULA(true, "");
                }
                else if (requestType == Constant.GET_USER_INFORMATION_REQUEST_ID) {
                    JSONObject userInfoJson = jsonObj.getJSONObject("result");
                    mAccessObject.setFirstName(userInfoJson.getString("firstName"));
                    mAccessObject.setLastName(userInfoJson.getString("lastName"));
                    mAccessObject.setEmotivID(userInfoJson.getString("username"));
                }
                else if (requestType == Constant.QUERY_HEADSET_REQUEST_ID) {
                    JSONArray headsetListJsonArray = jsonObj.getJSONArray("result");
                    mHeadsetObject.clearData();
                    if (headsetListJsonArray.length() > 0) {
                        for (int i = 0; i < headsetListJsonArray.length(); i++) {
                            String headsetName = headsetListJsonArray.getJSONObject(i).getString("id");
                            String headsetConnectedBy = headsetListJsonArray.getJSONObject(i).getString("connectedBy");
                            String headsetStatus = headsetListJsonArray.getJSONObject(i).getString("status");
                            boolean isVirtual = headsetListJsonArray.getJSONObject(i).getBoolean("isVirtual");
                            String virtualHeadsetId = headsetListJsonArray.getJSONObject(i).getString("virtualHeadsetId");

                            if (headsetStatus.equals("connected")) {
                                mHeadsetObject.setHeadsetStatus("connected");
                                mHeadsetObject.setHeadsetName(headsetName);
                                mHeadsetObject.setHeadsetConnectedBy(headsetConnectedBy);
                                mHeadsetObject.setVirtualHeadset(isVirtual);
                                mHeadsetObject.setVirtualHeadsetId(virtualHeadsetId);
                                mHeadsetObject.setConnected(true);
                            }

                            HeadsetObject headsetObject = new HeadsetObject();
                            headsetObject.setHeadsetName(headsetName);
                            headsetObject.setHeadsetConnectedBy(headsetConnectedBy);
                            headsetObject.setHeadsetStatus(headsetStatus);
                            headsetObject.setVirtualHeadset(isVirtual);
                            headsetObject.setVirtualHeadsetId(virtualHeadsetId);
                            if (headsetStatus.equals("connected")) {
                                headsetObject.setConnected(true);
                            }

                            mHeadsetObject.getHeadsetList().add(headsetObject);
                        }
                    }
                }
                else if (requestType == Constant.CREATE_SESSION_REQUEST_ID) {
                    JSONObject sessionObj = jsonObj.getJSONObject("result");
                    if (sessionObj.has("id"))
                        mSessionObject.setCurrentActivedSession(sessionObj.getString("id"));
                    if (sessionObj.has("status"))
                        mSessionObject.setCurrentSessonStatus(sessionObj.getString("status"));
                }
                else if (requestType == Constant.UPDATE_SESSION_REQUEST_ID) {
                    mSessionObject.clearData();
                }
                else if (requestType == Constant.CREATE_PROFILE_REQUEST_ID) {

                }
                else if (requestType == Constant.LOAD_PROFILE_REQUEST_ID) {
                    JSONObject trainingProfileObj = jsonObj.getJSONObject("result");
                    if (trainingProfileObj.has("name")) {
                        mTrainingObject.setCurrentProfile(trainingProfileObj.getString("name"));
                    }
                }
                else if (requestType == Constant.TRAINING_PROFILE_MC_REQUEST_ID) {
                    JSONObject trainingMCObj = jsonObj.getJSONObject("result");
                    mTrainingObject.setTrainingAction(trainingMCObj.getString("action"));
                }
                else if (requestType == Constant.TRAINING_PROFILE_FE_REQUEST_ID) {
                    JSONObject trainingMCObj = jsonObj.getJSONObject("result");
                    mTrainingObject.setTrainingAction(trainingMCObj.getString("action"));
                }
                else if (requestType == Constant.QUERY_VIRTUAL_HEADSETS_REQUEST_ID) {
                    JSONArray headsetListJsonArray = jsonObj.getJSONArray("result");
                    mVirtualHeadsetList.clear();
                    if (headsetListJsonArray.length() > 0) {
                        for (int i = 0; i < headsetListJsonArray.length(); i++) {
                            JSONObject jo = headsetListJsonArray.getJSONObject(i);
                            VirtualHeadsetObject vhs = new VirtualHeadsetObject();
                            vhs.setUuid(jo.getString("uuid"));
                            vhs.setName(jo.getString("name"));
                            vhs.setType(jo.getString("type"));
                            vhs.setConnectionType(jo.getString("connectionType"));
                            vhs.setOwnerId(jo.getString("ownerId"));
                            vhs.setSerial(jo.getString("serial"));
                            vhs.setPowerOn(jo.getBoolean("powerOn"));

                            mVirtualHeadsetList.add(vhs);
                        }
                    }
                }
            }

        } catch (Exception e) { e.printStackTrace(); }
    }
}