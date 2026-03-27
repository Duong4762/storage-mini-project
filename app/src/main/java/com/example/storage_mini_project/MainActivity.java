package com.example.storage_mini_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.storage_mini_project.dal.AppDB;

public class MainActivity extends AppCompatActivity {

    private AppDB database;
    private TextView tvWelcome;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = AppDB.getInstance(this);
        
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        
        String username = LoginActivity.getCurrentUsername(this);
        tvWelcome.setText("Chào mừng, " + username + "!\n\nỨng dụng Movie Ticket\nDatabase đã sẵn sàng với 5 bảng:\nUsers, Movies, Theaters, Showtimes, Tickets");
        
        btnLogout.setOnClickListener(v -> LoginActivity.logout(this));
    }
}