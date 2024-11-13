package com.example.projet.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface ScoreDAO {

    @Query("SELECT * FROM scores WHERE id = :id")
    Score getScoreFromId(String id);

    @Query("SELECT * FROM scores WHERE id LIKE :type")
    Observable<List<Score>> getAllFromType(String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScore(Score score);
}
