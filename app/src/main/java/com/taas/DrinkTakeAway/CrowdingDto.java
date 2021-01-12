package com.taas.DrinkTakeAway;

public class CrowdingDto
{
    private long sum;
    private int id_locale;

    public CrowdingDto(long sum, int id_locale)
    {
        this.sum = sum;
        this.id_locale = id_locale;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public int getId_locale() {
        return id_locale;
    }

    public void setId_locale(int id_locale) {
        this.id_locale = id_locale;
    }
}
