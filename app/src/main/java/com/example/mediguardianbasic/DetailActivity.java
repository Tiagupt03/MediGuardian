package com.example.mediguardianbasic;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailActivity extends AppCompatActivity {

    TextView detailCompartment, detailTitle, detailQuantity, detailStartTimings;
    FloatingActionButton deleteButton, editButton;

    Button doseManagementButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailCompartment = findViewById(R.id.detailCompartment);
        detailTitle = findViewById(R.id.detailTitle);
        detailQuantity = findViewById(R.id.detailQuantity);
        detailStartTimings = findViewById(R.id.detailStartTimings);
        doseManagementButton = findViewById(R.id.doseManagementButton);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);

        Intent intent = getIntent();
        if (intent != null) {
            String medicineName = intent.getStringExtra("Medicine Name");
            int tabletQuantity = intent.getIntExtra("Tablet Quantity in Compartment", 0);
            int compartmentNumber = intent.getIntExtra("Compartment #", 0);
            String startTimings = intent.getStringExtra("Medicine Start");

            detailCompartment.setText("Compartment " + compartmentNumber);
            detailTitle.setText("Medicine Name: " + medicineName);
            detailQuantity.setText("Tablet Quantity in Compartment: " + String.valueOf(tabletQuantity) + " tablet(s)");
            detailStartTimings.setText("Medicine Start Date: " + startTimings);

            String ProductID = getString(R.string.product_id);
            String medicineID = ProductID + "_c" + String.valueOf(compartmentNumber);

            doseManagementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DetailActivity.this, DoseMainActivity.class)
                            .putExtra("Medicine Name", medicineName)
                            .putExtra("Compartment #", compartmentNumber);
                    startActivity(intent);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(ProductID);
                    reference.child(medicineID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DetailActivity.this, "Medicine Deleted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                            .putExtra("Medicine Name", medicineName)
                            .putExtra("Tablet Quantity in Compartment", tabletQuantity)
                            .putExtra("Compartment #", compartmentNumber);
                    startActivity(intent);
                }
            });
        }
    }
}