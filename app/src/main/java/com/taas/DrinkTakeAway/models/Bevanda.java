package com.taas.DrinkTakeAway.models;

public class Bevanda {
    String name;
    String type;

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Bevanda(String name, String type){

        this.name = name;
        this.type = type;
    }


}
