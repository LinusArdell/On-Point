package com.test.onpoint.Class;

public class PointDataClass {
    String lokasi, qrCode;
    double latitude, longitude;

    public PointDataClass() {

    }

    public PointDataClass(String lokasi, String qrCode, double latitude, double longitude){
        this.lokasi = lokasi;
        this.qrCode = qrCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getQrCode() {
        return qrCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
