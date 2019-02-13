package com.example.vanur.ocrnumberplatedetection.User.Activity;

public class PaymentClass {
    private String transId;
    private String commuter;
    private String toll;
    private String date;
    private String xAmount;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getCommuter() {
        return commuter;
    }

    public void setCommuter(String commuter) {
        this.commuter = commuter;
    }

    public String getToll() {
        return toll;
    }

    public void setToll(String toll) {
        this.toll = toll;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getxAmount() {
        return xAmount;
    }

    public void setxAmount(String amount) {
        this.xAmount = amount;
    }
}
