package com.creativeshare.mrsool.models;

import java.io.Serializable;

public class OrderIdDataModel implements Serializable {

    private OrdrIdModel data;

    public OrdrIdModel getData() {
        return data;
    }

    public class OrdrIdModel implements Serializable
    {
        private String order_id;

        public String getOrder_id() {
            return order_id;
        }
    }
}
