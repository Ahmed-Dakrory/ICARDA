package com.dakrori.atlasmeasurements.Helpers;

public class ReadingObject {

    private String id;

    private String lat;

    private String longitude;

    private String type;

    private String value;
    private String datetimeNow;


    public ReadingObject(String id, String lat, String longitude, String type, String value, String datetimeNow) {
        this.id = id;
        this.lat = lat;
        this.longitude = longitude;
        this.type = type;
        this.value = value;
        this.datetimeNow = datetimeNow;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDatetimeNow() {
        return datetimeNow;
    }

    public void setDatetimeNow(String datetimeNow) {
        this.datetimeNow = datetimeNow;
    }
}
