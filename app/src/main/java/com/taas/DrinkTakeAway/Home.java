package com.taas.DrinkTakeAway;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
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

    TextView textCartItemCount;
    int mCartItemCount;

    boolean isLoggedInFacebook, isLoggedInGoogle;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mCartItemCount = 0;

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

        //Navigation view
        nw=(NavigationView) findViewById(R.id.menuLat);
        nw.setNavigationItemSelectedListener(this);

        View innerview=nw.getHeaderView(0);
        TextView textViewMenuTopUtente=(TextView) innerview.findViewById(R.id.textViewMenuTopUtente);
        TextView textViewMenuTopEmail=(TextView) innerview.findViewById(R.id.textViewMenuTopEmail);
        ImageView userImage = (ImageView) innerview.findViewById(R.id.imageView);


        //Check Facebook or Google user info
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        AccessToken facebookAccessToken = AccessToken.getCurrentAccessToken();

        isLoggedInFacebook = facebookAccessToken != null && !facebookAccessToken.isExpired();
        isLoggedInGoogle = googleAccount != null && !googleAccount.isExpired();


        if (isLoggedInGoogle){
            Picasso.get()
                    .load(googleAccount.getPhotoUrl().toString())
                    .resize(100, 100)
                    .centerCrop()
                    .into(userImage);
            textViewMenuTopUtente.setText(googleAccount.getDisplayName().toString());
            textViewMenuTopEmail.setText(googleAccount.getEmail().toString());
        }

        if (isLoggedInFacebook){
            GraphRequest request = GraphRequest.newMeRequest(
                    facebookAccessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                textViewMenuTopUtente.setText(object.getString("name"));
                                textViewMenuTopEmail.setText(object.getString("email"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();

            Picasso.get()
                    .load("http://graph.facebook.com/" + facebookAccessToken.getUserId().toString() + "/picture?type=square")
                    .resize(100, 100)
                    .centerCrop()
                    .into(userImage);

        }

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

                        String localId = r.getJSONObject("locale").getString("id");
                        String localName = r.getJSONObject("locale").getString("name");
                        String localAddress = r.getJSONObject("locale").getString("address");
                        String localType = r.getJSONObject("locale").getString("type");
                        String localLat = r.getJSONObject("locale").getString("lat");
                        String localLon = r.getJSONObject("locale").getString("lon");
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

        final MenuItem menuItem = menu.findItem(R.id.cart);

        View actionView = menuItem.getActionView();
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    private void setupBadge() {

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.cart: {
                Toast.makeText(context, "Hai cliccato  cart", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, ShoppingCart.class);
                bundle.putSerializable("order", ordine);

                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                //startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();

                ArrayList<EntryOrdine> newOrder=(ArrayList<EntryOrdine> )bundle.getSerializable("order");

                ordine = newOrder;
                mCartItemCount =0;

                for (EntryOrdine eo: ordine) {
                    mCartItemCount += eo.getNumerosity();
                }
                setupBadge();

            }
        }
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

                if (isLoggedInGoogle){
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                    GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(context,gso);
                    googleSignInClient.signOut();
                }
                else if (isLoggedInFacebook)
                    LoginManager.getInstance().logOut();
                if (isLoggedInFacebook || isLoggedInGoogle){
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
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
    }

    @Override
    public void onAddButtonClickGetDrink(String name, String price) {
        Toast.makeText(getApplicationContext(), "local name: "+ localName + ". drink name: "+ name + ". price: "+ price,
                Toast.LENGTH_SHORT).show();
        boolean checkExists = false;

        for (EntryOrdine eo: ordine) {
            if (eo.getDrinkName().equals(name) && eo.getLocalName().equals(localName)){
                eo.increaseNum();
                checkExists = true;
            }
        }
        if (!checkExists)
            ordine.add(new EntryOrdine(localName, name, Float.parseFloat(price.replaceAll("€",""))));

        mCartItemCount++;
        setupBadge();
    }
}