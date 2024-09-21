package com.test.onpoint.Class;

public class PointDataClass {
    String lokasi, qrCode, userName, dataDate, dataTime, key, keterangan;
    double latitude, longitude;
    boolean expanded, periksa;

    public PointDataClass() {

    }

    public PointDataClass(String lokasi, String qrCode, double latitude, double longitude, String userName, String dataDate, String dataTime, String keterangan, boolean periksa){
        this.lokasi = lokasi;
        this.qrCode = qrCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userName = userName;
        this.dataDate = dataDate;
        this.dataTime = dataTime;
        this.expanded = false;
        this.keterangan = keterangan;
        this.periksa = periksa;
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

    public String getUserName() {
        return userName;
    }

    public String getDataDate() {
        return dataDate;
    }

    public String getDataTime() {
        return dataTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public boolean isPeriksa() {
        return periksa;
    }
}
