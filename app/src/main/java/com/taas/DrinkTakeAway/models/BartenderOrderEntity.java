package com.taas.DrinkTakeAway.models;

public class BartenderOrderEntity extends HistoryOrderEntry {

    String userEmail;

    public BartenderOrderEntity(String drinkID, String localName, String drinkName, int numerosity, float price, int orderNumber, String timestamp, String status, String userEmail) {
        super(drinkID, localName, drinkName, numerosity, price, orderNumber, timestamp, status);
        this.userEmail = userEmail;
    }

    public String getEmail() {
        return userEmail;
    }
    public void setEmail(String email) {
        this.userEmail = email;
    }
}
