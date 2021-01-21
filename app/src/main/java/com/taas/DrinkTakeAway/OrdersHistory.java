package com.taas.DrinkTakeAway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.taas.DrinkTakeAway.adapter.DrinkAdapter;
import com.taas.DrinkTakeAway.adapter.HistoryOrderAdapter;
import com.taas.DrinkTakeAway.models.Bevanda;
import com.taas.DrinkTakeAway.models.HistoryOrderEntry;
import com.taas.DrinkTakeAway.models.Locale;
import com.taas.DrinkTakeAway.models.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersHistory extends AppCompatActivity implements HistoryOrderAdapter.onDrinkListenerCart{

    //final String serverAddress = "http://192.168.1.157:1111/api/v1/";
    final String serverAddress = "http://192.168.1.90:1111/api/v1/";

    private String email;
    private RequestQueue mQueue;
    private List<HistoryOrderEntry> historyOrderEntryList;

    Context context;
    RecyclerView rvOrder;
    String accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);

        this.setTitle("This is your Order History");

        rvOrder = (RecyclerView) findViewById(R.id.recyclerOrderHistory);
        historyOrderEntryList = new ArrayList<>();

        context = this;
        mQueue = Volley.newRequestQueue(this);

        if(getIntent() != null)
            email = getIntent().getStringExtra("email");

        SharedPreferences preferences = OrdersHistory.this.getSharedPreferences("drink_take_away",Context.MODE_PRIVATE);
        accessToken  = preferences.getString("token",null);

        getAllUserOrders(email);
    }

    private void getAllUserOrders(String email)
    {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, serverAddress + "orders?email=" + email , null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for(int i=0; i< response.length(); i++)
                    {
                        JSONObject r  = response.getJSONObject(i);

                        int orderNumber = r.getInt("number");
                        String drinkName = r.getJSONObject("menu").getJSONObject("bevanda").getString("name");
                        String localName = r.getJSONObject("menu").getJSONObject("locale").getString("name");
                        String timestamp = r.getString("timestamp");
                        String status = r.getString("status");
                        int numerosity = r.getInt("numerosity");
                        String drinkId = r.getJSONObject("menu").getJSONObject("id").getString("id_bevanda");
                        float price =(float) r.getJSONObject("menu").getLong("price");

                        historyOrderEntryList.add(new HistoryOrderEntry(drinkId, localName, drinkName, numerosity, price, orderNumber, timestamp, status));
                    }
                    //rvOrder.addItemDecoration(new DividerItemDecoration(context, LinearLayout.VERTICAL));
                    HistoryOrderAdapter adapter = new HistoryOrderAdapter(historyOrderEntryList, (HistoryOrderAdapter.onDrinkListenerCart) context);
                    // Attach the adapter to the recyclerview to populate items
                    rvOrder.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        rvOrder.setLayoutManager(new LinearLayoutManager(this));
        mQueue.add(request);

    }

    @Override
    public void onMinusButtonClickGetDrink(String name, String price, String numerosity, int pos) {

    }
}