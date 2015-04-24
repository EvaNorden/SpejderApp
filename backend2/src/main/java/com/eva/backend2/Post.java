package com.eva.backend2;

import com.googlecode.objectify.annotation.Entity;

import java.io.Serializable;

/**
 * Created by Eva on 12-03-2015.
 */
public class Post implements Serializable {
    private String name;
    private int number;
    private String description;
    private double latitude;
    private double longitude;
    private String location;

    public Post() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
