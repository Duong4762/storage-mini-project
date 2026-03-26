package com.example.storage_mini_project.dal;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.storage_mini_project.entities.Category;
import com.example.storage_mini_project.entities.Order;
import com.example.storage_mini_project.entities.OrderDetail;
import com.example.storage_mini_project.entities.Product;
import com.example.storage_mini_project.entities.User;

@Database(entities = {
        User.class,
        Category.class,
        Product.class,
        Order.class,
        OrderDetail.class
}, version = 1, exportSchema = false)
public abstract class AppDB extends RoomDatabase {

    private static final String DATABASE_NAME = "storage_mini_project.db";
    private static volatile AppDB instance;

    public abstract UserDAO userDAO();

    public abstract CategoyDAO categoryDAO();

    public abstract ProductDAO productDAO();

    public abstract OrderDAO orderDAO();

    public abstract OrderDetailDAO orderDetailDAO();

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
