package com.creativeshare.mrsool.models;

import java.io.Serializable;

public class NotificationModel implements Serializable {

    private String id_notification;
    private String date_notification;
    private String title_notification;
    private String from_user_phone;
    private String from_user_full_name;
    private String from_user_image;
    private String from_user_type;
    private String order_id;
    private String order_status;
    private String order_movement;
    private String driver_offer;
    private String order_details;
    private double rate;
    private String client_address;
    private String driver_id;
    private String client_id;
    private String place_lat;
    private String place_long;
    private String place_address;
    private String order_type;



    public String getId_notification() {
        return id_notification;
    }

    public String getDate_notification() {
        return date_notification;
    }

    public String getTitle_notification() {
        return title_notification;
    }

    public String getFrom_user_phone() {
        return from_user_phone;
    }

    public String getFrom_user_full_name() {
        return from_user_full_name;
    }

    public String getFrom_user_image() {
        return from_user_image;
    }

    public String getFrom_user_type() {
        return from_user_type;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getOrder_movement() {
        return order_movement;
    }

    public String getDriver_offer() {
        return driver_offer;
    }

    public String getOrder_details() {
        return order_details;
    }

    public double getRate() {
        return rate;
    }

    public String getClient_address() {
        return client_address;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getPlace_lat() {
        return place_lat;
    }

    public String getPlace_long() {
        return place_long;
    }

    public String getPlace_address() {
        return place_address;
    }

    public String getOrder_type() {
        return order_type;
    }
}
