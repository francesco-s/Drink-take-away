package com.taas.DrinkTakeAway;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class EndingPage extends AppCompatActivity {

    TextView id, amount, status;
    Button home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ending_page);

        id = findViewById(R.id.id);
        amount = findViewById(R.id.amount);
        status = findViewById(R.id.status);

        home = findViewById(R.id.btn_home);
        home.setBackgroundResource(R.drawable.ic_home);

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
            id.setText(response.getString("id"));
            amount.setText(paymentAmount + " €");
            status.setText(response.getString("state"));

        }catch (JSONException e)
        {

        }
    }
}