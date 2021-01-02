package com.taas.DrinkTakeAway;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.taas.DrinkTakeAway.adapter.CartAdapter;
import com.taas.DrinkTakeAway.adapter.DrinkAdapter;
import com.taas.DrinkTakeAway.models.EntryOrdine;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ShoppingCart extends AppCompatActivity implements CartAdapter.onDrinkListenerCart{

    RecyclerView rvBevandeCart;
    ArrayList<EntryOrdine> ordine;
    Context context;
    CartAdapter adapter;

    private int PAYPAL_REQ_CODE = 12;
    //public static final String PAYPAL_CLIENT_ID = "Aav06doww9oKl6daarVYc2Uzvlm5w_FpkWS_Uh4QWCLT3Y3X3J5EWSoXJUJMBJkqrd-tnlYZ2SpBfJcQ";
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalClientIDConfig.PAYPAL_CLIENT_ID);
    //SI USA ENVIRONMENT SANDBOX IN QUANTO STIAMO FACENDO DEI TEST (TEST PURPOSES ONLY)
    //SI USA ENVIRONMENT PRODUCTION QUANDO INVECE L'APP E' PRONTA PER LA RELEASE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        rvBevandeCart = (RecyclerView) findViewById(R.id.recyclercart);
        context = this;
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        ordine=(ArrayList<EntryOrdine>)bundle.getSerializable("ordine");
        float totale = 0;
        
        for(int i=0;i<ordine.size();i++)
        {
            totale = totale + ordine.get(i).getPrice();
        }
        Log.i("totale", "il totale da passare è " + totale);
        
        adapter = new CartAdapter(ordine, (CartAdapter.onDrinkListenerCart) this);
        rvBevandeCart.setAdapter(adapter);
        rvBevandeCart.setLayoutManager(new LinearLayoutManager(this));

        /*
         *
         * PayPal payment section start
         *
         */
        Button pay = findViewById(R.id.pay_button);

        Intent payIntent = new Intent(this, PayPalService.class);
        payIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(payIntent);

        float finalTotale = totale;
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payPalPaymentMethod(finalTotale);
            }
        });
    }       //OnCreate Ends

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
                Log.i("msg", "Payment successful");
            }
            else
            {
                Toast.makeText(this, "Payment failed", Toast.LENGTH_LONG).show();
                Log.i("msg", "Payment failed");
            }
        }
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    public void onMinusButtonClickGetDrink(String name, String price, int pos) {
        ordine.remove(pos);
        adapter = new CartAdapter(ordine, (CartAdapter.onDrinkListenerCart) context);
        rvBevandeCart.setAdapter(adapter);
    }
}