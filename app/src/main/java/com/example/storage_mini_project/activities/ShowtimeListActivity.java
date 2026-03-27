package com.example.storage_mini_project.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storage_mini_project02_v1.R;
import com.example.storage_mini_project02_v1.adapters.CinemaAdapter;
import com.example.storage_mini_project02_v1.adapters.DateAdapter;
import com.example.storage_mini_project02_v1.dal.AppDB;
import com.example.storage_mini_project02_v1.entities.Showtime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class ShowtimeListActivity extends AppCompatActivity {

    private DateAdapter dateAdapter;
    private CinemaAdapter cinemaAdapter;

    private int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtime_list);

        RecyclerView rvDates = findViewById(R.id.rv_dates);
        RecyclerView rvCinemas = findViewById(R.id.rv_cinemas);
        TextView tvTitle = findViewById(R.id.tv_select_date_label);

        movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        String movieTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);

        tvTitle.setText(movieTitle != null ? movieTitle : "Chọn suất chiếu");

        // RecyclerView ngày
        rvDates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dateAdapter = new DateAdapter();
        rvDates.setAdapter(dateAdapter);

        // RecyclerView rạp
        rvCinemas.setLayoutManager(new LinearLayoutManager(this));
        cinemaAdapter = new CinemaAdapter(this);
        rvCinemas.setAdapter(cinemaAdapter);

        // Load ngày
        List<String> dates = getNext5Days();
        dateAdapter.setData(dates);

        // Click ngày
        dateAdapter.setOnDateClickListener(date -> loadShowtimes(date));

        // Load mặc định ngày đầu
        if (!dates.isEmpty()) {
            loadShowtimes(dates.get(0));
        }
    }

    private void loadShowtimes(String date) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDB db = AppDB.getInstance(this);

            List<Showtime> showtimes =
                    db.showtimeDAO().getByMovieAndDate(movieId, date);

            runOnUiThread(() -> {
                cinemaAdapter.setData(showtimes);
            });
        });
    }

    // Tạo 5 ngày tiếp theo
    private List<String> getNext5Days() {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 5; i++) {
            dates.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dates;
    }
}