package com.final_project.plantidentifier.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlantDao {
    @Query("SELECT * FROM plant ORDER BY id")
    List<PlantEntry> loadAllTasks();

    @Query("SELECT * FROM plant WHERE id == :curId")
    PlantEntry getPlantById(int curId);

    @Insert
    void insertTask(PlantEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(PlantEntry taskEntry);

    @Delete
    void deleteTask(PlantEntry taskEntry);
}
