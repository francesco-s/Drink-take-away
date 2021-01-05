package com.taas.DrinkTakeAway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.taas.DrinkTakeAway.adapter.CartAdapter;
import com.taas.DrinkTakeAway.adapter.DrinkAdapter;
import com.taas.DrinkTakeAway.models.Bevanda;
import com.taas.DrinkTakeAway.models.CartEntry;
import com.taas.DrinkTakeAway.models.Locale;
import com.taas.DrinkTakeAway.models.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShoppingCart extends AppCompatActivity implements CartAdapter.onDrinkListenerCart{


    private RequestQueue mQueue;

    RecyclerView rvBevandeCart;
    ArrayList<CartEntry> ordine;
    Context context;
    CartAdapter adapter;
    Button submit;

    String localID;
    String userEmail;

    final String serverAddress = "http://192.168.1.90:1111/api/v1/saveOrder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        mQueue = Volley.newRequestQueue(this);

        rvBevandeCart = (RecyclerView) findViewById(R.id.recyclercart);
        submit = (Button) findViewById(R.id.submit);
        context = this;

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        ordine= (ArrayList<CartEntry>) bundle.getSerializable("order");
        localID = (String) bundle.getSerializable("localID");
        userEmail = (String) bundle.getSerializable("userEmail");

        adapter = new CartAdapter(ordine, (CartAdapter.onDrinkListenerCart) this);
        rvBevandeCart.setAdapter(adapter);
        rvBevandeCart.setLayoutManager(new LinearLayoutManager(this));


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObj= buildJsonObj();
                submitOrder(jsonObj);
            }
        });

    }


    @Override
    public void onMinusButtonClickGetDrink(String name, String price, String numerosity, int pos) {
        if (Integer.parseInt(numerosity) == 1){
            ordine.remove(pos);
        }
        else{
            CartEntry ce = ordine.get(pos);
            ce.decreaseNum();
        }
        adapter = new CartAdapter(ordine, (CartAdapter.onDrinkListenerCart) context);
        rvBevandeCart.setAdapter(adapter);
    }



    private void submitOrder(JSONObject obj)
    {


        JsonObjectRequest request = new JsonObjectRequest(serverAddress, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String r  = response.toString();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);

    }



    JSONObject buildJsonObj(){

        JSONObject params;
        JSONObject paramChild;

        params = new JSONObject();
        int count = 0;

        try {
            params.put("localID", localID);
            params.put("userEmail", userEmail);
            for (CartEntry ce : ordine){
                paramChild= new JSONObject();
                paramChild.put("drinkID", ce.getDrinkID());
                paramChild.put("drinkPrice", ce.getPrice());
                paramChild.put("drinkNumerosity", ce.getNumerosity());
                params.put(String.valueOf(count), paramChild);
                count ++;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;


    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putSerializable("order", ordine);
        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}