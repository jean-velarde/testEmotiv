package com.emotiv.cortexv2example.utils;

import android.Manifest;

public class Constant {

    /*
     * To get a client id and a client secret, you must connect to your Emotiv
     * account on emotiv.com and create a Cortex app.
     * https://www.emotiv.com/my-account/cortex-apps/
     */
    /*
    public final static String CLIENT_ID = "Kv8tEDU1GwpK6Kc2ouKwiqWTaZIKlb1w8jokZPbU";
    public final static String CLIENT_SECRET = "MmkmJx7OkPCIIhC9awlbnV6DzkV40pa03MhvvMOMnL85Uo22vaxZrLNrnFfSbHTtP0fpHVnUx1WGbtEP7bCDFEIFW3rYLIe08jdNntaiIzG8MXuNtmihCknnUGpSyd7z";
     */
    public final static String CLIENT_ID = "3n2Ci214jr2Onh4gBUDYWChh5Yt8UZJfqGs3u2Zn";
    public final static String CLIENT_SECRET = "pDIg3p7joIocZaAwkvoxRHHBIcJvNkyrrKLqhPOikx1VkYSiWuc8DE7VQd200Xt7l24by7f8g782cArNnAfWdoepik1IjePCHRFPEsElRM8G1UrjxMiv6nOTtbDdtccE";

    public final static int DEBIT_NUMBER = 10;
    // if LICENSE_ID is empty our system will choose a valid license from your emotiv account
    // if want to use a specific license. Please put your license here
    public final static String LICENSE_ID = "";
    // The name of the training profile used for the facial expression and mental command
    public final static String TRAINING_PROFILE_NAME = "cortex-v2-example";

    // The name of a record
    public final static String RECORD_NAME = "record-cortex-v2-example";

    /* Stream IDs */
    public final static int ACCESS_STREAM = 100;
    public final static int HEADSET_STREAM = 200;
    public final static int SESSION_STREAM = 300;
    public final static int RECORD_STREAM = 400;
    public final static int SUBSCRIBE_STREAM = 500;
    public final static int TRAINING_PROFILE_STREAM = 600;

    /* Request IDs */
    public final static int GET_USER_LOGGED_IN_REQUEST_ID = 1;
    public final static int AUTHORIZE_REQUEST_ID = 4;
    public final static int GET_USER_INFORMATION_REQUEST_ID = 5;
    public final static int GET_LICENSE_INFORMATION_REQUEST_ID = 6;
    public final static int QUERY_HEADSET_REQUEST_ID = 7;
    public final static int CONTROL_DEVICE_REQUEST_ID = 8;
    public final static int CREATE_SESSION_REQUEST_ID = 9;
    public final static int UPDATE_SESSION_REQUEST_ID = 10;
    public final static int CREATE_RECORD_REQUEST_ID = 11;
    public final static int STOP_RECORD_REQUEST_ID = 12;
    public final static int INJECT_MARKER_REQUEST_ID = 13;
    public final static int SUBSCRIBE_DATA_REQUEST_ID = 14;
    public final static int UNSUBSCRIBE_DATA_REQUEST_ID = 15;
    public final static int CREATE_PROFILE_REQUEST_ID = 16;
    public final static int LOAD_PROFILE_REQUEST_ID = 17;
    public final static int GET_CURRENT_PROFILE_REQUEST_ID = 18;
    public final static int TRAINING_PROFILE_MC_REQUEST_ID = 19;
    public final static int TRAINING_PROFILE_FE_REQUEST_ID = 20;
    public final static int ACCEPT_TRAINING_PROFILE_REQUEST_ID = 21;
    public final static int SAVE_TRAINING_PROFILE_REQUEST_ID = 22;

    public final static int LOGIN_REQUEST_ID = 23;
    public final static int LOGOUT_REQUEST_ID = 24;

    public final static int QUERY_VIRTUAL_HEADSETS_REQUEST_ID = 25;
    public final static int CREATE_VIRTUAL_HEADSET_REQUEST_ID = 26;
    public final static int UPDATE_VIRTUAL_HEADSET_REQUEST_ID = 27;
    public final static int DELETE_VIRTUAL_HEADSET_REQUEST_ID = 28;
    public final static int TRIGGER_VIRTUAL_HEADSET_EVENT_REQUEST_ID = 29;
    public final static int ACCEPT_EULA_REQUEST_ID = 30;

    /* Warning code */
    public final static int HEADSET_IS_CONNECTED = 104;
    public final static int HEADSET_CONNECTION_TIME_OUT = 102;
    public final static int AUTHEN_HANDLE_CODE = 100;
}
