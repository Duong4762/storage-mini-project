package com.example.storage_mini_project.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.storage_mini_project.LoginActivity;
import com.example.storage_mini_project.R;
import com.example.storage_mini_project.dal.AppDB;
import com.example.storage_mini_project.entities.Movie;
import com.example.storage_mini_project.entities.Showtime;
import com.example.storage_mini_project.entities.Ticket;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeatSelectionActivity extends AppCompatActivity {

    private GridLayout gridSeats;
    private TextView tvTotal, tvMovieInfo, tvShowtimeInfo;
    private Button btnConfirm;
    private AppDB database;
    private ExecutorService executorService;
    private int showtimeId;
    private Showtime showtime;
    private List<String> bookedSeats = new ArrayList<>();
    private List<String> selectedSeats = new ArrayList<>();
    private static final int ROWS = 10;
    private static final int COLS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        database = AppDB.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        showtimeId = getIntent().getIntExtra("showtimeId", -1);

        initViews();
        loadShowtimeData();
        loadBookedSeats();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chọn ghế");

        gridSeats = findViewById(R.id.gridSeats);
        tvTotal = findViewById(R.id.tvTotal);
        tvMovieInfo = findViewById(R.id.tvMovieInfo);
        tvShowtimeInfo = findViewById(R.id.tvShowtimeInfo);
        btnConfirm = findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(v -> confirmBooking());
    }

    private void loadShowtimeData() {
        executorService.execute(() -> {
            showtime = database.showtimeDAO().getById(showtimeId);
            Movie movie = database.movieDAO().getById(showtime.getMovieId());

            runOnUiThread(() -> {
                if (showtime != null && movie != null) {
                    tvMovieInfo.setText(movie.getTitle());
                    tvShowtimeInfo.setText(showtime.getShowDate() + " - " + showtime.getShowTime() + " - Phòng " + showtime.getScreenNumber());
                }
            });
        });
    }

    private void loadBookedSeats() {
        executorService.execute(() -> {
            bookedSeats = database.ticketDAO().getBookedSeats(showtimeId);

            runOnUiThread(() -> {
                createSeatLayout();
            });
        });
    }

    private void createSeatLayout() {
        gridSeats.setColumnCount(COLS);
        gridSeats.removeAllViews();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                String seatNumber = (char) ('A' + row) + String.valueOf(col + 1);
                Button seatButton = new Button(this);
                
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(col, 1f);
                params.setMargins(4, 4, 4, 4);
                seatButton.setLayoutParams(params);
                
                seatButton.setText(seatNumber);
                seatButton.setTextSize(10);
                seatButton.setPadding(8, 16, 8, 16);

                if (bookedSeats.contains(seatNumber)) {
                    seatButton.setBackgroundColor(Color.parseColor("#757575"));
                    seatButton.setTextColor(Color.WHITE);
                    seatButton.setEnabled(false);
                } else {
                    seatButton.setBackgroundColor(Color.parseColor("#4CAF50"));
                    seatButton.setTextColor(Color.WHITE);
                    seatButton.setOnClickListener(v -> toggleSeatSelection(seatButton, seatNumber));
                }

                gridSeats.addView(seatButton);
            }
        }
    }

    private void toggleSeatSelection(Button button, String seatNumber) {
        if (selectedSeats.contains(seatNumber)) {
            selectedSeats.remove(seatNumber);
            button.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else {
            selectedSeats.add(seatNumber);
            button.setBackgroundColor(Color.parseColor("#E50914"));
        }
        updateTotal();
    }

    private void updateTotal() {
        double total = selectedSeats.size() * showtime.getPrice();
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotal.setText("Tổng tiền: " + format.format(total));
    }

    private void confirmBooking() {
        if (selectedSeats.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ghế", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = LoginActivity.getCurrentUserId(this);
        long bookingDate = System.currentTimeMillis();

        executorService.execute(() -> {
            for (String seatNumber : selectedSeats) {
                Ticket ticket = new Ticket(
                    userId,
                    showtimeId,
                    seatNumber,
                    bookingDate,
                    showtime.getPrice(),
                    "BOOKED"
                );
                database.ticketDAO().insert(ticket);
            }

            int newAvailableSeats = showtime.getAvailableSeats() - selectedSeats.size();
            database.showtimeDAO().updateAvailableSeats(showtimeId, newAvailableSeats);

            runOnUiThread(() -> {
                Toast.makeText(this, "Đặt vé thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, TicketConfirmationActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
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
