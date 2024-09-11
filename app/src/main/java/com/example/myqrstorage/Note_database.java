//With referance to
//https://developer.android.com/codelabs/android-room-with-a-view#0
//https://developer.android.com/training/data-storage/room/defining-data

package com.example.myqrstorage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class,Note.class}, version = 1)
public abstract class Note_database extends RoomDatabase {

    private static Note_database instance;

    public abstract Dao dao();

    public static synchronized Note_database getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(context.getApplicationContext(), Note_database.class, "QR_Database")
                    .build();
        }

        return instance;
    }


}
