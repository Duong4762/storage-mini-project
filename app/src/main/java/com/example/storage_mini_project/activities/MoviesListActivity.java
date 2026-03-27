package com.example.storage_mini_project.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storage_mini_project.R;
import com.example.storage_mini_project.adapters.MoviesAdapter;
import com.example.storage_mini_project.dal.AppDB;
import com.example.storage_mini_project.entities.Movie;
import com.example.storage_mini_project.entities.Showtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MoviesListActivity extends AppCompatActivity {

    private RecyclerView rvMovies;
    private TextView tvEmpty;
    private MoviesAdapter adapter;
    private AppDB database;
    private ExecutorService executorService;
    private int theaterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);

        database = AppDB.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        theaterId = getIntent().getIntExtra("theaterId", -1);
        String theaterName = getIntent().getStringExtra("theaterName");

        initViews(theaterName);
        loadMovies();
    }

    private void initViews(String theaterName) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(theaterName);

        rvMovies = findViewById(R.id.rvMovies);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MoviesAdapter(this, new ArrayList<>(), theaterId);
        rvMovies.setAdapter(adapter);
    }

    private void loadMovies() {
        executorService.execute(() -> {
            List<Showtime> showtimes = database.showtimeDAO().getByTheater(theaterId);
            Set<Integer> movieIds = new HashSet<>();
            for (Showtime showtime : showtimes) {
                movieIds.add(showtime.getMovieId());
            }

            List<Movie> movies = new ArrayList<>();
            for (Integer movieId : movieIds) {
                Movie movie = database.movieDAO().getById(movieId);
                if (movie != null) {
                    movies.add(movie);
                }
            }

            runOnUiThread(() -> {
                if (movies.isEmpty()) {
                    rvMovies.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvMovies.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                    adapter.updateData(movies);
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
