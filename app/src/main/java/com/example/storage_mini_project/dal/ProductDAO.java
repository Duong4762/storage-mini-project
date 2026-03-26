package com.example.storage_mini_project.dal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.storage_mini_project.entities.Product;

import java.util.List;

@Dao
public interface ProductDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products WHERE id = :id")
    Product getById(int id);

    @Query("SELECT * FROM products ORDER BY name ASC")
    List<Product> getAll();

    @Query("SELECT * FROM products WHERE category_id = :categoryId")
    List<Product> getByCategory(int categoryId);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%'")
    List<Product> searchByKeyword(String keyword);

    @Query("SELECT * FROM products WHERE stock_quantity > 0")
    List<Product> getInStock();

    @Query("DELETE FROM products")
    void deleteAll();
}
