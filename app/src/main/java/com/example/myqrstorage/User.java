package com.example.myqrstorage;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String Username;

    @ColumnInfo
    public String Password;

    public User(String Username, String Password){
        this.Username = Username;
        this.Password = Password;
    }

}