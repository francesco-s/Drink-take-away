package com.taas.DrinkTakeAway.models;

import java.io.Serializable;

public class HistoryOrderEntry  extends CartEntry implements Serializable{

    String timestamp;
    int orderNumber;
    String status;


    public HistoryOrderEntry(String drinkID, String localName, String drinkName, int numerosity, float price, int orderNumber, String timestamp, String status) {
        super(drinkID, localName, drinkName, numerosity, price);
        this.timestamp = timestamp;
        this.orderNumber = orderNumber;
        this.status = status;

    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
