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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateActivity extends AppCompatActivity {

    Button updateButton;
    EditText  updateTabletQuantity;
    TextView updateCompartmentNumber, updateMedicineName;
    DatabaseReference databaseReference;
    String ProductID;
    String medicineID;
    Integer weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Intent intent = getIntent();
        if (intent != null) {
            String medicineName = intent.getStringExtra("Medicine Name");
            int compartmentNumber = intent.getIntExtra("Compartment #", 0);
            int tabletquantity = intent.getIntExtra("Tablet Quantity in Compartment", 0);

            ProductID = getString(R.string.product_id);
            medicineID = ProductID + "_c" + compartmentNumber;

            updateMedicineName = findViewById(R.id.Updatename);
            updateCompartmentNumber = findViewById(R.id.UpdateCompartment);
            updateTabletQuantity = findViewById(R.id.Updatequantity);
            updateButton = findViewById(R.id.updateButton);

            updateMedicineName.setText(medicineName);
            updateCompartmentNumber.setText(String.valueOf(compartmentNumber));

            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(ProductID).child(medicineID);

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateData();
                }
            });
        }
    }

    public void updateData(){
        String quantityString = updateTabletQuantity.getText().toString();

        if (quantityString.isEmpty()) {
            Toast.makeText(UpdateActivity.this, "Quantity field can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.valueOf(quantityString);
        } catch (NumberFormatException e) {
            Toast.makeText(UpdateActivity.this, "Please enter a valid value for tablet quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if(quantity <= 0){
            Toast.makeText(UpdateActivity.this, "Please add valid number of tablets", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the original quantity from the Firebase Realtime Database
        databaseReference.child("dataQuantity").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int originalQuantity = dataSnapshot.getValue(Integer.class);

                    // Update the corresponding fields in the Firebase Realtime Database
                    databaseReference.child("dataQuantity").setValue(quantity + originalQuantity).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UpdateActivity.this, "Medicine Refilled", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UpdateActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(UpdateActivity.this, "Original quantity not found in the database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}