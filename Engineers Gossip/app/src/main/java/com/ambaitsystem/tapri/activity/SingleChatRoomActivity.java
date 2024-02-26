package com.ambaitsystem.tapri.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.adapter.SingleChatRoomThreadAdapter;
import com.ambaitsystem.tapri.app.Config;
import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.DbBasic;
import com.ambaitsystem.tapri.helper.single_chat_message;
import com.ambaitsystem.tapri.model.Message;
import com.ambaitsystem.tapri.model.User;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleChatRoomActivity extends AppCompatActivity {

    private String TAG = SingleChatRoomActivity.class.getSimpleName();

    private String SELF_MESSAGE = "5874";
    private String OTHER_MESSAGE = "4587";

    public SQLiteDatabase db = null;

    private String user_id, selfUserId, user_name;
    private RecyclerView recyclerView;
    private SingleChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private Button btnLoadMore;
    private ImageButton btnSend;
    private ProgressBar progress_bar;
    ProgressBar progress_bar_loading;

    private int lastVisibleItem, totalItemCount;
  //  Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        setContentView(R.layout.activity_chat_room);

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (ImageButton) findViewById(R.id.btn_send);

        TextView txtdepartmentclose = (TextView) findViewById(R.id.txtdepartmentclose);
        txtdepartmentclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLoadMore = (Button) findViewById(R.id.btnloadmore);
        btnLoadMore.setVisibility(View.GONE);

        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progress_bar.setVisibility(View.GONE);

        progress_bar_loading = (ProgressBar) findViewById(R.id.progress_bar_loading);
        progress_bar_loading.setVisibility(View.GONE);

        Intent intent = getIntent();

        //From X user message arrived
        user_id = intent.getStringExtra("user_id");
        String message = intent.getStringExtra("message");
        user_name = intent.getStringExtra("name");
        txtdepartmentclose.setText("  " + Html.fromHtml("<b><font color=\"#FFFFFF\">" + user_name + "</font></b>"));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();
        // self user id is to identify the message owner
        selfUserId = MyApplication.getInstance().getPrefManager().getUser().getId();

        mAdapter = new SingleChatRoomThreadAdapter(this, messageArrayList, selfUserId);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();

            }
        });


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION))
                {
                   // new push message is received
                    handlePushNotification(intent);
                }
            }
        };

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //Load All Chat Messages
        Read_DB_Set_Value();


     //   mTracker = ((MyApplication) getApplication()).getDefaultTracker();

    }


    @Override
    protected void onResume() {
        super.onResume();

        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));


       // mTracker.setScreenName("Single_chat_Room");
      //  mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     */
    private void handlePushNotification(Intent intent) {
        Message message = (Message) intent.getSerializableExtra("message");
        String from_user_id = intent.getStringExtra("user_id");

        if (message != null && from_user_id != null) {
            if (user_id.equalsIgnoreCase(from_user_id)) {
                messageArrayList.add(message);
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 1) {
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                }
            }
        }
    }

    /**
     * Posting a new message in chat room
     * will make an http call to our server. Our server again sends the message
     * to all the devices as push notification
     */
    private void sendMessage() {
        final String message = this.inputMessage.getText().toString().trim().replace("'","").replace(","," ");

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

                progress_bar.setVisibility(View.VISIBLE);

        this.inputMessage.setText("");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.SINGLE_ROOM_MESSAGE_PUSH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //   Log.e(TAG, "response: " + response);
                try {
                    progress_bar.setVisibility(View.GONE);
                } catch (Exception e) {
                }

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONObject commentObj = obj.getJSONObject("messages");

                        String commentId = commentObj.getString("message_id");
                        String commentText = commentObj.getString("message");
                        String createdAt = commentObj.getString("created_at");

                        JSONObject userObj = obj.getJSONObject("user");
                        String userId = userObj.getString("user_id");
                        String userName = userObj.getString("name");
                        User user = new User(userId, userName, null, null, null, null);

                        Message message = new Message();
                        message.setId(commentId);
                        message.setMessage("$#" + commentText + " " + SELF_MESSAGE);
                        message.setCreatedAt(createdAt);
                        message.setUser(user);

                        messageArrayList.add(message);
                        mAdapter.notifyDataSetChanged();

                        if (mAdapter.getItemCount() > 1) {
                            // scrolling to bottom of the recycler view
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                        //user_name is value shown on windows lable
                        //Other Name
                        if(userId.equalsIgnoreCase(MyApplication.getInstance().getPrefManager().getUser().getId().toString())) {
                            commentText = "$#$#" + SELF_MESSAGE + " " + commentText + " : ";
                        }
                        else {
                            commentText = "$#$#" + OTHER_MESSAGE + " " + commentText + " : ";

                        }
                        store_Outcomming_message_for_Single_User(user_id, user_name, commentText, createdAt, "", 1);

                    } else {
                        Toast.makeText(getApplicationContext(), "Not able to send message!#0", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Not able to send message!#1", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                //Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Not able to send message!#2", Toast.LENGTH_SHORT).show();
                inputMessage.setText(message);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", MyApplication.getInstance().getPrefManager().getUser().getId());
                params.put("to", user_id);
                params.put("message", message);

                //Log.e(TAG, "Params: " + params.toString());

                return params;
            }

            ;
        };


        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    /**
     * Fetching all the messages of a single chat room
     */
 /*   private void fetchChatThread(int Count_Index)
    {
        //When This function calls we will remove all elements from   messageArrayList
        messageArrayList.clear();
        progress=new ProgressDialog(this);
        progress.setMessage("Fetching Messages");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();

        String endPoint = EndPoints.CHAT_THREAD.replace("_ID_", chatRoomId+"_"+Count_Index);
        Log.v("End Point","#"+endPoint);
       // Log.e(TAG, "endPoint: " + endPoint);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                endPoint, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
               // Log.e(TAG, "response: " + response);
                try
                { progress.dismiss();}catch (Exception e1){}

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error
                    if (obj.getBoolean("error") == false) {
                        JSONArray commentsObj = obj.getJSONArray("messages");

                        for (int i = 0; i < commentsObj.length(); i++) {
                            JSONObject commentObj = (JSONObject) commentsObj.get(i);

                            String commentId = commentObj.getString("message_id");
                            String commentText = commentObj.getString("message");
                            String createdAt = commentObj.getString("created_at");

                            JSONObject userObj = commentObj.getJSONObject("user");
                            String userId = userObj.getString("user_id");
                            String userName = userObj.getString("username");
                            User user = new User(userId, userName, null);

                            Message message = new Message();
                            message.setId(commentId);
                            message.setMessage(commentText);
                            message.setCreatedAt(createdAt);
                            message.setUser(user);

                            messageArrayList.add(message);
                        }

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Not Able To get Chat!#3,Check Internet Connection." + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                   // Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Not Able To get Chat!#4,Check Internet Connection."+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Not Able To get Chat!#5,Check Internet Connection.", Toast.LENGTH_SHORT).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
*/
    private void Read_DB_Set_Value() {
        //Check First Time table.
        //If there is entry in FirstTime Table then, App is Already Configure i.e Redirect to Main Activity
        //Else,Redirect to Configure

        db = (new DbBasic(this)).getReadableDatabase();
        //Cursor constantsCursor = db.rawQuery("SELECT * FROM single_users_messages where isactive = 1 order by created_at DESC",null);
        Cursor constantsCursor = db.rawQuery("SELECT user_id,name,email,message,created_at FROM single_users_messages where isactive = 1 and user_id = " + user_id, null);

        constantsCursor.moveToFirst();

        single_chat_message ObjSingleChat;
        if (constantsCursor.getCount() > 0) {
            while (!constantsCursor.isAfterLast()) {
                String userId = constantsCursor.getString(constantsCursor.getColumnIndex("user_id"));
                String userName = constantsCursor.getString(constantsCursor.getColumnIndex("name"));
                User user = new User(userId, userName, null, null, null, null);

                Message message = new Message();
                message.setId("0");
                message.setMessage(constantsCursor.getString(constantsCursor.getColumnIndex("message")).replace("$#", ""));
                message.setCreatedAt(constantsCursor.getString(constantsCursor.getColumnIndex("created_at")));
                message.setUser(user);

                messageArrayList.add(message);
                mAdapter.notifyDataSetChanged();

                constantsCursor.moveToNext();
            }
        } else {
            String userId = "0";
            String userName = "-";
            User user = new User(userId, userName, null, null, null, null);

            Message message = new Message();
            message.setId("0");
            message.setMessage("0 Message");
            message.setCreatedAt("-");
            message.setUser(user);

            messageArrayList.add(message);
            mAdapter.notifyDataSetChanged();
        }
        db.close();


    }

    private void store_Outcomming_message_for_Single_User(String user_id, String name, String message, String created_at, String email, int isactive) {
        //---------------------------------------------------------------------------------------------------------
        //Create Table : single_users_messages
        //---------------------------------------------------------------------------------------------------------
        db = (new DbBasic(this)).getWritableDatabase();
        try {

            //created_at = created_at.replace(","," ");
            String Insert_Query = "INSERT INTO single_users_messages (user_id,name,message,created_at,email,isactive) VALUES('" + user_id + "','" + name + "','" + message + "','" + created_at + "','" + email + "'," + 1 + ");";
            db.execSQL(Insert_Query);
            db.close();
        } catch (Exception e) {
            db.close();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

}
