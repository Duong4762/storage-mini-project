package com.example.storage_mini_project;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storage_mini_project.adapters.TheatersAdapter;
import com.example.storage_mini_project.dal.AppDB;
import com.example.storage_mini_project.entities.Theater;
import com.example.storage_mini_project.helpers.DatabaseInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppDB database;
    private RecyclerView rvTheaters;
    private TextView tvEmpty;
    private TheatersAdapter adapter;
    private ExecutorService executorService;

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
        executorService = Executors.newSingleThreadExecutor();
        
        initViews();
        initializeDataAndLoad();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Danh sách rạp chiếu");
        
        rvTheaters = findViewById(R.id.rvTheaters);
        tvEmpty = findViewById(R.id.tvEmpty);
        
        rvTheaters.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TheatersAdapter(this, new ArrayList<>());
        rvTheaters.setAdapter(adapter);
    }

    private void initializeDataAndLoad() {
        executorService.execute(() -> {
            // Check if database is empty and initialize
            if (database.theaterDAO().getAll().isEmpty()) {
                // Initialize sample data synchronously in this thread
                DatabaseInitializer.insertSampleUsers(database);
                DatabaseInitializer.insertSampleMovies(database);
                DatabaseInitializer.insertSampleTheaters(database);
                DatabaseInitializer.insertSampleShowtimes(database);
            }
            
            // Now load theaters (already in background thread)
            List<Theater> theaters = database.theaterDAO().getAll();
            
            runOnUiThread(() -> {
                if (theaters.isEmpty()) {
                    rvTheaters.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvTheaters.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                    adapter.updateData(theaters);
                }
            });
        });
    }

    private void loadTheaters() {
        executorService.execute(() -> {
            List<Theater> theaters = database.theaterDAO().getAll();
            
            runOnUiThread(() -> {
                if (theaters.isEmpty()) {
                    rvTheaters.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvTheaters.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                    adapter.updateData(theaters);
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            LoginActivity.logout(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}