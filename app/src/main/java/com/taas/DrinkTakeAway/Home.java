package com.taas.DrinkTakeAway;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.taas.DrinkTakeAway.adapter.DrinkAdapter;
import com.taas.DrinkTakeAway.models.Bevanda;
import com.taas.DrinkTakeAway.models.EntryOrdine;
import com.taas.DrinkTakeAway.models.Locale;
import com.taas.DrinkTakeAway.models.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements DrinkAdapter.onDrinkListener,  NavigationView.OnNavigationItemSelectedListener{

    private RequestQueue mQueue;
    private String localName;

    ArrayList<Menu> menus;
    ArrayList<EntryOrdine> ordine;
    RecyclerView rvBevande;
    Context context;
    NavigationView nw;

    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Button birre = findViewById(R.id.beer);
        final Button vini = findViewById(R.id.wine);
        final Button cocktail = findViewById(R.id.cocktail);
        final Button allDrink = findViewById(R.id.all);
        rvBevande = (RecyclerView) findViewById(R.id.recyclermenu);

        context =this;
        VolleyCallback callback = null;

        mQueue = Volley.newRequestQueue(this);

        menus = new ArrayList<>();
        ordine = new ArrayList<>();

        bundle = new Bundle();

        nw=(NavigationView) findViewById(R.id.menuLat);
        nw.setNavigationItemSelectedListener(this);

        if(getIntent() != null){
            localName = getIntent().getStringExtra("name");
            System.out.println(localName);
        }

        birre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = birre.getText().toString();
                jsonParse(n);
            }
        });

        vini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = vini.getText().toString();
                jsonParse(n);
            }
        });

        cocktail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = cocktail.getText().toString();
                jsonParse(n);
            }
        });

        allDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = allDrink.getText().toString();
                jsonParse(n);
            }
        });



        InitJsonParsing(new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                for(int i=0; i< response.length(); i++)
                {
                    try {
                        JSONObject r  = response.getJSONObject(i);
                        String drinkName = r.getJSONObject("bevanda").getString("name");
                        String drinkType = r.getJSONObject("bevanda").getString("type");

                        String localName = r.getJSONObject("locale").getString("name");
                        String localAddress = r.getJSONObject("locale").getString("name");
                        String localType = r.getJSONObject("locale").getString("name");
                        String localLat = r.getJSONObject("locale").getString("name");
                        String localLon = r.getJSONObject("locale").getString("name");

                        float price =(float) r.getLong("price");


                        Bevanda bevandaAtt = new Bevanda(drinkName, drinkType);
                        Locale localeAtt = new Locale(localName, localAddress, localType, localLat, localLon);

                        menus.add(new Menu(localeAtt, bevandaAtt, price));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                DrinkAdapter adapter = new DrinkAdapter(menus, (DrinkAdapter.onDrinkListener) context);
                // Attach the adapter to the recyclerview to populate items
                rvBevande.setAdapter(adapter);
            }
        });

        rvBevande.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.cart: {
                Toast.makeText(context, "Hai cliccato  cart", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ShoppingCart.class);
                bundle.putSerializable("ordine", ordine);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
        return true;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuInfoC:{
                Toast.makeText(context, "Hai cliccato info cliente", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.menuSupport:{
                Toast.makeText(context, "Contatta francescosannicola1997@gmail.com", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.menuLogout:{
                Toast.makeText(context, "Hai cliccato logout", Toast.LENGTH_SHORT).show();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }




    public void InitJsonParsing (final VolleyCallback callback){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://192.168.1.90:1111/api/v1/menu?nameLocale=" + localName, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void jsonParse(String buttonName)
    {
        String url = setUrl(buttonName);
        ArrayList<Bevanda> bev = new ArrayList<Bevanda>();

        menus.clear();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    for(int i=0; i< response.length(); i++)
                    {

                        JSONObject r  = response.getJSONObject(i);
                        String drinkName = r.getJSONObject("bevanda").getString("name");
                        String drinkType = r.getJSONObject("bevanda").getString("type");

                        String localName = r.getJSONObject("locale").getString("name");
                        String localAddress = r.getJSONObject("locale").getString("name");
                        String localType = r.getJSONObject("locale").getString("name");
                        String localLat = r.getJSONObject("locale").getString("name");
                        String localLon = r.getJSONObject("locale").getString("name");

                        float price =(float) r.getLong("price");

                        Bevanda bevandaAtt = new Bevanda(drinkName, drinkType);
                        Locale localeAtt = new Locale(localName, localAddress, localType, localLat, localLon);

                        menus.add(new Menu(localeAtt, bevandaAtt, price));


                    }

                    DrinkAdapter adapter = new DrinkAdapter(menus, (DrinkAdapter.onDrinkListener) context);
                    // Attach the adapter to the recyclerview to populate items
                    rvBevande.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);

    }

    public String setWelcomeText(String defaultName)
    {
        String name = "";
        String complete = "";

        Bundle extras = getIntent().getExtras();

        if(extras!=null)
        { name = extras.getString("key"); }

        if(!defaultName.equals(""))
        { complete = defaultName.replace("name", name); }

        return complete;
    }

    public String setUrl(String buttonName)
    {
        String url = "";
        switch (buttonName)
        {
            case "All":
                url = "http://192.168.1.90:1111/api/v1/menu?nameLocale=" + localName;
                break;
            case "Beers":
                url = "http://192.168.1.90:1111/api/v1/specificdrinktype?nameLocale=" + localName + "&typeBevanda=beer";
                break;
            case "Wines":
                url = "http://192.168.1.90:1111/api/v1/specificdrinktype?nameLocale=" + localName + "&typeBevanda=wine";
                break;
            case "Cocktails":
                url = "http://192.168.1.90:1111/api/v1/specificdrinktype?nameLocale=" + localName + "&typeBevanda=cocktail";
                break;
        }   //FINE SWITCH

    return url;
    }

    @Override
    public void onDrinkClick(int pos) {
        Toast.makeText(getApplicationContext(), String.valueOf(pos),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDrinkClickGetName(String pos) {
        Toast.makeText(getApplicationContext(), pos,
                Toast.LENGTH_LONG).show();
        int a=0;
    }

    @Override
    public void onAddButtonClickGetDrink(String name, String price) {
        Toast.makeText(getApplicationContext(), "local name: "+ localName + ". drink name: "+ name + ". price: "+ price,
                Toast.LENGTH_SHORT).show();
        ordine.add(new EntryOrdine(localName, name, Float.parseFloat(price.replaceAll("€",""))));


    }
}