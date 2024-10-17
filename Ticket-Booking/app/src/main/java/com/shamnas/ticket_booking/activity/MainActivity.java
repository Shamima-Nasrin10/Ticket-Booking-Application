package com.shamnas.ticket_booking.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shamnas.ticket_booking.R;
import com.shamnas.ticket_booking.databinding.ActivityMainBinding;
import com.shamnas.ticket_booking.model.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private int adultPassenger = 1, childPassenger = 1;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH);

    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLocations();
        initPassengers();
        initClassSeat();
        initDatePickup();
        setVariable();

    }

    private void initLocations() {
        binding.progressBarFrom.setVisibility(View.VISIBLE);
        binding.progressBarTo.setVisibility(View.VISIBLE);
        DatabaseReference myRef = database.getReference("Locations");
        ArrayList<Location> locationList = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        locationList.add(issue.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, locationList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.fromSp.setAdapter(adapter);
                    binding.toSp.setAdapter(adapter);
                    binding.fromSp.setSelection(1);
                    binding.progressBarFrom.setVisibility(View.GONE);
                    binding.progressBarTo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initPassengers() {
        binding.plusAdultBtn.setOnClickListener(v -> {
            adultPassenger++;
            binding.adultTxt.setText(adultPassenger + " Adult");
        });
        binding.minusAdultBtn.setOnClickListener(v -> {
            if (adultPassenger > 1) {
                adultPassenger--;
                binding.adultTxt.setText(adultPassenger + " Adult");
            }
        });

        binding.plusChildBtn.setOnClickListener(v -> {
            childPassenger++;
            binding.childTxt.setText(childPassenger + " Child");
        });

        binding.minusChildBtn.setOnClickListener(v -> {
            if (childPassenger > 0) {
                childPassenger--;
                binding.childTxt.setText(childPassenger + " Child");
            }
        });
    }

    private void initClassSeat() {

        binding.progressBarClass.setVisibility(View.VISIBLE);
        ArrayList<String> classList = new ArrayList<>();
        classList.add("Business Class");
        classList.add("First Class");
        classList.add("Economy Class");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, classList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.classSp.setAdapter(adapter);
        binding.progressBarClass.setVisibility(View.GONE);
    }

    private void showDatePickerDialog(TextView textView) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            calendar.set(selectedYear, selectedMonth, selectedDay);
            String formattedDate = dateFormat.format(calendar.getTime());
            textView.setText(formattedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void initDatePickup() {
        Calendar calendarToday=Calendar.getInstance();
        String currentDate= dateFormat.format(calendarToday.getTime());
        binding.departureDate.setText(currentDate);

        Calendar calendarTomorrow=Calendar.getInstance();
        calendarTomorrow.add(Calendar.DAY_OF_YEAR,1);
        String tomorrowDate=dateFormat.format(calendarTomorrow.getTime());
        binding.returnDateTxt.setText(tomorrowDate);

        binding.departureDate.setOnClickListener(v -> showDatePickerDialog(binding.departureDate));

        binding.returnDateTxt.setOnClickListener(v -> showDatePickerDialog(binding.returnDateTxt));
    }

    private void setVariable() {

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SearchActivity.class);
            }
        });
    }

}