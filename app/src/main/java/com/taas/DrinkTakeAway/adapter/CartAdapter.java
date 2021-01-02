package com.taas.DrinkTakeAway.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taas.DrinkTakeAway.R;
import com.taas.DrinkTakeAway.models.EntryOrdine;
import com.taas.DrinkTakeAway.models.Menu;

import java.util.List;

import static android.content.ContentValues.TAG;

public class CartAdapter extends
        RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private CartAdapter.onDrinkListenerCart mOnDrinkListenerCart;
    private List<EntryOrdine> mEntryOrdine;

    // Pass in the contact array into the constructor
    public CartAdapter(List<EntryOrdine> ordine,  CartAdapter.onDrinkListenerCart mOnDrinkListenerCart) {
        mEntryOrdine = ordine;
        this.mOnDrinkListenerCart = mOnDrinkListenerCart;

    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View drinkView = inflater.inflate(R.layout.item_cart, parent, false);

        // Return a new holder instance
        CartAdapter.ViewHolder viewHolder = new CartAdapter.ViewHolder(drinkView, mOnDrinkListenerCart);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        EntryOrdine entryOrdine = mEntryOrdine.get(position);
        // Set item views based on your views and data model

        TextView textView = holder.nameTextView;
        textView.setText(entryOrdine.getDrinkName());

        TextView textView1 = holder.priceTextView;
        textView1.setText(String.valueOf(entryOrdine.getPrice()) + " €");

        TextView textViewNum = holder.numerosityTextView;
        textViewNum.setText(String.valueOf(entryOrdine.getNumerosity()));

        Button button = holder.minusButton;

        //button.setText(contact.isOnline() ? "Message" : "Offline");
        //button.setEnabled(contact.isOnline());
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView, priceTextView, numerosityTextView;
        public Button minusButton;
        CartAdapter.onDrinkListenerCart mOnDrinkListenerCart;



        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, onDrinkListenerCart onDrinkListenerCart) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.drink_name_cart);
            priceTextView = (TextView) itemView.findViewById(R.id.drink_price_cart);
            numerosityTextView = (TextView) itemView.findViewById(R.id.drink_numerosity);

            minusButton = (Button) itemView.findViewById(R.id.minus_button);

            this.mOnDrinkListenerCart = onDrinkListenerCart;

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnDrinkListenerCart.onMinusButtonClickGetDrink(nameTextView.getText().toString(), priceTextView.getText().toString(), numerosityTextView.getText().toString() ,getAdapterPosition());
                }
            });

            //itemView.setOnClickListener(this);
        }
    }


    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mEntryOrdine.size();
    }


    public interface onDrinkListenerCart{
        //void onDrinkClickGetName (String name);
        void onMinusButtonClickGetDrink(String name, String price, String numerosity, int pos);
    }
}