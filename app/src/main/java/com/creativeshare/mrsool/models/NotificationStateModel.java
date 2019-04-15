package com.creativeshare.mrsool.models;

import java.io.Serializable;

public class NotificationStateModel implements Serializable {

    private String notification_state;
    private String order_movement;

    public void setNotification_state(String notification_state) {
        this.notification_state = notification_state;
    }

    public void setOrder_movement(String order_movement) {
        this.order_movement = order_movement;
    }

    public String getNotification_state() {
        return notification_state;
    }

    public String getOrder_movement() {
        return order_movement;
    }
}
