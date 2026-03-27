package com.example.storage_mini_project;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storage_mini_project.dal.AppDB;
import com.example.storage_mini_project.entities.Theater;

import java.util.List;

public class TheaterActivity extends AppCompatActivity {

    ListView listView;
    AppDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);

        listView = findViewById(R.id.listViewTheater);
        db = AppDB.getInstance(this);

        new Thread(() -> {
            List<Theater> list = db.theaterDAO().getAll();

            runOnUiThread(() -> {
                listView.setAdapter(new ArrayAdapter<Theater>(
                        this, 0, list) {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = getLayoutInflater()
                                    .inflate(R.layout.item_theater, parent, false);
                        }

                        Theater t = getItem(position);

                        TextView name = convertView.findViewById(R.id.tvName);
                        TextView location = convertView.findViewById(R.id.tvLocation);

                        name.setText(t.getName());
                        location.setText(t.getLocation());

                        return convertView;
                    }
                });
            });

        }).start();
    }
}