package com.example.flixster.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {

    List<String> items;
    public CreditsAdapter(Context context, List<String> items){
        this.items = items;
    }


    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("CreditsAdapter", "onCreateViewHolder");
        // Use layout inflator to inflate a view

        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent, false);

        // wrap it inside a View Holder and return it
        return new ViewHolder(todoView);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Involves populating the data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("CreditsAdapter", "onBindViewHolder" + position);

        // Grab the item at the position
        String item = items.get(position);
        //Bind the item into the specified view holder
        holder.bind(item);


    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(android.R.id.text1);
            tvItem.setTextSize(14);
            tvItem.setLineSpacing(0, -2);

        }

        public void bind(String item) {
            tvItem.setText(item);
        }

    }

}
