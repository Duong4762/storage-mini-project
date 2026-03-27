package com.example.storage_mini_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.storage_mini_project.entities.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.Holder> {

    List<Movie> list;
    OnClick listener;

    public interface OnClick {
        void click(Movie m);
    }

    public MovieAdapter(List<Movie> list, OnClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder h, int i) {
        Movie m = list.get(i);

        h.title.setText(m.getTitle());
        h.genre.setText(m.getGenre());
        h.rating.setText("⭐ " + m.getRating());

        h.itemView.setOnClickListener(v -> listener.click(m));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView title, genre, rating;
        ImageView img;

        public Holder(View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            genre = v.findViewById(R.id.tvGenre);
            rating = v.findViewById(R.id.tvRating);
            img = v.findViewById(R.id.imgPoster);
        }
    }
}