package com.creativeshare.mrsool.models;

import java.io.Serializable;

public class Favourite_location implements Serializable {

    private String name;
    private String street;
    private String address;
    private double lat;
    private double lng;

    public Favourite_location(String name, String street, String address, double lat, double lng) {
        this.name = name;
        this.street = street;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }


    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
