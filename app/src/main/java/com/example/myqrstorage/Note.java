package com.example.myqrstorage;

import androidx.annotation.NonNull;
import androidx.room.Entity;


@Entity(primaryKeys = {"ItemName", "Username"})
public class Note {
    @NonNull
    public String ItemName;
    @NonNull
    public String Username;

    public int Amount;
    public Boolean Checked;

    public Note(String ItemName, String Username, int Amount, boolean Checked){
        this.ItemName = ItemName;
        this.Username = Username;
        this.Amount = Amount;
        this.Checked = Checked;
    }
}
