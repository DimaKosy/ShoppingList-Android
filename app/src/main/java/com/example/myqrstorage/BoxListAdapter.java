package com.example.myqrstorage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoxListAdapter extends RecyclerView.Adapter<BoxListAdapter.viewholder> {

    ArrayList<BoxObject> BoxList;
    Activity parent;
    String username;
    BoxListAdapter self;

    //database
    static Note_database myDB;
    Dao dao;

    //services
    static ExecutorService service = Executors.newSingleThreadExecutor();

    BoxListAdapter(ArrayList<BoxObject> BoxList, Activity parent, String username){
        myDB = Room.databaseBuilder(parent.getApplicationContext(), Note_database.class,"QR_Database").build();

        this.BoxList = BoxList;
        this.parent = parent;
        this.username = username;
        this.self = this;
    }

    @NonNull
    @Override
    public BoxListAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_boxlist, parent, false);

        return new BoxListAdapter.viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int i) {

        holder.BoxTitle.setText(BoxList.get(i).getTitle());
        holder.BoxDesc.setText(Integer.toString(BoxList.get(i).getAmount()));
        holder.checkBox.setChecked(BoxList.get(i).getChecked());


        holder.NoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();

                b.putString("username", username);
                b.putString("itemName", BoxList.get(i).getTitle());
                b.putInt("Amount", BoxList.get(i).getAmount());
                b.putBoolean("checked",BoxList.get(i).getChecked());
                BoxList.get(i).Updated = true;


                Intent intent = new Intent(parent, NoteActivity.class);
                intent.putExtras(b);
                parent.startActivityForResult(intent,1);
            }
        });



        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BoxList.get(i).setChecked(holder.checkBox.isChecked());

                asyncUpdateUserNote(parent,new Note(BoxList.get(i).getTitle(), username,BoxList.get(i).getAmount(),BoxList.get(i).getChecked()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return BoxList.size();
    }


    static class viewholder extends  RecyclerView.ViewHolder{
        TextView BoxTitle, BoxDesc;
        ImageView BoxImg;
        LinearLayout NoteLayout;
        CheckBox checkBox;
        viewholder(View view){
            super(view);
            BoxTitle = view.findViewById(R.id.BoxTitle);
            BoxDesc = view.findViewById(R.id.BoxDesc);
            NoteLayout = view.findViewById(R.id.NoteLayout);
            checkBox = view.findViewById(R.id.checkBox);
        }
    }

    public void asyncUpdateUserNote(Activity activity, Note noteObject) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    myDB.dao().updateNote(noteObject);
                } catch (Exception e) {
                    Log.d("DAO_ERR", "FAILED UPDATE");

                }
            }
        });
    }

}
