package com.ambaitsystem.tapri.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.DbBasic;
import com.ambaitsystem.tapri.helper.single_chat_message;
import com.ambaitsystem.vgecchat.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;


public class List_Single_chat_Messages extends AppCompatActivity {
    public SQLiteDatabase db = null;
    List<single_chat_message> list = null;
    RecyclerView mRecyclerView;
    adapter_single_chat_message ObjAdapter;
    //Tracker mTracker;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_single_chat_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font  color=\"#FFFFFF\">Messages</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.single_chat_messages_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();
        ObjAdapter = new adapter_single_chat_message(list, R.layout.content_single_chat_messages_row, this);
        mRecyclerView.setAdapter(ObjAdapter);

        //Read DB And Set All Value to single_chat_message
        try {
            Read_DB_Set_Value();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not load messages,Please Retry." + e.toString(), Toast.LENGTH_LONG).show();
        }


       // mTracker = ((MyApplication) getApplication()).getDefaultTracker();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_friends:
                Intent intent = new Intent(this, Request_Friends_see_all.class);
                startActivity(intent);
                return true;
            case R.id.action_clear_chat:
                AlertDialog.Builder builder = new AlertDialog.Builder(List_Single_chat_Messages.this);

                builder.setTitle("Clear chat history");
                builder.setMessage("Are you sure to clear chat history?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Clear_All_Messages();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void Clear_All_Messages() {
        //---------------------------------------------------------------------------------------------------------
        //Create Table : single_users_messages
        //---------------------------------------------------------------------------------------------------------
        db = (new DbBasic(this)).getWritableDatabase();
        try {
            String Delete_Query = "DELETE from single_users_messages";
            db.execSQL(Delete_Query);
            db.close();

            Toast.makeText(this, "All Messages deleted", Toast.LENGTH_SHORT).show();
            list.clear();
            ObjAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            db.close();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

    public void displatch_dataitem_change()
    {
        try {
            list.clear();
            Read_DB_Set_Value();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not load messages,Please Retry." + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

       // mTracker.setScreenName("Single_chat_List");
      //  mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void Read_DB_Set_Value() {

        //Check First Time table.
        //If there is entry in FirstTime Table then, App is Already Configure i.e Redirect to Main Activity
        //Else,Redirect to Configure

        db = (new DbBasic(this)).getReadableDatabase();
        //Cursor constantsCursor = db.rawQuery("SELECT * FROM single_users_messages where isactive = 1 order by created_at DESC",null);
        Cursor constantsCursor = db.rawQuery("SELECT user_id,name,email,GROUP_CONCAT(message,created_at)  as message FROM single_users_messages where isactive = 1 group by user_id order by created_at asc", null);

        constantsCursor.moveToFirst();

        single_chat_message ObjSingleChat;
        if (constantsCursor.getCount() > 0) {
            while (!constantsCursor.isAfterLast()) {
                ObjSingleChat = new single_chat_message();
                //user_id,name,message,created_at,email,isactive

                ObjSingleChat.SetSingle_chat_user_id(constantsCursor.getString(constantsCursor.getColumnIndex("user_id")));
                ObjSingleChat.SetSingle_chat_user_name(constantsCursor.getString(constantsCursor.getColumnIndex("name")));
                String Message = constantsCursor.getString(constantsCursor.getColumnIndex("message")).replace("$#", "\n");
                ObjSingleChat.SetSingle_chat_message(Message);

                //ObjSingleChat.SetStrSingle_chat_timestamp(constantsCursor.getString(constantsCursor.getColumnIndex("created_at")));
                ObjSingleChat.SetStrSingle_chat_user_email(constantsCursor.getString(constantsCursor.getColumnIndex("email")));
                list.add(ObjSingleChat);
                constantsCursor.moveToNext();

                //Ig Message contain "TEA OFFER RECEIVED" then show : layout_accept_reject [contains button]
                //On click of Accept reject call function
                //On Response :
                // Hide layout_accept_reject
                //Show : layout_status & Set Status to txtstatus_accept_reject
                //Remove Chat of Selected User ID


            }
        } else {
            ObjSingleChat = new single_chat_message();

            ObjSingleChat.SetSingle_chat_user_id("0");
            ObjSingleChat.SetSingle_chat_user_name("0 Message");
            ObjSingleChat.SetSingle_chat_message("-");
            ObjSingleChat.SetStrSingle_chat_timestamp("-");
            ObjSingleChat.SetStrSingle_chat_user_email("-");
            list.add(ObjSingleChat);
        }
        db.close();

        ObjAdapter.notifyDataSetChanged();

    }

    public int get_DB_count(Context context) {

        //Check First Time table.
        //If there is entry in FirstTime Table then, App is Already Configure i.e Redirect to Main Activity
        //Else,Redirect to Configure

        db = (new DbBasic(context)).getReadableDatabase();
        //Cursor constantsCursor = db.rawQuery("SELECT * FROM single_users_messages where isactive = 1 order by created_at DESC",null);
        Cursor constantsCursor = db.rawQuery("SELECT user_id,name,email,GROUP_CONCAT(message,created_at)  as message FROM single_users_messages where isactive = 1 group by user_id", null);

        constantsCursor.moveToFirst();

        return constantsCursor.getCount();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_single_chat, menu);
        return true;
    }

}
