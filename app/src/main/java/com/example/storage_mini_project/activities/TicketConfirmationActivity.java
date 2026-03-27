package com.example.storage_mini_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storage_mini_project.MainActivity;
import com.example.storage_mini_project.R;
import com.example.storage_mini_project.adapters.TicketsAdapter;
import com.example.storage_mini_project.dal.AppDB;
import com.example.storage_mini_project.entities.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicketConfirmationActivity extends AppCompatActivity {

    private RecyclerView rvTickets;
    private TextView tvEmpty;
    private Button btnBackHome;
    private TicketsAdapter adapter;
    private AppDB database;
    private ExecutorService executorService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_confirmation);

        database = AppDB.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        userId = getIntent().getIntExtra("userId", -1);

        initViews();
        loadTickets();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vé đã đặt");

        rvTickets = findViewById(R.id.rvTickets);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBackHome = findViewById(R.id.btnBackHome);

        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketsAdapter(this, new ArrayList<>());
        rvTickets.setAdapter(adapter);

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadTickets() {
        executorService.execute(() -> {
            List<Ticket> tickets = database.ticketDAO().getByUser(userId);

            runOnUiThread(() -> {
                if (tickets.isEmpty()) {
                    rvTickets.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvTickets.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                    adapter.updateData(tickets);
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
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
