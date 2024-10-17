package com.shamnas.ticket_booking.activity;

import android.content.Intent;
import android.os.Bundle;

import com.shamnas.ticket_booking.databinding.ActivityIntroBinding;

public class IntroActivity extends BaseActivity {

private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.startBtn.setOnClickListener(v -> startActivity(new Intent(IntroActivity.this, MainActivity.class)));
//        );
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }
}