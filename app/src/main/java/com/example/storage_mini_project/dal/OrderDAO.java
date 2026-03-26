package com.example.storage_mini_project.dal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.storage_mini_project.entities.Order;

import java.util.List;

@Dao
public interface OrderDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Order order);

    @Update
    void update(Order order);

    @Delete
    void delete(Order order);

    @Query("SELECT * FROM orders WHERE id = :id")
    Order getById(int id);

    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY order_date DESC")
    List<Order> getByUser(int userId);

    @Query("SELECT * FROM orders ORDER BY order_date DESC")
    List<Order> getAll();

    @Query("SELECT * FROM orders WHERE user_id = :userId AND status = :status")
    List<Order> getByUserAndStatus(int userId, String status);

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    void updateStatus(int orderId, String status);

    @Query("DELETE FROM orders WHERE id = :orderId")
    void deleteById(int orderId);
}
