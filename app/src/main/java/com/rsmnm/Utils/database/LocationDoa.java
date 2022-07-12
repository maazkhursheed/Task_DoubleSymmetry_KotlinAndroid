package com.rsmnm.Utils.database;

import java.util.ArrayList;
import java.util.List;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.rsmnm.Models.LocationItem;

/**
 * Created by saqib on 9/12/2018.
 */

@Dao
public interface LocationDoa {

    @Query("SELECT COUNT(*) FROM LocationItem")
    Integer getTotalCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LocationItem locationItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(ArrayList<LocationItem> locationItems);

    @Query("SELECT * FROM LocationItem")
    List<LocationItem> getAll();

    @Delete
    public void delete(LocationItem loction);

}
