package com.creativeshare.mrsool.models;

import java.io.Serializable;
import java.util.List;

public class NearDelegateDataModel implements Serializable {

    private List<DelegateModel> data;
    private Meta meta;

    public List<DelegateModel> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public class DelegateModel implements Serializable
    {
        private String user_id;
        private String user_phone;
        private String user_full_name;
        private String user_image;
        private String user_google_lat;
        private String user_google_long;
        private String distance;

        public String getUser_id() {
            return user_id;
        }

        public String getUser_phone() {
            return user_phone;
        }

        public String getUser_full_name() {
            return user_full_name;
        }

        public String getUser_image() {
            return user_image;
        }

        public String getUser_google_lat() {
            return user_google_lat;
        }

        public String getUser_google_long() {
            return user_google_long;
        }

        public String getDistance() {
            return distance;
        }
    }

    public class Meta implements Serializable
    {
        private int current_page;
        private int last_page;
        private int total_drivers;


        public int getCurrent_page() {
            return current_page;
        }

        public int getLast_page() {
            return last_page;
        }

        public int getTotal_drivers() {
            return total_drivers;
        }
    }

}
