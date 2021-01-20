package com.taas.DrinkTakeAway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.taas.DrinkTakeAway.adapter.BartenderOrderAdapter;
import com.taas.DrinkTakeAway.adapter.HistoryOrderAdapter;
import com.taas.DrinkTakeAway.models.BartenderOrderEntity;
import com.taas.DrinkTakeAway.models.CartEntry;
import com.taas.DrinkTakeAway.models.HistoryOrderEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BartenderActivity extends AppCompatActivity implements BartenderOrderAdapter.onDrinkListenerCart {

    final String serverAddress = "http://192.168.1.157:1111/api/v1/";
    //final String serverAddress = "http://192.168.1.90:1111/api/v1/";


    Context context;
    RecyclerView rvBartenderHistory;
    private RequestQueue mQueue;
    String accessToken;

    private List<BartenderOrderEntity> bartenderOrderEntityList;

    private String localName = "Jumping Jester";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bartender);

        this.setTitle("Bartender Order list");

        context = this;

        mQueue = Volley.newRequestQueue(this);
        bartenderOrderEntityList = new ArrayList<>();

        rvBartenderHistory = (RecyclerView) findViewById(R.id.recyclermenuBartender);

        //Retrieve token
        SharedPreferences preferences = BartenderActivity.this.getSharedPreferences("drink_take_away",Context.MODE_PRIVATE);
        accessToken  = preferences.getString("token",null);//second parameter default value.


        getAllBartenderOrders(localName);
    }




    private void getAllBartenderOrders(String email)
    {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, serverAddress + "localOrders?nameLocale=" + localName , null, new Response.Listener<JSONArray>() {
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
                        int numerosity = r.getInt("numerosity");
                        String status = r.getString("status");
                        String drinkId = r.getJSONObject("menu").getJSONObject("id").getString("id_bevanda");

                        String email = r.getString("email");
                        float price =(float) r.getJSONObject("menu").getLong("price");

                        bartenderOrderEntityList.add(new BartenderOrderEntity(drinkId, localName, drinkName, numerosity, price, orderNumber, timestamp, status, email));

                    }

                    rvBartenderHistory.addItemDecoration(new DividerItemDecoration(context, LinearLayout.VERTICAL));
                    BartenderOrderAdapter adapter = new BartenderOrderAdapter(bartenderOrderEntityList, (BartenderOrderAdapter.onDrinkListenerCart) context, localName);
                    // Attach the adapter to the recyclerview to populate items
                    rvBartenderHistory.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        rvBartenderHistory.setLayoutManager(new LinearLayoutManager(this));
        mQueue.add(request);

    }

    @Override
    public void onCompletedButtonClick(String orderNumber, String localName, String email) {
        DMLreq(orderNumber, "completed");
        saveOrderToDB2(Integer.parseInt(orderNumber));
    }

    @Override
    public void onPreparingButtonClick(String orderNumber) {
        DMLreq(orderNumber, "preparing");
    }

    private void DMLreq(String orderNumber, String status)
    {

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, serverAddress + "bartender/updateStatusOrder?status=" + status + "&orderNumber=" + Integer.parseInt(orderNumber), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Toast.makeText(context, response.toString(), Toast.LENGTH_LONG);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        rvBartenderHistory.setLayoutManager(new LinearLayoutManager(this));
        mQueue.add(request);
    }



    private void saveOrderToDB2(int orderNumber)
    {
        JSONObject innerParams, params;

        innerParams = new JSONObject();
        params = new JSONObject();


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, serverAddress + "getDrinkByOrderNumber?orderNumber=" + orderNumber , null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for(int i=0; i< response.length(); i++)
                    {
                        JSONObject r  = response.getJSONObject(i);
                        String drinkName = r.getJSONObject("menu").getJSONObject("bevanda").getString("name");
                        String localName = r.getJSONObject("menu").getJSONObject("locale").getString("name");
                        String email = r.getString("email");
                        int numerosity = r.getInt("numerosity");

                        innerParams.put("localName", localName);
                        innerParams.put("drinkName", drinkName);

                        innerParams.put("number", orderNumber);
                        innerParams.put("userEmail", email);

                        innerParams.put("quantity", numerosity);

                        params.put(String.valueOf(i), innerParams);
                    }

                    String address = "http://192.168.1.157:1112/api/v1/";
                    //String address = "http://192.168.1.90:1112/api/v1/";
                    JsonObjectRequest request2 = new JsonObjectRequest(address+ "orderHistory/saveOrder", params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String r  = response.toString();

                            int a=0;
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    mQueue.add(request2);

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

        mQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}