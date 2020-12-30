package com.taas.DrinkTakeAway.models;

import java.io.Serializable;

public class EntryOrdine implements Serializable {

    String localName;
    String drinkName;
    float price;

    public EntryOrdine(String localName, String drinkName, float price){
        this.localName=localName;
        this.drinkName =drinkName;
        this.price=price;

    }
    public float getPrice() {
        return price;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
