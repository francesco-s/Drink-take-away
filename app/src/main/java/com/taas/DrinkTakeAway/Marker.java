package com.taas.DrinkTakeAway;

public class Marker {

    //ID, NAME, TYPE, ADDRESS, LATITUDE, LONGITUDE
    private int id;
    private String name;
    private String type;
    private String address;
    private double lat;
    private double lon;
    private long crowding;



    public Marker(int id, String name, String type, String address, double lat, double lon, long crowding)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.crowding = crowding;
    }

    public long getCrowding() {
        return crowding;
    }

    public void setCrowding(long crowding) {
        this.crowding = crowding;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


}
