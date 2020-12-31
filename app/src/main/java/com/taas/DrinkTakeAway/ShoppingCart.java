package com.taas.DrinkTakeAway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.taas.DrinkTakeAway.adapter.CartAdapter;
import com.taas.DrinkTakeAway.adapter.DrinkAdapter;
import com.taas.DrinkTakeAway.models.EntryOrdine;

import java.util.ArrayList;

public class ShoppingCart extends AppCompatActivity implements CartAdapter.onDrinkListenerCart{

    RecyclerView rvBevandeCart;
    ArrayList<EntryOrdine> ordine;
    Context context;
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        rvBevandeCart = (RecyclerView) findViewById(R.id.recyclercart);
        context = this;
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        ordine=(ArrayList<EntryOrdine> )bundle.getSerializable("order");

        adapter = new CartAdapter(ordine, (CartAdapter.onDrinkListenerCart) this);
        rvBevandeCart.setAdapter(adapter);
        rvBevandeCart.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onMinusButtonClickGetDrink(String name, String price, int pos) {
        ordine.remove(pos);
        adapter = new CartAdapter(ordine, (CartAdapter.onDrinkListenerCart) context);
        rvBevandeCart.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {

        Toast.makeText(getApplicationContext(), "local name: . drink name:",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putSerializable("order", ordine);
        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}