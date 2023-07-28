package com.example.mediguardianbasic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoseMainActivity extends AppCompatActivity {
    FloatingActionButton addDosageButton;
    TextView doseTitle;
    RecyclerView dosageRecyclerView;
    List<DosageDataClass> dosageDataList;
    DatabaseReference databaseReference;
    ValueEventListener dosageEventListener;
    SearchView dosageSearchView;
    DetailAdapter dosageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dose_main);

        addDosageButton = findViewById(R.id.addDosageButton);
        doseTitle = findViewById(R.id.doseTitle);
        dosageRecyclerView = findViewById(R.id.dosageRecyclerView);
        dosageSearchView = findViewById(R.id.dosageSearch);
        dosageSearchView.clearFocus();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DoseMainActivity.this, 1);
        dosageRecyclerView.setLayoutManager(gridLayoutManager);
        dosageDataList = new ArrayList<>();
        dosageAdapter = new DetailAdapter(DoseMainActivity.this, dosageDataList);
        dosageRecyclerView.setAdapter(dosageAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            String medicineName = intent.getStringExtra("Medicine Name");
            int compartmentNumber = intent.getIntExtra("Compartment #", 0);

            doseTitle.setText(medicineName);

            String ProductID = getString(R.string.product_id);
            String medicineID = ProductID + "_c" + String.valueOf(compartmentNumber);

            AlertDialog.Builder builder = new AlertDialog.Builder(DoseMainActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(ProductID).child(medicineID).child("Reminders");
            dialog.show();

            dosageEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dosageDataList.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    DosageDataClass dosageDataClass = itemSnapshot.getValue(DosageDataClass.class);
                    dosageDataList.add(dosageDataClass);
                }
                dosageAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

            dosageSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchList(newText);
                    return true;
                }
            });

            addDosageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DoseMainActivity.this, AddDosageActivity.class)
                                    .putExtra("Compartment #", compartmentNumber);
                    startActivity(intent);
                }
            });
        }
    }
    public void searchList(String text){
        ArrayList<DosageDataClass> searchList = new ArrayList<>();
        for(DosageDataClass dosageDataClass: dosageDataList){
            if(dosageDataClass.getDataFrequency().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dosageDataClass);
            }
        }
        dosageAdapter.searchDataList(searchList);
    }
}
