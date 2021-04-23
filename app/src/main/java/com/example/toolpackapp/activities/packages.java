package com.example.toolpackapp.activities;

import android.text.format.Time;

import java.util.Date;

public class packages {

    private String pvendor;
    private String pdriver;
    private String pstatus;
    private Date pdeliveryDate;
    private Time pdeliveryTime;

    public packages() {
    } // Needed for Firebase

    public packages(String vendor, String driver, String status, Date deliveryDate, Time deliveryTime) {
        pvendor = vendor;
        pdriver = driver;
        pstatus = status;
        pdeliveryDate = deliveryDate;
        pdeliveryTime = deliveryTime;
    }

    public String getVendor() {
        return pvendor;
    }

    public void setVendor(String vendor) {
        pvendor = vendor;
    }

    public String getDriver() {
        return pdriver;
    }

    public void setDriver(String driver) {
        pdriver = driver;
    }

    public String getStatus() {
        return pstatus;
    }

    public void setStatus(String status) {
        pstatus = status;
    }

    public Date getDate() {
        return pdeliveryDate;
    }

    public void setDate(Date deliveryDate) {
        pdeliveryDate = deliveryDate;
    }

    public Time getTime() {
        return pdeliveryTime;
    }

    public void setTime(Time deliveryTime) {
        pdeliveryTime = deliveryTime;
    }

}
