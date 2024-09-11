package com.example.myqrstorage;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@androidx.room.Dao
public interface Dao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE Username LIKE :username LIMIT 1")
    User findByUsername(String username);

    @Query("SELECT * FROM note WHERE Username LIKE :username")
    List<Note> getUserBoxes(String username);


    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAllUsers(User... users);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAllNotes(Note... notes);

    @Delete
    void removeAllNotes(Note... notes);

    @Update
    void updateNote(Note...notes);

    @Delete
    void deleteUser(User user);
}
