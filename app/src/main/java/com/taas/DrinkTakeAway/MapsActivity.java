package com.taas.DrinkTakeAway;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String EXTRA_TEXT = "com.taas.DrinkTakeAway.MapsActivity.EXTRA_TEXT";

    ArrayList<Marker> markers = new ArrayList<>();
    int cont = 0;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        button = (Button) findViewById(R.id.menu_button);
        LinearLayout li = (LinearLayout) findViewById(R.id.bottom);
        li.setBackgroundColor(Color.parseColor("#fbb324"));

        //LETTURA JSON
        InputStream is = getResources().openRawResource(R.raw.marker);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            int n;
            while((n = reader.read(buffer)) !=-1)
            {
                writer.write(buffer, 0, n);
            } //FINE WHILE

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String marker_array = writer.toString();

        //FETCH JSON
        try {

            JSONObject json = new JSONObject(marker_array);
            JSONArray jArray =  json.getJSONArray("markers");
            for (int i = 0;i<jArray.length();i++)
            {
                JSONObject obj = jArray.getJSONObject(i);

                Marker m = new Marker("name", "type", "address", 0.0, 0.1);
                m.setName(obj.getString("name"));
                m.setType(obj.getString("type"));
                m.setAddress(obj.getString("address"));
                m.setLat(obj.getDouble("lat"));
                m.setLon(obj.getDouble("lon"));
                markers.add(m);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String name, type, add;
        LatLng pos;
        double lat;
        double lon;

        //CREAZIONE MARKER
        for (Marker m: markers)
        {
            lat = m.getLat();
            lon = m.getLon();

            type = m.getType();

            if(mMap!=null)
            {
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker)
                    {

                        View row = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                        //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID ADDRESS
                        TextView name = (TextView) row.findViewById(R.id.name);
                        //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID ADDRESS
                        TextView address = (TextView) row.findViewById(R.id.address);
                        //PRENDIAMO DAL LAYOUT LA TEXT VIEW CON ID CROWDING
                        TextView crowding = (TextView) row.findViewById(R.id.crowding);

                        //SETTING COLORI TESTO/INFOWINDOW
                        String cName = markers.get(cont).getName() + " ";
                        String cAdd = markers.get(cont).getAddress() + " ";
                        String cCrow = "High/Medium/Low ";

                        //ASSEGNAZIONE TESTO DA VISUALIZZARE
                        name = setTextAndColor(name, cName, " Name");

                        address = setTextAndColor(address, cAdd, " Address");

                        setTextAndColor(crowding, cCrow, " Crowding");

                        cont++;

                        row.setBackgroundColor(Color.parseColor("#fbb324"));

                        return row;

                    }

                    @Override
                    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {

                        return null;

                    }  //FINE getInfoContent

                }); //FINE setInfoWindowAdapter

            }  //FINE IF

            pos = new LatLng(lat, lon);
            //mMap.addMarker(new MarkerOptions().position(pos).title("Name: " + m.getName() + "\nType: " + m.getType() + "\nAddress: " + m.getAddress()));
            mMap.addMarker(new MarkerOptions().position(pos).title(m.getName())
                    .icon(bitmapDescriptorFromVector(getApplicationContext(), setCustomIcon(type))));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {


                    TextView selectedMarker = (TextView) findViewById(R.id.local_name);
                    selectedMarker.setText(markers.get(cont).getName());
                    selectedMarker.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    selectedMarker.setTextColor(Color.parseColor("#000000"));

                    final String localName = (String) selectedMarker.getText();

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openMenu(localName);
                        }
                    });

                    return false;
                }
            });

        } //FINE FOR CREAZIONE MARKER

    } //FINE OnMapReady

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
        int path = R.drawable.ic_cocktail;         //INIZIALIZZAZIONE PER NON AVERE ERRORI A RUNTIME

        switch (type)
        {
            case "Pub":
                path = R.drawable.ic_beer;
                break;
            case "Cocktail bar":
                path = R.drawable.ic_cocktail;
                break;
            case "Wine Bar":
                path = R.drawable.ic_wine;
                break;
        }
        return path;
    }

}