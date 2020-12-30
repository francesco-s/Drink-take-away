package com.taas.DrinkTakeAway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.taas.DrinkTakeAway.models.EntryOrdine;

import java.util.ArrayList;

public class ShoppingCart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);


        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        ArrayList<EntryOrdine> eo=(ArrayList<EntryOrdine> )bundle.getSerializable("ordine");

        int a=0;

    }
}