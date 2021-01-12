package com.taas.DrinkTakeAway.Interface;


import com.taas.DrinkTakeAway.CrowdingDto;

import java.util.ArrayList;

public interface CrowdingVolleyResponseListener {
    void onError(String message);

    void onResponse(ArrayList<CrowdingDto> drinkToDoQuantity);
}
