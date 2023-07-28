package com.example.mediguardianbasic;

public class DosageDataClass {
    private String dataFrequency;
    private Float dataDosage;

    public int dataCompartment;

    public int getDataCompartment() {
        return dataCompartment;
    }

    public Float getDataDosage() {
        return dataDosage;
    }

    public String getDataFrequency() {
        return dataFrequency;
    }


    public DosageDataClass(String dataFrequency, Float dataDosage, Integer dataCompartment) {
        this.dataFrequency = dataFrequency;
        this.dataDosage = dataDosage;
        this.dataCompartment = dataCompartment;
    }

    public DosageDataClass(){}
}
