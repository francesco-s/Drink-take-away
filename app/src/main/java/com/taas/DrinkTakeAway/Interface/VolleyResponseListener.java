package com.taas.DrinkTakeAway.Interface;
import com.taas.DrinkTakeAway.Marker;

import java.util.ArrayList;

public interface VolleyResponseListener {
    void onError(String message);

    void onResponse(ArrayList<Marker> listOfMarker);
}
