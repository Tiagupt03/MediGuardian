package com.example.mediguardianbasic;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Locale;
import java.text.DateFormat;

public class UploadActivity extends AppCompatActivity {

    Button saveButton, timeButton, dateButton;
    EditText uploadName, uploadQuantity, uploadCompartment;
    DatePickerDialog datePickerDialog;
    int hour, minute;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadName = findViewById(R.id.Uploadname);
        uploadQuantity = findViewById(R.id.Uploadquantity);
        uploadCompartment = findViewById(R.id.UploadCompartment);
        timeButton = findViewById(R.id.timeButton);
        dateButton = findViewById(R.id.datePickerButton);
        saveButton = findViewById(R.id.saveButton);

        initDatePicker();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                String productID = getString(R.string.product_id);
                reference = database.getReference("users").child(productID);

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isMedicineExists = false;
                        if (snapshot.getChildrenCount() >= 3) {
                            Toast.makeText(UploadActivity.this, "You have reached the maximum limit of medicines.", Toast.LENGTH_SHORT).show();
                        } else {
                            String name = uploadName.getText().toString();
                            String quantityString = uploadQuantity.getText().toString();
                            String compartment = uploadCompartment.getText().toString();
                            String selectedDate = dateButton.getText().toString();
                            String selectedTime = timeButton.getText().toString();

                            if (name.isEmpty() || quantityString.isEmpty() || compartment.isEmpty() || selectedTime.equals("Select Time") || selectedDate.equals("Select Date")) {
                                Toast.makeText(UploadActivity.this, "None of the fields can be empty", Toast.LENGTH_SHORT).show();
                                return;}
                            int quantity;
                            try {quantity = Integer.parseInt(quantityString);} catch (NumberFormatException e) {Toast.makeText(UploadActivity.this, "Please enter a valid value for tablet quantity", Toast.LENGTH_SHORT).show();return;}
                            int compartmentNumber;try {compartmentNumber = Integer.parseInt(compartment);} catch (NumberFormatException e) {Toast.makeText(UploadActivity.this, "Please enter a valid compartment number", Toast.LENGTH_SHORT).show();return;}

                            if (compartmentNumber > 3 || compartmentNumber <= 0) {
                                Toast.makeText(UploadActivity.this, "Please input a valid compartment number as indicated on the pillbox", Toast.LENGTH_SHORT).show();
                                return;}

                            // Get the current time
                            Calendar currentTime = Calendar.getInstance();
                            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                            int currentMinute = currentTime.get(Calendar.MINUTE);

                            // Parse the selected time from the timeButton
                            String[] selectedTimeParts = timeButton.getText().toString().split(":");
                            int selectedHour = Integer.parseInt(selectedTimeParts[0]);
                            int selectedMinute = Integer.parseInt(selectedTimeParts[1]);

                            // Compare the selected time with the current time
                            boolean is_cur_time_less = selectedHour < currentHour || (selectedHour == currentHour && selectedMinute < currentMinute);
                            boolean is_today = selectedDate.equals(getTodaysDate());
                            if (is_cur_time_less && is_today) {
                                // Display an error message if the selected time is earlier than the current time
                                Toast.makeText(UploadActivity.this, "Please select a time greater than the current time", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                DataClass existingMedicine = dataSnapshot.getValue(DataClass.class);

                                if (existingMedicine != null && (existingMedicine.getDataName().equals(name) || existingMedicine.getDataCompartment() == compartmentNumber)) {
                                    isMedicineExists = true;
                                    break;}}
                            if (isMedicineExists) {
                                Toast.makeText(UploadActivity.this, "Compartment already occupied or medicine already exists.", Toast.LENGTH_SHORT).show();
                            } else {
                                String medicineID = productID + "_c" + compartmentNumber;

                                DataClass dataClass = new DataClass(name, quantity, compartmentNumber, selectedDate, selectedTime);

                                reference.child(medicineID).setValue(dataClass);

                                Toast.makeText(UploadActivity.this, "Details entered successfully!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error, if needed
                    }
                });
            }
        });
    }

    @NonNull
    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return dateFormat.format(cal.getTime());
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        // Set the default value for selectedDate
        String defaultDate = "Select Date";
        dateButton.setText(defaultDate);
    }

    @NonNull
    private String makeDateString(int day, int month, int year) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return dateFormat.format(cal.getTime());
    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                String time = String.format("%02d:%02d", hour, minute);
                timeButton.setText(time);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, false);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void openDatePicker(View view) {if (datePickerDialog != null) {datePickerDialog.show();}}
}