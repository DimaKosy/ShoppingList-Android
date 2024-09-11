package com.example.myqrstorage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteActivity extends AppCompatActivity {
    Activity parent;
    Boolean Update;
    String OldName;
    int OldAmount;

    //Views
    TextView ItemName, Amount;
    Button Save;

    //database
    Note_database myDB;
    Dao dao;
    ArrayList<BoxObject> BoxInfo;

    //Service
    ExecutorService service = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);
        parent = this;

        //Bundle
        Bundle b = getIntent().getExtras();
        Update = false;
        OldName = "";


        String username = b.getString("username");
        Log.d("BUNDLE_ERR", username);
        String itemName = b.getString("itemName");
        Log.d("BUNDLE_ERR", itemName);
        int amount = b.getInt("Amount");
        Log.d("BUNDLE_ERR", ""+amount);
        Boolean checked = b.getBoolean("checked");

        //Database initialization
        myDB = Room.databaseBuilder(getApplicationContext(), Note_database.class,"QR_Database").build();

        //View init
        ItemName = findViewById(R.id.ItemName);
        Amount = findViewById(R.id.Amount);
        Save = findViewById(R.id.Save);

        Log.d("Comp", "comparing");

        //To use the old name and old amount to delete the box
        if(!itemName.matches("")){
            ItemName.setText(itemName);
            OldName = itemName;
            OldAmount = amount;
            Update = true;
        }
        Amount.setText(Integer.toString(amount));



        //Buttons
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = ItemName.getText().toString();
                int amount = Math.round(Float.parseFloat(Amount.getText().toString()));

                //basic limitations for name and amount
                if(ItemName.getText().toString().length() < 3){
                    Toast.makeText(getApplicationContext(), "Please enter an item name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(amount <= 0){
                    Toast.makeText(getApplicationContext(), "Please pick enter an amount greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                //checks if item is to be updated
                //We have to delete the item rather than just update it as the item name is tied to its primary key
                //This forces us to make a new item instead
                if(Update){
//                    Log.d("DAO_RMVE", ""+)
                    asyncRemoveUserNote(parent, new Note(
                            OldName,
                            username,
                            OldAmount,
                            checked
                    ));
                }

                asyncAddUserNote_Finish(parent, new Note(
                        itemName,
                        username,
                        amount,
                        checked
                ));


            }
        });

    }

    public void asyncAddUserNote_Finish(Activity activity, Note noteObject) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    myDB.dao().insertAllNotes(noteObject);

                    //Exits activity
                    activity.setResult(RESULT_OK);
                    activity.finish();

                } catch (Exception e) {
                    Log.d("DAO_ERR", "EXISTS");

                    (activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "This note already exists\nPlease use another name", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d("ASYNC_ADD", "COMP_FAIL");

                }
                finally {
                    Log.d("ASYNC_COMP", "COMP");
                }
            }
        });
    }

    public void asyncRemoveUserNote(Activity activity, Note noteObject) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    myDB.dao().removeAllNotes(noteObject);
                } catch (Exception e) {
                    Log.d("DAO_ERR", "EXISTS");

                    (activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "This note doesn't exists", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}