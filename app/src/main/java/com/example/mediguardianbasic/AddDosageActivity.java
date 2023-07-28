package com.example.mediguardianbasic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import androidx.annotation.NonNull;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.jetbrains.annotations.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.text.DateFormat;

public class AddDosageActivity extends AppCompatActivity {
    Button DoseSaveButton, frequencyTimeButton;
    TextView UploadTabletPerDose;
    FirebaseDatabase database;
    int hour, minute;
    DatabaseReference reference;
    ArrayList<String> dosage_options;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dosage);

        Intent intent = getIntent();
        if (intent != null) {
            int compartmentnumber = intent.getIntExtra("Compartment #", 0);
            String compartment = String.valueOf(compartmentnumber);

            frequencyTimeButton = findViewById(R.id.frequency_time);
            UploadTabletPerDose = findViewById(R.id.UploadTabletPerDose);
            DoseSaveButton = findViewById(R.id.DoseSaveButton);
            dosage_options = new ArrayList<>();

            dosage_options.add("0.25");
            dosage_options.add("0.5");
            dosage_options.add("0.75");
            dosage_options.add("1");
            dosage_options.add("2");
            dosage_options.add("3");
            dosage_options.add("4");
            dosage_options.add("5");
            dosage_options.add("6");
            dosage_options.add("7");
            dosage_options.add("8");
            dosage_options.add("9");
            dosage_options.add("10");
            dosage_options.add(0, "Select Dosage");
            UploadTabletPerDose.setText("Select Dosage");

            UploadTabletPerDose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new Dialog(AddDosageActivity.this);
                    dialog.setContentView(R.layout.dialog_searchable_spinner_dosage);
                    dialog.getWindow().setLayout(650, 800);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    EditText editText2 = dialog.findViewById(R.id.edit_text2);
                    ListView listView2 = dialog.findViewById(R.id.list_view2);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddDosageActivity.this, android.R.layout.simple_list_item_1, dosage_options);
                    listView2.setAdapter(adapter);
                    editText2.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            UploadTabletPerDose.setText(adapter.getItem(position));
                            dialog.dismiss();
                        }
                    });
                }
            });

            DoseSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    database = FirebaseDatabase.getInstance();
                    String productID = getString(R.string.product_id);
                    String medicineID = productID + "_c" + compartment;
                    reference = database.getReference("users").child(productID).child(medicineID).child("Reminders");

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean isDosageExists = false;
                            String frequency = frequencyTimeButton.getText().toString();
                            String DosageString = UploadTabletPerDose.getText().toString();
                            //Validation
                            if (frequency.equals("Select Time")) {
                                Toast.makeText(AddDosageActivity.this, "Frequency can't be empty", Toast.LENGTH_SHORT).show();
                                return;}
                            if (snapshot.hasChild(frequency)) {
                                Toast.makeText(AddDosageActivity.this, "There already exists a dosage with same frequency", Toast.LENGTH_SHORT).show();
                                return;}
                            float dosage = Float.parseFloat(DosageString);

                            DosageDataClass dataClass = new DosageDataClass(frequency, dosage, compartmentnumber);
                            reference.child(frequency).setValue(dataClass);

                            Toast.makeText(AddDosageActivity.this, "Dosage Details entered successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database error, if needed
                        }
                    });
                }
            });
        }
    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                //String original_time = String.format("%02d:%02d", hour, minute);
                String division;
                if (hour == 0) {
                    hour = 12;
                    division = " AM";
                } else if (hour > 12) {
                    hour = hour - 12;
                    division = " PM";
                } else if (hour == 12){
                    division = " PM";
                }
                else {
                    division = " AM";
                }
                String time = String.format("%02d:%02d", hour, minute);
                frequencyTimeButton.setText(time + division);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, false);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
}