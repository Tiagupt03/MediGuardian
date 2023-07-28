package com.example.mediguardianbasic;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import java.lang.String;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;
    private int maxLength = 30;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        DataClass data = dataList.get(position);
        String medicine_name = data.getDataName();
        Integer tablet_quantity = data.getDataQuantity();
        Integer compartment_number = data.getDataCompartment();
        String startDate = data.getDataStartDate();
        String startTime = data.getDataStartTime();
        String medicine_start = startDate + " " + startTime;

        // Check the length of the medicine name and truncate if necessary
        String truncatedMedicineName = medicine_name;
        if (medicine_name.length() > maxLength) {
            truncatedMedicineName = medicine_name.substring(0, maxLength) + "...";
        }

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Medicine Name", medicine_name);
                intent.putExtra("Tablet Quantity in Compartment", tablet_quantity);
                intent.putExtra("Compartment #", compartment_number);
                intent.putExtra("Medicine Start", medicine_start);
                context.startActivity(intent);
            }
        });
        holder.recTitle.setText(truncatedMedicineName); // Use the truncated medicine name
        holder.recQuantity.setText(tablet_quantity.toString() + " tablet(s) left");
    }



    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{
    TextView recTitle, recQuantity;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recCard = itemView.findViewById(R.id.recCard);
        recQuantity = itemView.findViewById(R.id.recQuantity);
        recTitle = itemView.findViewById(R.id.recTitle);
    }
}