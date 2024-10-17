package com.shamnas.ticket_booking.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.shamnas.ticket_booking.R;
import com.shamnas.ticket_booking.databinding.ActivitySearchBinding;

public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding binding;
    private String from, to, date;
    private int numPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();

    }

    private void getIntentExtra() {
        from=getIntent().getStringExtra("from");
        to=getIntent().getStringExtra("to");
    }
}