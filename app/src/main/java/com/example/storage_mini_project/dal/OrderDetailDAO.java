package com.example.storage_mini_project.dal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.storage_mini_project.entities.OrderDetail;

import java.util.List;

@Dao
public interface OrderDetailDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OrderDetail orderDetail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<OrderDetail> orderDetails);

    @Update
    void update(OrderDetail orderDetail);

    @Delete
    void delete(OrderDetail orderDetail);

    @Query("SELECT * FROM order_details WHERE order_id = :orderId")
    List<OrderDetail> getByOrder(int orderId);

    @Query("SELECT * FROM order_details WHERE product_id = :productId")
    List<OrderDetail> getByProduct(int productId);

    @Query("DELETE FROM order_details WHERE order_id = :orderId")
    void deleteByOrder(int orderId);
}
