package com.taas.DrinkTakeAway;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taas.DrinkTakeAway.Interface.CrowdingVolleyResponseListener;
import com.taas.DrinkTakeAway.Interface.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private static final String EXTRA_TEXT = "com.example.mapint.MapsActivity.EXTRA_TEXT";

    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<CrowdingDto> crowdingList = new ArrayList<>();
    private Button button;
    private RequestQueue mQueue;
    private RequestQueue crowQueue;
    //final String ipAddress = "http://192.168.1.157";
    final String ipAddress = "http://192.168.1.90";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("time", "create "+java.time.Clock.systemUTC().instant());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mQueue = Volley.newRequestQueue(this);
        crowQueue = Volley.newRequestQueue(this);

        button = (Button) findViewById(R.id.menu_button);
        LinearLayout li = (LinearLayout) findViewById(R.id.bottom);
        li.setBackgroundColor(Color.parseColor("#fbb324"));

    }       //onCreate() ends

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        /*
         *READ JSON VIA URL
         */
        jsonParseLocali(new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.i("list", "Something wrong");
            }

            @Override
            public void onResponse(ArrayList<Marker> listOfMarker) {
                Log.i("list", "Merker list size: " + listOfMarker.size());

                String name, type, add;
                LatLng pos;
                double lat, lon;


                if(listOfMarker.isEmpty())
                {  Log.i("control", "Vuota");  }

                //Adding MARKER
                for(int i=0; i<listOfMarker.size();i++)
                {
                    lat = listOfMarker.get(i).getLat();
                    lon = listOfMarker.get(i).getLon();
                    type = listOfMarker.get(i).getType();

                    pos = new LatLng(lat, lon);
                    mMap.addMarker(new MarkerOptions().position(pos).title(listOfMarker.get(i).getName())
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), setCustomIcon(type))));

                }
                markers = listOfMarker;
            }
        });

            getCrowding(new CrowdingVolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Log.i("list", "Something wrong");
                }

                @Override
                public void onResponse(ArrayList<CrowdingDto> cDto) {
                    crowdingList = cDto;
                    for(CrowdingDto d:crowdingList)
                    {
                        Log.i("crow", "i val della lista sono " + d.getSum() + " " + d.getId_locale());     //Valori ok
                    }
                    Log.i("crow", "la lista è " + crowdingList);
                    Log.i("crow", "la lista è " + cDto);
                }
            });

        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnMapClickListener(this);
        setMapCenter();

    } // OnMapReady ends

    //------------------------------------------------------------------------------------------------------------//

    private void getCrowding(final CrowdingVolleyResponseListener listener)
    {
        //String url = "http://10.0.2.2:1111/api/v1/getDrinkQuantityToDo/"+id;
        String url = ipAddress + ":1111/api/v1/getDrinkQuantityToDo";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        ArrayList<CrowdingDto> cDto = new ArrayList<>();
                        try {

                            for(int i=0; i<response.length(); i++)
                            {
                                CrowdingDto obj = new CrowdingDto(0, 0);
                                JSONObject dtoCrowding = response.getJSONObject(i);
                                obj.setSum(dtoCrowding.getLong("sum"));
                                obj.setId_locale(dtoCrowding.getInt("id_local"));
                                Log.i("list", "cdto "+ dtoCrowding.getLong("sum") + dtoCrowding.getInt("id_local"));
                                cDto.add(obj);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int j =0;j<cDto.size();j++)
                            Log.i("list", "cdto "+ cDto.get(j).getId_locale() + " " + cDto.get(j).getSum());

                        listener.onResponse(cDto);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                listener.onError(error.toString());
            }
        });
        crowQueue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void jsonParseLocali(final VolleyResponseListener listener)
    {
        Log.i("time", "parse "+java.time.Clock.systemUTC().instant());
        Log.i("parse", "Parsing locali");
        String url = ipAddress + ":1113/api/v1/getAllLocals";

        ArrayList<Marker> al = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {
                try {
                    for(int i=0; i<response.length(); i++)
                    {
                        Marker m = new Marker(0,"name", "type", "address", 0.0, 0.0, 0);
                        JSONObject locale = response.getJSONObject(i);

                        m.setId(locale.getInt("id"));
                        m.setName(locale.getString("name"));
                        m.setAddress(locale.getString("address"));
                        m.setType(locale.getString("type"));
                        m.setLat(locale.getDouble("lat"));
                        m.setLon(locale.getDouble("lon"));
                        m.setCrowding(0);
                        al.add(m);
                    }

                    for (Marker m: al)
                    {   Log.i("each",  "--> " + m.getId() + " " + m.getName() + " " + m.getAddress() + " " + m.getType() + " " + m.getLat() + " " + m.getLon());    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onResponse(al);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                listener.onError(error.toString());
            }
        });

        mQueue.add(request);
    }    // jsonParseLocali method ends

    //------------------------------------------------------------------------------------------------------------//

    private void setMapCenter() {
        //SETTING FOCUS TO CITY CENTER
        float zoomLevel = 16.0f;
        LatLng mapCenter;
        //Mole Antonelliana 45.0690113  7.6910275
        //Porta Nuova/San Salvario 45.062055    7.6763373
        //Piazza Vittorio Veneto
        double latCenter = 45.0647992, lngCenter = 7.6930788;
        mapCenter = new LatLng(latCenter, lngCenter);
        mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(mapCenter, zoomLevel)));
    }

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
        TextView selectedMarkerLocalName = findViewById(R.id.local_name);

        final String localName = marker.getTitle();

        selectedMarkerLocalName.setText(localName);

        TextView selectedMarker = (TextView) findViewById(R.id.local_name);

        selectedMarker.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        //selectedMarker.setTextColor(Color.parseColor("#000000"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu(localName);
            }
        });
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(this, "Choose a marker on the map", Toast.LENGTH_LONG).show();
    }

    @Override
    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
        String title = marker.getTitle();
        int crow = 0;
        String cCrow = null;
        int waitTime = 0;

        View row = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

        TextView name = (TextView) row.findViewById(R.id.name);

        TextView address = (TextView) row.findViewById(R.id.address);

        TextView crowding = (TextView) row.findViewById(R.id.crowding);
        TextView wait = row.findViewById(R.id.waiting_time);

        for (int i=0;i<markers.size();i++)
        {
            if (title.equals(markers.get(i).getName())) {
                try
                {   crow = (int) crowdingList.get(markers.get(i).getId()-1).getSum();   }
                catch (IndexOutOfBoundsException e)
                {   crow = 0;   }
                cCrow = setCrowdingInfoWinfow(crow);
                Log.i("val crow", "val infow " + markers.get(i).getCrowding());
                //Log.i("val info", "lettura " + crowdingList.get(markers.get(i).getId()-1).getSum());

                //SETTING TEXT/INFOWINDOW COLOR
                String cName = markers.get(i).getName() + " ";
                String cAdd = markers.get(i).getAddress() + " ";

                //SETTING TEXTVIEW CONTENT
                name = setTextAndColor(name, cName, " Name");

                address = setTextAndColor(address, cAdd, " Address");

                setTextAndColor(crowding, cCrow, " Crowding");

                waitTime = getWaitInfoWindow(crow);
                setTextAndColor(wait, String.valueOf(waitTime), " Waiting time");

                row.setBackgroundColor(Color.parseColor("#fbb324"));
            }
        }

        return row;
    }

    @Override
    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
        return null;
    } //FINE getInfoContent


    //ANDIAMO A DEFINIRE LA PROCEDURA PER APRIRE IL MENU
    public void openMenu(String localName) {
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("name", localName);

        startActivity(intent);

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    public TextView setTextAndColor(TextView tv, String str, String att)
    {
        if(att.equals(" Waiting time"))
            tv.setText(att + ": " + str + " min");
        else
            tv.setText(att + ": " + str);

        tv.setTextColor(Color.parseColor("#000000"));

        return tv;
    }

    public int setCustomIcon(String type)
    {
        int path = R.drawable.ic_cocktail;         //INIZIALIZATION TO AVOID RUNTIME ERRORS

        switch (type)
        {
            case "pub":
                path = R.drawable.ic_beer;
                break;
            case "cocktail bar":
                path = R.drawable.ic_cocktail;
                break;
            case "wine bar":
                path = R.drawable.ic_wine;
                break;
        }
        return path;
    }

    public String setCrowdingInfoWinfow(int crow)
    {
        String cCrow;
        if(crow >= 25)
            cCrow = "High";
        else if(crow<25 && crow > 7)
                cCrow = "Medium";
        else
            cCrow = "Low";

        return cCrow;
    }

    public int getWaitInfoWindow(int crow)
    {
        int wait = 0;

        if(crow <=2)
            wait = 0;
        else if(crow >2 && crow <=7)
            wait = 2;
        else if(crow >7 && crow <=12)
            wait = 5;
        else if(crow >12 && crow <=18)
            wait = 10;
        else if(crow >18 && crow <=25)
            wait = 15;
        else
            wait = 20;

        return wait;
    }

}