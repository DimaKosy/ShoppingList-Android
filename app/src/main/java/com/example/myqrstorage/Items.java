package com.example.myqrstorage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"Box_ID", "name"})
public class Items {
    public String Box_ID;
    public String name;

    @ColumnInfo
    public int count;
}
