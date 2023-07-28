package com.example.mediguardianbasic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoseDetailActivity extends AppCompatActivity {

    TextView addDosageFrequency, addDosageDose, addCompartment;
    FloatingActionButton doseDeleteButton, doseEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dose_detail);

        addDosageFrequency = findViewById(R.id.addDosageFrequency);
        addDosageDose = findViewById(R.id.addDosageDose);
        addCompartment = findViewById(R.id.addCompartment);
        doseDeleteButton = findViewById(R.id.doseDeleteButton);
        doseEditButton = findViewById(R.id.doseEditButton);

        Intent intent = getIntent();
        if (intent != null) {
            String frequency = intent.getStringExtra("Frequency");
            float tablets_per_reminder = intent.getFloatExtra("Tablets per reminder", 0);
            int compartmentNumber = intent.getIntExtra("Compartment #", 0);

            addDosageFrequency.setText("Time of Dose: " + frequency);
            addDosageDose.setText("Dose: " + String.valueOf(tablets_per_reminder) + " Tablet(s) per reminder");
            addCompartment.setText("Compartment " +  String.valueOf(compartmentNumber));

            String ProductID = getString(R.string.product_id);
            String medicineID = ProductID + "_c" + String.valueOf(compartmentNumber);
            doseDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(ProductID).child(medicineID).child("Reminders");
                    reference.child(frequency).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DoseDetailActivity.this, "Dose Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });

            doseEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DoseDetailActivity.this, UpdateDoseActivity.class)
                            .putExtra("Frequency", frequency)
                            .putExtra("Tablets per reminder", tablets_per_reminder)
                            .putExtra("Compartment #", compartmentNumber);
                    startActivity(intent);
                }
            });
        }
    }
}