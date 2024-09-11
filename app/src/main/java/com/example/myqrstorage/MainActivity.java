package com.example.myqrstorage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    //Views
    Button Login, Signup;
    EditText NameInput, PwdInput;

    //Database
    Note_database myDB;
    Dao dao;

    //Services and Activities
    ExecutorService service = Executors.newSingleThreadExecutor();
    Activity parent_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent_activity = this;

        //Database initialization
        myDB = Room.databaseBuilder(getApplicationContext(), Note_database.class,"QR_Database").build();

        //Button Assignment
        Login = findViewById(R.id.Login);
        Signup = findViewById(R.id.Signup);

        //Input Assignment
        NameInput = findViewById(R.id.NameInput);
        PwdInput = findViewById(R.id.PwdInput);


        //Button Onclicks
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncLoginAttempt(parent_activity, NameInput.getText().toString().toLowerCase(), PwdInput.getText().toString());
            }
        });

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //basic limitations for name and password
                if(NameInput.getText().toString().length() < 3){
                    Toast.makeText(getApplicationContext(), "Username must be at least 3 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(PwdInput.getText().toString().length() < 4){
                    Toast.makeText(getApplicationContext(), "Password must be at least 4 digits long", Toast.LENGTH_SHORT).show();
                    return;
                }

                asyncAddUser(parent_activity, NameInput.getText().toString().toLowerCase(), PwdInput.getText().toString());
            }
        });


    }

    public void asyncAddUser(Activity activity, String username, String pwd){
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    myDB.dao().insertAllUsers(new User(username, pwd));

                    (activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(activity, "Logging in...", Toast.LENGTH_SHORT).show();

                            //Bundling data for next activity
                            Bundle b = new Bundle();

                            b.putString("username", username);
                            b.putString("password", pwd);

                            Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                            intent.putExtras(b);

                            startActivity(intent);
                            finish();
                        }
                    });

                }
                catch (Exception e){
                    Log.d("DAO_ERR", "EXISTS");

                    (activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "This user is already registered", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                for(User i : myDB.dao().getAll()){
                    Log.d("DAO_LIST", ""+ i.Username);
                }
            }
        });
    }

    public void asyncLoginAttempt(Activity activity,String username, String pwd){
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    User temp = myDB.dao().findByUsername(username);

                    Log.d("DAO_Out", temp.Username + " : " + temp.Password);

                    (activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!pwd.matches(temp.Password)){
                                Toast.makeText(activity, "Incorrect password", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(activity, "Logging in...", Toast.LENGTH_SHORT).show();

                            //Bundling data for next activity
                            Bundle b = new Bundle();

                            b.putString("username", temp.Username);
                            b.putString("password", temp.Password);

                            Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                            intent.putExtras(b);

                            startActivity(intent);
                            finish();
                        }
                    });

                }
                catch (Exception e){
                    Log.d("DAO_ERR", "Login Attempt");

                    (activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "This user doesn't exist", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                for(User i : myDB.dao().getAll()){
                    Log.d("DAO_LIST", ""+ i.Username);
                }
            }
        });
    }

//    @Override
//    protected void OnActivityResult(int requestCode, int resultCode, @Nullable Intent data){
//
//    }
}