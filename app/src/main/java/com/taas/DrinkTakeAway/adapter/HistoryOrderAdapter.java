package com.taas.DrinkTakeAway.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taas.DrinkTakeAway.R;
import com.taas.DrinkTakeAway.models.HistoryOrderEntry;

import java.util.List;

public class HistoryOrderAdapter extends
        RecyclerView.Adapter<HistoryOrderAdapter.ViewHolder> {

    private HistoryOrderAdapter.onDrinkListenerCart mOnDrinkListenerCart;
    private List<HistoryOrderEntry> mHistoryOrderEntry;

    // Pass in the contact array into the constructor
    public HistoryOrderAdapter(List<HistoryOrderEntry> order, HistoryOrderAdapter.onDrinkListenerCart mOnDrinkListenerCart) {
        mHistoryOrderEntry = order;
        this.mOnDrinkListenerCart = mOnDrinkListenerCart;

    }

    @NonNull
    @Override
    public HistoryOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View drinkView = inflater.inflate(R.layout.item_history_order, parent, false);

        // Return a new holder instance
        HistoryOrderAdapter.ViewHolder viewHolder = new HistoryOrderAdapter.ViewHolder(drinkView, mOnDrinkListenerCart);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryOrderAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        HistoryOrderEntry HistoryOrderEntry = mHistoryOrderEntry.get(position);

        TextView timestampTextView = holder.timestampTextView;
        TextView localNameTextView = holder.localNameTextView;
        TextView orderNumberTextView = holder.orderNumberTextView;
        TextView statusNumberTextView = holder.statusNumberTextView;


        if (position != 0){
            HistoryOrderEntry HistoryOrderEntryPre = mHistoryOrderEntry.get(position - 1);
            if (HistoryOrderEntry.getOrderNumber() != HistoryOrderEntryPre.getOrderNumber()){
                timestampTextView.setText("\n" +HistoryOrderEntry.getTimestamp().substring(0,10)+ "\n");
                localNameTextView.setText("\n" +HistoryOrderEntry.getLocalName()+ "\n");
                orderNumberTextView.setText("\n Order#" + Integer.toString(HistoryOrderEntry.getOrderNumber())+ "\n");
                statusNumberTextView.setText("\n" +HistoryOrderEntry.getStatus()+ "\n");
            }
        }
        else {
            timestampTextView.setText("\n" +HistoryOrderEntry.getTimestamp().substring(0,10)+ "\n");
            localNameTextView.setText("\n" + HistoryOrderEntry.getLocalName() + "\n");
            orderNumberTextView.setText("\n Order#" + Integer.toString(HistoryOrderEntry.getOrderNumber()) + "\n");
            statusNumberTextView.setText("\n" +HistoryOrderEntry.getStatus()+ "\n");

        }

        timestampTextView.setTypeface(null, Typeface.BOLD);
        localNameTextView.setTypeface(null, Typeface.BOLD);
        orderNumberTextView.setTypeface(null, Typeface.BOLD);

        // Set item views based on your views and data model

        TextView textView = holder.nameTextView;
        textView.setText(HistoryOrderEntry.getDrinkName());

        TextView textView1 = holder.priceTextView;
        textView1.setText(String.valueOf(HistoryOrderEntry.getPrice()) + " €");

        TextView textViewNum = holder.numerosityTextView;
        textViewNum.setText(String.valueOf(HistoryOrderEntry.getNumerosity()));

        holder.setIsRecyclable(false);
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView, priceTextView, numerosityTextView, localNameTextView, timestampTextView, orderNumberTextView, statusNumberTextView;
        HistoryOrderAdapter.onDrinkListenerCart mOnDrinkListenerCart;



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, onDrinkListenerCart onDrinkListenerCart) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.drink_name_cart_history);
            priceTextView = (TextView) itemView.findViewById(R.id.drink_price_cart_history);
            numerosityTextView = (TextView) itemView.findViewById(R.id.drink_numerosity_history);
            localNameTextView = (TextView) itemView.findViewById(R.id.local_name_history);
            timestampTextView = (TextView) itemView.findViewById(R.id.timestamp_history);
            orderNumberTextView = (TextView) itemView.findViewById(R.id.order_num_history);
            statusNumberTextView = (TextView) itemView.findViewById(R.id.status_history);



            this.mOnDrinkListenerCart = onDrinkListenerCart;

            //itemView.setOnClickListener(this);
        }
    }


    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mHistoryOrderEntry.size();
    }


    public interface onDrinkListenerCart{
        //void onDrinkClickGetName (String name);
        void onMinusButtonClickGetDrink(String name, String price, String numerosity, int pos);
    }
}