package com.creativeshare.mrsool.tags;

public class Tags {

    public static final String base_url = "http://marsoolk.com";
    public static final String IMAGE_URL = base_url+"/uploads/images/";
    public static final String session_login = "login";
    public static final String session_logout = "logout";

    public static final int MALE = 1;
    public static final int FEMALE = 2;

    public static final String TYPE_CLIENT = "1";
    public static final String TYPE_DELEGATE = "2";

    public static final int APPABOUT = 1;
    public static final int APPTERMS = 3;
    public static final int APPPRIVACY = 4;


    public static final String ORDER_NEW = "0";
    public static final String ORDER_CURRENT = "3";
    public static final String ORDER_OLD = "7";


    public static final String CLIENT_SEND_NEW_ORDER = "0";
    public static final String DELEGATE_ACCEPT_ORDER = "1";
    public static final String DELEGATE_REFUSED_ORDER = "2";
    public static final String CLIENT_ACCEPT_ORDER = "3";
    public static final String CLIENT_REFUSED_ORDER = "4";
    public static final String ORDER_FINISHED = "7";
    public static final String DELEGATE_FINISHED_ORDER = "5";


    public static final int STATE_DELEGATE_NOT_APPROVED_ORDER = 0;
    public static final int STATE_DELEGATE_ACCEPT_ORDER = 1;
    public static final int STATE_DELEGATE_COLLECTING_ORDER = 2;
    public static final int STATE_DELEGATE_COLLECTED_ORDER = 3;
    public static final int STATE_DELEGATE_DELIVERING_ORDER = 4;
    public static final int STATE_DELEGATE_DELIVERED_ORDER = 5;


    public static final int START_TYPING = 1;
    public static final int END_TYPING= 2;


    public static final String FIREBASE_NOT_TYPING = "typing";
    public static final String FIREBASE_NOT_MOVMENT = "order_movement";
    public static final String FIREBASE_NOT_DRIVER_ACTION = "driver_action";
    public static final String FIREBASE_NOT_CLIENT_ACTION = "client_action";
    public static final String FIREBASE_NOT_SEND_MESSAGE = "send_message";




}
