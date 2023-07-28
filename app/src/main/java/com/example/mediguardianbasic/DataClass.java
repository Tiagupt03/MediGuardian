package com.example.mediguardianbasic;

public class DataClass {

    private String dataName;
    private int dataQuantity;
    private int dataCompartment;
    private String dataStartDate;
    private String dataStartTime;

    public DataClass() {
    }

    public DataClass(String dataName, int dataQuantity, int dataCompartment, String dataStartDate, String dataStartTime) {
        this.dataName = dataName;
        this.dataQuantity = dataQuantity;
        this.dataCompartment = dataCompartment;
        this.dataStartDate = dataStartDate;
        this.dataStartTime = dataStartTime;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public int getDataQuantity() {
        return dataQuantity;
    }

    public void setDataQuantity(int dataQuantity) {
        this.dataQuantity = dataQuantity;
    }

    public int getDataCompartment() {
        return dataCompartment;
    }

    public void setDataCompartment(int dataCompartment) {
        this.dataCompartment = dataCompartment;
    }

    public String getDataStartDate() {
        return dataStartDate;
    }

    public void setDataStartDate(String dataStartDate) {
        this.dataStartDate = dataStartDate;
    }

    public String getDataStartTime() {
        return dataStartTime;
    }

    public void setDataStartTime(String dataStartTime) {
        this.dataStartTime = dataStartTime;
    }
}
