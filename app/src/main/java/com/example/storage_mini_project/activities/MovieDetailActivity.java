package com.example.storage_mini_project.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storage_mini_project.R;
import com.example.storage_mini_project.adapters.ShowtimesAdapter;
import com.example.storage_mini_project.dal.AppDB;
import com.example.storage_mini_project.entities.Movie;
import com.example.storage_mini_project.entities.Showtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView ivPoster;
    private TextView tvTitle, tvGenre, tvDuration, tvRating, tvDescription, tvEmpty;
    private RecyclerView rvShowtimes;
    private ShowtimesAdapter adapter;
    private AppDB database;
    private ExecutorService executorService;
    private int movieId, theaterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        database = AppDB.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        movieId = getIntent().getIntExtra("movieId", -1);
        theaterId = getIntent().getIntExtra("theaterId", -1);

        initViews();
        loadMovieDetails();
        loadShowtimes();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chi tiết phim");

        ivPoster = findViewById(R.id.ivPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvGenre = findViewById(R.id.tvGenre);
        tvDuration = findViewById(R.id.tvDuration);
        tvRating = findViewById(R.id.tvRating);
        tvDescription = findViewById(R.id.tvDescription);
        rvShowtimes = findViewById(R.id.rvShowtimes);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvShowtimes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShowtimesAdapter(this, new ArrayList<>());
        rvShowtimes.setAdapter(adapter);
    }

    private void loadMovieDetails() {
        executorService.execute(() -> {
            Movie movie = database.movieDAO().getById(movieId);

            runOnUiThread(() -> {
                if (movie != null) {
                    tvTitle.setText(movie.getTitle());
                    tvGenre.setText(movie.getGenre() + " • " + movie.getReleaseYear());
                    tvDuration.setText(movie.getDuration() + " phút");
                    tvRating.setText("⭐ " + movie.getRating());
                    tvDescription.setText(movie.getDescription());

                    Glide.with(this)
                            .load(movie.getPosterUrl())
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .into(ivPoster);
                }
            });
        });
    }

    private void loadShowtimes() {
        executorService.execute(() -> {
            List<Showtime> showtimes = database.showtimeDAO().getByMovieAndTheater(movieId, theaterId);

            runOnUiThread(() -> {
                if (showtimes.isEmpty()) {
                    rvShowtimes.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvShowtimes.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                    adapter.updateData(showtimes);
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
