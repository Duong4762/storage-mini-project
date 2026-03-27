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
import com.example.storage_mini_project.helpers.DatabaseInitializer;

public class MainActivity extends AppCompatActivity {

    private AppDB database;
    private TextView tvWelcome;
    private Button btnLogout;
    private Button btnStartBooking;

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
        DatabaseInitializer.initializeSampleData(this);
        
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        btnStartBooking = findViewById(R.id.btnStartBooking);
        
        String username = LoginActivity.getCurrentUsername(this);
        tvWelcome.setText("Chào mừng, " + username + "!\n\n" +
                "🎬 Ứng dụng Movie Ticket\n\n" +
                "✅ Database đã sẵn sàng với:\n" +
                "• 3 Users\n" +
                "• 12 Movies\n" +
                "• 8 Theaters\n" +
                "• 500+ Showtimes\n\n" +
                "Login: admin / admin123");
        
        btnLogout.setOnClickListener(v -> LoginActivity.logout(this));
        
        btnStartBooking.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TheaterActivity.class);
            startActivity(intent);
        });
    }
}