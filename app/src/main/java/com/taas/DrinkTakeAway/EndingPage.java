package com.taas.DrinkTakeAway;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class EndingPage extends AppCompatActivity {

    TextView id, amount, status, orderID;
    Button home, history;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ending_page);

        getSupportActionBar().hide();

        orderID = findViewById(R.id.order_id);
        id = findViewById(R.id.id);
        amount = findViewById(R.id.amount);
        status = findViewById(R.id.status);

        home = findViewById(R.id.btn_home);

        home.setBackgroundResource(R.drawable.ic_home);
        home.setTooltipText("Go back to the home page");
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(EndingPage.this, MapsActivity.class);
                EndingPage.this.startActivity(homeIntent);
                finish();
            }
        });

        history = findViewById(R.id.btn_history);
        history.setTooltipText("See your order history");
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(EndingPage.this, OrdersHistory.class);
                EndingPage.this.startActivity(historyIntent);
                finish();
            }
        });

        Intent intent = getIntent();

        try {
            JSONObject json = new JSONObject((intent.getStringExtra("PaymentDetails")));
            showDetails(json.getJSONObject("response"), intent.getFloatExtra("PaymentAmount", 0));
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //Shows the major information about the payment
    private void showDetails(JSONObject response, float paymentAmount) {

        try {
            String payId = response.getString("id");

            id.setText("Payment ID: " + payId);
            amount.setText("Total: " + paymentAmount + " €");
            status.setText("Status: " + response.getString("state"));
            orderID.setText("Order code: " + generateCode());


            //Toast.makeText(this, "Pagamento effettuato", Toast.LENGTH_LONG).show();

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public String generateCode()
    {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        String code = "";

        Random rand = new Random();
        int length = 15;
        char[] text = new char[length];

        for(int i=0; i<length; i++)
            text[i] = base.charAt(rand.nextInt(base.length()));

        for(int i=0; i<text.length; i++)
            code += text[i];


        return code;
    }
}