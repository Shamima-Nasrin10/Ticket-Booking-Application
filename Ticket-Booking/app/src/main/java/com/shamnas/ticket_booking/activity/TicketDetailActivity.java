package com.shamnas.ticket_booking.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.shamnas.ticket_booking.R;
import com.shamnas.ticket_booking.databinding.ActivityTicketDetailBinding;
import com.shamnas.ticket_booking.model.Flight;

public class TicketDetailActivity extends BaseActivity {
    private ActivityTicketDetailBinding binding;
    private Flight flight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        getIntentExtra();
        setVariable();

    }

    private void setVariable() {
        binding.backBtnTicket.setOnClickListener(v -> finish());
        binding.fromTxt.setText(flight.getFromShort());
        binding.fromSmallTxt.setText(flight.getFrom());
        binding.toTxt.setText(flight.getTo());
        binding.toShortTxt.setText(flight.getToShort());
        binding.toSmallTxt.setText(flight.getTo());
        binding.dateTxt.setText(flight.getDate());
        binding.timeTxt.setText(flight.getTime());
        binding.arrivalTxt.setText(flight.getArriveTime());
        binding.classTxtTicket.setText(flight.getClassSeat());
        binding.priceTxtTicket.setText("$"+flight.getPrice());
        binding.airlinesTxt.setText(flight.getAirlineName());
        binding.seatTxt.setText(flight.getPassenger());

        Glide.with(TicketDetailActivity.this)
                .load(flight.getAirlineLogo())
                .into(binding.logo);
    }

    private void getIntentExtra() {
        flight=(Flight) getIntent().getSerializableExtra("flight");
    }
}