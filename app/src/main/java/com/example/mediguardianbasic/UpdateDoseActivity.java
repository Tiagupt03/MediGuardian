package com.example.mediguardianbasic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UpdateDoseActivity extends AppCompatActivity {
    DatabaseReference databaseReference;

    String ProductID;
    String medicineID;
    Button UpdateDosageButton;
    EditText UpdateDosage;
    TextView UpdateFrequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dose);
        Intent intent = getIntent();

        if (intent != null) {
            String frequency = intent.getStringExtra("Frequency");
            int compartmentNumber = intent.getIntExtra("Compartment #", 0);
            float dosage = intent.getFloatExtra("Tablets per reminder", 0);

            ProductID = getString(R.string.product_id);
            medicineID = ProductID + "_c" + compartmentNumber;

            UpdateFrequency = findViewById(R.id.UpdateFrequency);
            UpdateDosage = findViewById(R.id.UpdateDosage);
            UpdateDosageButton = findViewById(R.id.UpdateDosageButton);

            UpdateFrequency.setText(frequency);
            UpdateDosage.setText(String.valueOf(dosage));

            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(ProductID).child(medicineID).child("Reminders");

            UpdateDosageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateData();
                }
            });
        }
    }

    public void updateData() {
        String frequency = UpdateFrequency.getText().toString();
        String dosagestring = UpdateDosage.getText().toString();
        float dosage = Float.parseFloat(dosagestring);

        //Check Validity same as AddDosageActivity
        if (dosagestring.isEmpty()) {
            Toast.makeText(UpdateDoseActivity.this, "Dose can't be empty", Toast.LENGTH_SHORT).show();
            return;}
        if(dosage <= 0){
            Toast.makeText(UpdateDoseActivity.this, "Invalid Dose added", Toast.LENGTH_SHORT).show();
            return;}

        // Update the corresponding fields in the Firebase Realtime Database
        databaseReference.child(frequency).child("dataDosage").setValue(dosage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UpdateDoseActivity.this, "Dose Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdateDoseActivity.this, MainActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateDoseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}