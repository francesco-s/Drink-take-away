package com.taas.DrinkTakeAway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.taas.DrinkTakeAway.adapter.CartAdapter;
import com.taas.DrinkTakeAway.adapter.DrinkAdapter;
import com.taas.DrinkTakeAway.models.Bevanda;
import com.taas.DrinkTakeAway.models.CartEntry;
import com.taas.DrinkTakeAway.models.Locale;
import com.taas.DrinkTakeAway.models.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart extends AppCompatActivity implements CartAdapter.onDrinkListenerCart{


    private RequestQueue mQueue;

    RecyclerView rvBevandeCart;
    ArrayList<CartEntry> ordine;
    Context context;
    CartAdapter adapter;
    Button submit;

    String localID;
    String userEmail;

    String accessToken;


    float amount;
    float total ;

    private int PAYPAL_REQ_CODE = 12;
    //public static final String PAYPAL_CLIENT_ID = "Aav06doww9oKl6daarVYc2Uzvlm5w_FpkWS_Uh4QWCLT3Y3X3J5EWSoXJUJMBJkqrd-tnlYZ2SpBfJcQ";
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId(PayPalClientIDConfig.PAYPAL_CLIENT_ID);
    //WE USE ENVIRONMENT SANDBOX, TEST PURPOSES ONLY
    //ENVIRONMENT PRODUCTION IS USED WHEN THE APP IS READY FOR THE RELEASE


    final String serverAddress = "http://192.168.1.90:1111/api/v1/saveOrder";
    //final String serverAddress = "http://192.168.1.157:1111/api/v1/saveOrder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        ShoppingCart.this.setTitle("Your Shopping Cart");

        mQueue = Volley.newRequestQueue(this);

        rvBevandeCart = (RecyclerView) findViewById(R.id.recyclercart);
        submit = (Button) findViewById(R.id.pay_button);
        context = this;

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        ordine= (ArrayList<CartEntry>) bundle.getSerializable("order");
        localID = (String) bundle.getSerializable("localID");
        userEmail = (String) bundle.getSerializable("userEmail");

        adapter = new CartAdapter(ordine, (CartAdapter.onDrinkListenerCart) this);
        rvBevandeCart.setAdapter(adapter);
        rvBevandeCart.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences preferences = ShoppingCart.this.getSharedPreferences("drink_take_away",Context.MODE_PRIVATE);
        accessToken  = preferences.getString("token",null);//second parameter default value.


        total = 0;
        int oID;
        for(int i=0;i<ordine.size();i++)
        { total = total + ordine.get(i).getPrice(); }


        /*
         * PayPal payment section start
         */
        Intent payIntent = new Intent(this, PayPalService.class);
        payIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(payIntent);

        float finalTotal = total;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                payPalPaymentMethod(finalTotal);
                amount = finalTotal;

                JSONObject jsonObj= buildJsonObj();
                submitOrder(jsonObj);
            }
        });

    }



    private void payPalPaymentMethod(float totale) {

        PayPalPayment pay = new PayPalPayment(new BigDecimal(totale),
                "EUR", "Spike", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, pay);

        startActivityForResult(intent, PAYPAL_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //WE CHECK THE REQUEST CODE BECAUSE THERE MAY BE DIFFERENT OTHER CODES FOR OTHER ACTIVITIES
        if(requestCode == PAYPAL_REQ_CODE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation!=null)
                {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        startActivity(new Intent(this, EndingPage.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount)
                        );  //startActivity method ends

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "Payment canceled", Toast.LENGTH_LONG);
            }
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
        {
            Toast.makeText(this, "Invalid payment", Toast.LENGTH_LONG);
        }
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