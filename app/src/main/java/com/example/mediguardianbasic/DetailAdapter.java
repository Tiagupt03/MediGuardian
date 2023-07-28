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

public class DetailAdapter extends RecyclerView.Adapter<MyDosageViewHolder> {

    private Context context;
    private List<DosageDataClass> dosageDataList;
    private int maxLength = 30;

    public DetailAdapter(Context context, List<DosageDataClass> dosageDataList) {
        this.context = context;
        this.dosageDataList = dosageDataList;
    }

    @NonNull
    @Override
    public MyDosageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dosage_recycler_item, parent, false);
        return new MyDosageViewHolder(view);}


    @Override
    public void onBindViewHolder(@NonNull MyDosageViewHolder holder, final int position) {
        DosageDataClass data = dosageDataList.get(position);
        String frequency = data.getDataFrequency();
        Float dosage = data.getDataDosage();
        int compartment_number = data.getDataCompartment();
        //String truncatedFrequency = TextUtils.ellipsize(frequency, holder.recFrequency.getPaint(), max_len, TextUtils.TruncateAt.END).toString();
        String truncatedFrequency = frequency;
        if (frequency.length() > maxLength) {
            truncatedFrequency = frequency.substring(0, maxLength) + "...";
        }

        holder.dosageRecCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DoseDetailActivity.class);
                intent.putExtra("Frequency", frequency);
                intent.putExtra("Tablets per reminder", dosage);
                intent.putExtra("Compartment #", compartment_number);
                context.startActivity(intent);
            }
        });
        holder.recFrequency.setText(truncatedFrequency);
        holder.recDosage.setText(dosage.toString() + " tablet(s) per reminder");
    }


    @Override
    public int getItemCount() {
        return dosageDataList.size();
    }

    public void searchDataList(ArrayList<DosageDataClass> searchList){
        dosageDataList = searchList;
        notifyDataSetChanged();
    }
}

class MyDosageViewHolder extends RecyclerView.ViewHolder{
    TextView recFrequency, recDosage;
    CardView dosageRecCard;

    public MyDosageViewHolder(@NonNull View itemView) {
        super(itemView);
        dosageRecCard = itemView.findViewById(R.id.dosageRecCard);
        recFrequency = itemView.findViewById(R.id.recFrequency);
        recDosage = itemView.findViewById(R.id.recDosage);
    }
}