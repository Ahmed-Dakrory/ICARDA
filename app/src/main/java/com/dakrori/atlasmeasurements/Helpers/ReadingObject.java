package com.dakrori.atlasmeasurements.Helpers;

public class ReadingObject {

    private String id;

    private String lat;

    private String longitude;

    private String type;

    private String value;
    private String datetimeNow;
    private String WaterOrSoil;
    private String Address;
    private String SOIL_TYPE;
    private String CROP_TYPE;


    public ReadingObject(String id, String lat, String longitude, String type, String value, String datetimeNow,
            String WaterOrSoil1,String Address1,String SOIL_TYPE1,String CROP_TYPE1) {
        this.id = id;
        this.lat = lat;
        this.longitude = longitude;
        this.type = type;
        this.value = value;
        this.datetimeNow = datetimeNow;
        this.WaterOrSoil = WaterOrSoil1;
        this.Address = Address1;
        this.SOIL_TYPE = SOIL_TYPE1;
        this.CROP_TYPE = CROP_TYPE1;
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

    public String getWaterOrSoil() {
        return WaterOrSoil;
    }

    public void setWaterOrSoil(String waterOrSoil) {
        WaterOrSoil = waterOrSoil;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getSOIL_TYPE() {
        return SOIL_TYPE;
    }

    public void setSOIL_TYPE(String SOIL_TYPE) {
        this.SOIL_TYPE = SOIL_TYPE;
    }

    public String getCROP_TYPE() {
        return CROP_TYPE;
    }

    public void setCROP_TYPE(String CROP_TYPE) {
        this.CROP_TYPE = CROP_TYPE;
    }
}
