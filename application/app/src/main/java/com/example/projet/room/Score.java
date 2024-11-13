package com.example.projet.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "scores")
public class Score implements Serializable {

    @NonNull
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "points")
    public int points;

    @ColumnInfo(name = "total")
    public int total;

    public Score(String id, int points, int total) {
        this.id = id;
        this.points = points;
        this.total = total;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.points+"/"+this.total;
    }

    public boolean estMeilleur(Score other) {
        if (other == null) {
            return true;
        }
        if (points == other.points) {
            return total< other.total;
        }
        return points > other.points;
    }

}
