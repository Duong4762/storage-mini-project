package com.example.storage_mini_project.dal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.storage_mini_project.entities.Category;

import java.util.List;

@Dao
public interface CategoyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE id = :id")
    Category getById(int id);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAll();

    @Query("SELECT * FROM categories WHERE name LIKE '%' || :keyword || '%'")
    List<Category> searchByName(String keyword);

    @Query("DELETE FROM categories")
    void deleteAll();
}
