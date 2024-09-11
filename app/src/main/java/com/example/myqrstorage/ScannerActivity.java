package com.example.myqrstorage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerActivity extends AppCompatActivity {

    int REQUEST_CODE = 1;
    Activity parent = this;
    String username;

    //Buttons
    Button AddBox, Logout, Delete;

    //database
    Note_database myDB;
    Dao dao;
    ArrayList<BoxObject> BoxInfo;

    //Adapter
    RecyclerView BoxList;
    BoxListAdapter boxListAdapter;

    //Service
    ExecutorService service = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scanner);
        //Bundles
        Bundle b = getIntent().getExtras();
        username = b.getString("username");
        String password = b.getString("password");

        //Buttons
        AddBox = findViewById(R.id.AddBox);
        Logout = findViewById(R.id.Logout);
        Delete = findViewById(R.id.Delete);


        //Database initialization
        myDB = Room.databaseBuilder(getApplicationContext(), Note_database.class,"QR_Database").build();

        //recycleview adapter init
        BoxInfo = new ArrayList<BoxObject>();

        BoxList = findViewById(R.id.BoxList);
        BoxList.setLayoutManager(new LinearLayoutManager(this));

        boxListAdapter = new BoxListAdapter(BoxInfo, parent, username);
        BoxList.setAdapter(boxListAdapter);

        AddBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();

                b.putString("username", username);
                b.putString("itemName", "");
                b.putInt("Amount", 0);
                b.putBoolean("checked",false);

                Intent intent = new Intent(ScannerActivity.this, NoteActivity.class);
                intent.putExtras(b);

                //launches intent with with the activity result so that we can call an update to the recyclerview when returning to this activity
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<BoxObject> temp = new ArrayList<BoxObject>();
                for(BoxObject b : BoxInfo){
                    if(b.Checked){
                        asyncRemoveUserNote(parent, new Note(b.getTitle(), username,b.getAmount(),b.getChecked()));
                        temp.add(b);
//                        boxListAdapter.
                    }
                }
                //avoids concurrent modification Exceptions
                BoxInfo.removeAll(temp);
                boxListAdapter.notifyDataSetChanged();

            }
        });
//        BoxInfo.clear();
//        asyncGetBoxes(this,username);

//        asyncAddUserNote(this, new Note("Mango",username,0));

    }

    @Override
    protected void onResume() {
        super.onResume();
//        BoxInfo.clear();
        asyncGetBoxes(this,username);
        boxListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        BoxInfo.clear();
        Log.d("ACT_RES", ""+resultCode + " : " + requestCode);

        if (resultCode == RESULT_OK) {
            for(int i = 0; i < BoxInfo.size(); i++){
                if(BoxInfo.get(i).Updated){
                    boxListAdapter.notifyItemChanged(i);
                    BoxInfo.get(i).Updated = false;
                }
            }

            asyncGetBoxes(this,username);
            boxListAdapter.notifyDataSetChanged();
        }

    }


    public void asyncGetBoxes(Activity activity, String username){
//        BoxInfo.clear();
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BoxInfo.clear();
                    List<Note> temp = myDB.dao().getUserBoxes(username);

                    for(Note n : temp){
                        BoxInfo.add(new BoxObject(n.ItemName,n.Amount, n.Checked));
                        Log.d("DAO_READ", n.ItemName);
                    }

                    boxListAdapter.notifyDataSetChanged();
                }
                catch (Exception e){
                    Log.d("DAO_ERR", "FAILED TO GET BOXES");
//
//                    (activity).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(activity, "This user is already registered", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
                for(User i : myDB.dao().getAll()){
                    Log.d("DAO_LIST", ""+ i.Username);
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

    public void asyncAddUserNote(Activity activity, Note noteObject) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    myDB.dao().insertAllNotes(noteObject);
                } catch (Exception e) {
                    Log.d("DAO_ERR", "EXISTS");

                    (activity).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "This note already exists\nPlease use a different name", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}