package com.example.storage_mini_project.dal;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.storage_mini_project.entities.User;
import com.example.storage_mini_project.entities.Movie;
import com.example.storage_mini_project.entities.Theater;
import com.example.storage_mini_project.entities.Showtime;
import com.example.storage_mini_project.entities.Ticket;

@Database(entities = {
        User.class,
        Movie.class,
        Theater.class,
        Showtime.class,
        Ticket.class
}, version = 2, exportSchema = false)
public abstract class AppDB extends RoomDatabase {

    private static final String DATABASE_NAME = "movie_ticket_app.db";
    private static volatile AppDB instance;

    public abstract UserDAO userDAO();

    public abstract MovieDAO movieDAO();

    public abstract TheaterDAO theaterDAO();

    public abstract ShowtimeDAO showtimeDAO();

    public abstract TicketDAO ticketDAO();

    public static AppDB getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDB.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDB.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
