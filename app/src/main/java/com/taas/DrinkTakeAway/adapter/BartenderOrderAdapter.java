package com.taas.DrinkTakeAway.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taas.DrinkTakeAway.R;
import com.taas.DrinkTakeAway.models.BartenderOrderEntity;
import com.taas.DrinkTakeAway.models.HistoryOrderEntry;

import org.w3c.dom.Text;

import java.util.List;

public class BartenderOrderAdapter extends
        RecyclerView.Adapter<BartenderOrderAdapter.ViewHolder> {

    private BartenderOrderAdapter.onDrinkListenerCart mOnDrinkListenerBartender;
    private List<BartenderOrderEntity> mBartenderOrderEntry;

    String localName;

    // Pass in the contact array into the constructor
    public BartenderOrderAdapter(List<BartenderOrderEntity> order, BartenderOrderAdapter.onDrinkListenerCart mOnDrinkListenerBartender, String localName) {
        mBartenderOrderEntry = order;
        this.mOnDrinkListenerBartender = mOnDrinkListenerBartender;
        this.localName = localName;

    }

    @NonNull
    @Override
    public BartenderOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View drinkView = inflater.inflate(R.layout.item_history_order_db2, parent, false);

        // Return a new holder instance
        BartenderOrderAdapter.ViewHolder viewHolder = new BartenderOrderAdapter.ViewHolder(drinkView, mOnDrinkListenerBartender);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BartenderOrderAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        BartenderOrderEntity bartenderOrderEntity = mBartenderOrderEntry.get(position);

        TextView orderNumberTextView = holder.orderNumberTextView;
        TextView orderUserEmail = holder.emailTextView;
        Button buttonCompleted = holder.buttonCompleted;
        Button buttonPreparing = holder.buttonPreparing;

        if (position != 0){
            BartenderOrderEntity bartenderOrderEntityPre = mBartenderOrderEntry.get(position - 1);
            if (bartenderOrderEntity.getOrderNumber() != bartenderOrderEntityPre.getOrderNumber()){
                orderNumberTextView.setText("\n Order#" + Integer.toString(bartenderOrderEntity.getOrderNumber())+ "\n");
                buttonCompleted.setVisibility(View.VISIBLE);
                buttonPreparing.setVisibility(View.VISIBLE);
                orderNumberTextView.setVisibility(View.VISIBLE);
                orderUserEmail.setVisibility(View.VISIBLE);
                orderUserEmail.setText(bartenderOrderEntity.getEmail());
            }
        }
        else {
            orderNumberTextView.setText("\n Order#" + Integer.toString(bartenderOrderEntity.getOrderNumber()) + "\n");
            buttonCompleted.setVisibility(View.VISIBLE);
            buttonPreparing.setVisibility(View.VISIBLE);
            orderNumberTextView.setVisibility(View.VISIBLE);
            orderUserEmail.setVisibility(View.VISIBLE);
            orderUserEmail.setText(bartenderOrderEntity.getEmail());

        }

        orderNumberTextView.setTypeface(null, Typeface.BOLD);

        // Set item views based on your views and data model

        TextView textView = holder.nameTextView;
        textView.setText(bartenderOrderEntity.getDrinkName());

        TextView textViewNum = holder.numerosityTextView;
        textViewNum.setText(String.valueOf(bartenderOrderEntity.getNumerosity()));

        holder.setIsRecyclable(false);
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView, numerosityTextView, orderNumberTextView, emailTextView;
        public Button buttonCompleted, buttonPreparing;
        BartenderOrderAdapter.onDrinkListenerCart mOnDrinkListenerBartender;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView, onDrinkListenerCart onDrinkListenerCart) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ViewHolder context = this;

            nameTextView = (TextView) itemView.findViewById(R.id.drink_name_cart_history_db2);
            numerosityTextView = (TextView) itemView.findViewById(R.id.drink_numerosity_history_db2);
            orderNumberTextView = (TextView) itemView.findViewById(R.id.order_num_history_db2);
            emailTextView = (TextView) itemView.findViewById(R.id.order_email_history_db2);


            buttonCompleted = (Button) itemView.findViewById(R.id.completed_button);
            buttonPreparing = (Button) itemView.findViewById(R.id.preparing_button);

            this.mOnDrinkListenerBartender = onDrinkListenerCart;

            //itemView.setOnClickListener(this);

            buttonCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnDrinkListenerBartender.onCompletedButtonClick(orderNumberTextView.getText().toString().replaceAll("\n Order#", "").replaceAll("\n", ""), localName, emailTextView.getText().toString(), getAdapterPosition());
                   // mOnDrinkListenerBartender.saveOrdertoSecondService(localName, nameTextView.getText().toString(), orderNumberTextView.getText().toString(), emailTextView.getText().toString(),numerosityTextView.getText().toString());
                }
            });
            buttonPreparing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnDrinkListenerBartender.onPreparingButtonClick(orderNumberTextView.getText().toString().replaceAll("\n Order#", "").replaceAll("\n", ""));
                }
            });
        }
    }


    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mBartenderOrderEntry.size();
    }


    public interface onDrinkListenerCart{
        //void onDrinkClickGetName (String name);
        //void onMinusButtonClickGetDrink(String name, String price, String numerosity, int pos);
        void onCompletedButtonClick(String orderNumber, String localName, String email, int pos);
        void onPreparingButtonClick(String orderNumber);
    }

}