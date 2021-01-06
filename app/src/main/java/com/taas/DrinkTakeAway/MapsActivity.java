package com.taas.DrinkTakeAway;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private static final String EXTRA_TEXT = "com.taas.DrinkTakeAway.MapsActivity.EXTRA_TEXT";

    ArrayList<Marker> markers = new ArrayList<>();
    private RequestQueue mQueue;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mQueue = Volley.newRequestQueue(this);

        button = (Button) findViewById(R.id.menu_button);
        LinearLayout li = (LinearLayout) findViewById(R.id.bottom);
        li.setBackgroundColor(Color.parseColor("#fbb324"));

    }       //OnCreate ends

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

                String name, type, add;
                LatLng pos;
                double lat, lon;

                //CREAZIONE MARKER
                for(int i=0; i<listOfMarker.size();i++)
                {
                    lat = listOfMarker.get(i).getLat();
                    lon = listOfMarker.get(i).getLon();
                    type = listOfMarker.get(i).getType();

                    pos = new LatLng(lat, lon);
                    mMap.addMarker(new MarkerOptions().position(pos).title(listOfMarker.get(i).getName())
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), setCustomIcon(type))));

                } //Creation markers loo ends
                markers = listOfMarker;
            }
        });

        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnMapClickListener(this);
        setMapCenter();

    } //OnMapReady ends

    //Setting interface for Callback
    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(ArrayList<Marker> listOfMarker);
    }

    private void jsonParseLocali(final VolleyResponseListener listener)
    {

        //String url = "http://10.0.2.2:1111/api/v1/locale";
        //String url = "http://192.168.1.157:1111/api/v1/locale";
        String url = "http://192.168.1.90:1111/api/v1/getAllLocals";
        ArrayList<Marker> al = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        try {
                            LatLng pos;
                            for(int i=0; i< response.length(); i++)
                            {
                                Marker m = new Marker(0,"name", "type", "address", 0.0, 0.0);
                                JSONObject locale = response.getJSONObject(i);

                                //Setting the Marker class object
                                m.setId(locale.getInt("id"));
                                m.setName(locale.getString("name"));
                                m.setAddress(locale.getString("address"));
                                m.setType(locale.getString("type"));
                                m.setLat(locale.getDouble("lat"));
                                m.setLon(locale.getDouble("lon"));
                                al.add(m);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Setting the Callback listener
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

    //SETTING FOCUS TO CITY CENTER
    private void setMapCenter() {
        float zoomLevel = 16.0f;
        LatLng mapCenter;
        //Mole Antonelliana 45.0690113  7.6910275
        //Porta Nuova/San Salvario 45.062055    7.6763373
        //Piazza Vittorio Veneto
        double latCenter = 45.0647992, lngCenter = 7.6930788;
        mapCenter = new LatLng(latCenter, lngCenter);
        mMap.moveCamera((CameraUpdateFactory.newLatLngZoom(mapCenter, zoomLevel)));
    }

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
        tv.setText(att + ": " + str);
        tv.setTextColor(Color.parseColor("#000000"));

        return tv;
    }

    public int setCustomIcon(String type)
    {
        int path = R.drawable.ic_cocktail;         //It needs initialization to avoid runtime error

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

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
        TextView selectedMarkerLocalName = findViewById(R.id.local_name);

        final String localName = marker.getTitle();
        selectedMarkerLocalName.setText(localName);

        TextView selectedMarker = (TextView) findViewById(R.id.local_name);

        selectedMarker.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        selectedMarker.setTextColor(Color.parseColor("#000000"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu(localName);
            }
        });

        return false;
    }

    @Override
    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
        String title = marker.getTitle();

        View row = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

        for (int i=0;i<markers.size();i++)
        {
            if(title.equals(markers.get(i).getName()))
            {

                TextView name = (TextView) row.findViewById(R.id.name);
                TextView address = (TextView) row.findViewById(R.id.address);
                TextView crowding = (TextView) row.findViewById(R.id.crowding);

                String cName = markers.get(i).getName() + " ";
                String cAdd = markers.get(i).getAddress() + " ";
                String cCrow = "High/Medium/Low ";


                name = setTextAndColor(name, cName, " Name");
                address = setTextAndColor(address, cAdd, " Address");
                setTextAndColor(crowding, cCrow, " Crowding");

                row.setBackgroundColor(Color.parseColor("#fbb324"));

            }
        }
        return row;
    }

    @Override
    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
        return null;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(this, "Choose a marker on the map", Toast.LENGTH_LONG).show();
    }
}