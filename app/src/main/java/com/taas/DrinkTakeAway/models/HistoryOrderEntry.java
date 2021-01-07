package com.taas.DrinkTakeAway.models;

import java.io.Serializable;

public class HistoryOrderEntry  extends CartEntry implements Serializable{

    String timestamp;
    int orderNumber;

    public HistoryOrderEntry(String drinkID, String localName, String drinkName, int numerosity, float price, int orderNumber, String timestamp) {
        super(drinkID, localName, drinkName, numerosity, price);
        this.timestamp = timestamp;
        this.orderNumber = orderNumber;
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
}
