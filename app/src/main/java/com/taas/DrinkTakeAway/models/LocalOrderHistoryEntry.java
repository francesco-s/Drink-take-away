package com.taas.DrinkTakeAway.models;

public class LocalOrderHistoryEntry {

    String localName;
    String drinkName;
    int number;
    String email;
    int quantity;

    public String getEmail() {
        return email;
    }

    public String getLocalName() {
        return localName;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public int getNumber() {
        return number;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}


