package com.taas.DrinkTakeAway.models;

import java.io.Serializable;

public class CartEntry implements Serializable {

    String localName;
    String drinkName;
    float price;
    int numerosity;
    String drinkID;

    public CartEntry(String drinkID, String localName, String drinkName, float price){
        this.localName=localName;
        this.drinkName =drinkName;
        this.price=price;
        this.numerosity = 1;
        this.drinkID = drinkID;
    }

    public void setDrinkID(String drinkID) {
        this.drinkID = drinkID;
    }

    public String getDrinkID() {
        return drinkID;
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

    public void setNumerosity(int numerosity) {
        this.numerosity = numerosity;
    }

    public int getNumerosity() {
        return numerosity;
    }

    public void increaseNum(){
        this.numerosity++;
    }

    public void decreaseNum(){ this.numerosity --;}


}
