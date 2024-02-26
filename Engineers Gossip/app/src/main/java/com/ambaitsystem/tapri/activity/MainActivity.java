package com.ambaitsystem.tapri.activity;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ambaitsystem.tapri.adapter.ChatRoomsAdapter;
import com.ambaitsystem.tapri.app.Config;
import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.tapri.helper.SimpleDividerItemDecoration;
import com.ambaitsystem.tapri.helper.SpacesItemDecoration;
import com.ambaitsystem.tapri.helper.subscription_status;
import com.ambaitsystem.tapri.model.ChatRoom;
import com.ambaitsystem.tapri.model.Message;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;
    //    private ToolTipView myToolTipView;
    //Horizontal Recycle View
    //private RecyclerView horizontal_recycler_view;

    public ArrayList<Data> data_user;

    private ProgressBar progress;
    TextView txtsent_0_row;
    Button btnrequestsent_retry;
   // Tracker mTracker;

    //Start of oncreate method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MyApplication.getInstance().getPrefManager().getUser() == null) {
            launchLoginActivity();
        }

        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font  color=\"#FFFFFF\">Choose your tapri</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 2);
        recyclerView.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        recyclerView.setLayoutManager(lm);

       /* 1//Horizontal Recycler View
        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.addItemDecoration(new SpacesItemDecoration(2));
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);
*/
        ///////////////////////////////////////
        progress = (ProgressBar) findViewById(R.id.Progress_for_sent);
        txtsent_0_row = (TextView) findViewById(R.id.txtsent_0_row);

        btnrequestsent_retry = (Button) findViewById(R.id.btnrequestsent_retry);
        btnrequestsent_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchChatRooms();
            }
        });
        /**
         * Broadcast receiver calls in two scenarios
         * 1. gcm registration is completed
         * 2. when new push notification is received
         * */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications


                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL
                    Log.e(TAG, "GCM registration id is sent to our server");

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                }
            }
        };


        chatRoomArrayList = new ArrayList<>();
        mAdapter = new ChatRoomsAdapter(this, chatRoomArrayList);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new ChatRoomsAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                ChatRoom chatRoom = chatRoomArrayList.get(position);
                Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));

        /**
         * Always check for google play services availability before
         * proceeding further with GCM
         * */
        if (checkPlayServices()) {

            fetchChatRooms();
        }

        //Context Menu : Register :Step 1
        registerForContextMenu(recyclerView);
////////////////////////////////////////////////////////////////////////////////////////////
        //ToolTip // Only for one time

       /* if(MyApplication.getInstance().getPrefManager().GetToolTipFlag() == null) {
            ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);

            ToolTip toolTip = new ToolTip()
                    .withText("To stop receiving messages : Menu > Logout OR Tap Long On Room > Not Interested")
                    .withColor(Color.RED)
                    .withTextColor(Color.WHITE)
                    .withAnimationType(ToolTip.AnimationType.FROM_TOP);
            myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.recycler_view));

            //Set Flag : Already Shown
            MyApplication.getInstance().getPrefManager().StoreToolTipFlag("true");

        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        */

        //Animate txtviewOpenTapriandChat to highlight
      /*1  TextView txtviewOpenTapriandChat = (TextView) findViewById(R.id.txtviewOpenTapriandChat);
        animatedString(txtviewOpenTapriandChat, "Open your tapri and lets chat.....");
*/
        showcaseDialogTutorial();

     //   mTracker = ((MyApplication) getApplication()).getDefaultTracker();

    }

    public void animatedString(final TextView tw, final String endString) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setObjectValues("amit", endString);
        //Toast.makeText(this,endString,Toast.LENGTH_LONG).show();
        valueAnimator.setEvaluator(new TypeEvaluator<CharSequence>() {

            @Override
            public CharSequence evaluate(float v, CharSequence startValue, CharSequence endvalue) {
                if (startValue.length() < endvalue.length()) {
                    return endvalue.subSequence(0, (int) (endvalue.length() * v));
                } else
                    return startValue.subSequence(0, endvalue.length() + (int) ((startValue.length() - endvalue.length()) * v));
            }
        });
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(3);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                tw.setText(valueAnimator.getAnimatedValue().toString());
            }
        });
        valueAnimator.start();
    }

    //Context Menu : OnContext Item Selection :Step 2
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;

        try {
            position = ((ChatRoomsAdapter) getAdapter()).getPosition();
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getGroupId()) {
            case 0:
                // Subscribe
                //Toast.makeText(this,"0"+item.getItemId(),Toast.LENGTH_LONG).show();
                updateRow_Name(Integer.toString(position + 1), "s");
                break;
            case 1:
                // UnSubscribe
                //Toast.makeText(this,"1"+item.getItemId(),Toast.LENGTH_LONG).show();
                updateRow_Name(Integer.toString(position + 1), "u");
                break;
            case 2:
                // Exit
                //Toast.makeText(this,"2"+item.getItemId(),Toast.LENGTH_LONG).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Handles new push notification
     */
    private void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra("type", -1);

        // if the push is of chat room message
        // simply update the UI unread messages count
        if (type == Config.PUSH_TYPE_CHATROOM) {
            Message message = (Message) intent.getSerializableExtra("message");
            String chatRoomId = intent.getStringExtra("chat_room_id");

            if (message != null && chatRoomId != null) {
                updateRow(chatRoomId, message);
            }
        } else if (type == Config.PUSH_TYPE_USER) {
            // push belongs to user alone
            // just showing the message in a toast
            Message message = (Message) intent.getSerializableExtra("message");
            Toast.makeText(getApplicationContext(), "New Message: " + message.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Updates the chat list unread count and the last message
     */
    private void updateRow(String chatRoomId, Message message) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(chatRoomId)) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage(message.getMessage());

                //Put Count In SharedPreference
                int Count = MyApplication.getInstance().getPrefManager().Increament_unread_count(cr.getId());
                cr.setUnreadCount(Count);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void updateRow_Name(String position, String NewName) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId().equals(position)) {
                int index = chatRoomArrayList.indexOf(cr);

                if (NewName.equalsIgnoreCase("s")) {
                    subscription_status ObjSubscriptionstatus = new subscription_status();
                    Boolean status = ObjSubscriptionstatus.Update_ChatRoomId_for_Subscribe_OR_Unsubscribe(getApplicationContext(), cr.getId(), "true");
                    ObjSubscriptionstatus = null;
                    if (status == true) {
                        cr.setName(cr.getName());
                        cr.setsubscribeStatus(true);


                    } else {
                        Toast.makeText(getApplicationContext(), "Not Subscribed,Please Retry.", Toast.LENGTH_LONG).show();

                    }
                } else {
                    subscription_status ObjSubscriptionstatus = new subscription_status();
                    Boolean status = ObjSubscriptionstatus.Update_ChatRoomId_for_Subscribe_OR_Unsubscribe(getApplicationContext(), cr.getId(), "false");
                    ObjSubscriptionstatus = null;

                    if (status == true) {
                        cr.setName(cr.getName());
                        cr.setsubscribeStatus(false);


                    } else {
                        Toast.makeText(getApplicationContext(), "Not unsubscribed,Please Retry.", Toast.LENGTH_LONG).show();
                    }
                }

                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * fetching the chat rooms by making http call
     */
    private void fetchChatRooms() {
        progress.setVisibility(View.VISIBLE);
        txtsent_0_row.setText("");
        try {
            progress.setVisibility(View.VISIBLE);
        } catch (Exception e) {
        }
        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.CHAT_ROOMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    // check for error flag
                    if (obj.getBoolean("error") == false)
                    {

                        chatRoomArrayList.clear();


                        btnrequestsent_retry.setVisibility(View.GONE);
                        try {
                            progress.setVisibility(View.GONE);
                        } catch (Exception e) {
                        }

                        //Get random_users And show it in Top Horizontal View
                      /*1  JSONArray random_usersarray = obj.getJSONArray("random_users");
                        for (int i = 0; i < random_usersarray.length(); i++) {
                            JSONObject randomuserObj = (JSONObject) random_usersarray.get(i);
                            data_user.add(new Data(R.drawable.user_top, randomuserObj.getString("user_id") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("institute") + "#" + randomuserObj.getString("batch") + "#" + randomuserObj.getString("branch") + "#" + randomuserObj.getString("likes") + "#" + randomuserObj.getString("isinterested")));
                        }
                        data_user.add(new Data(R.drawable.user_top, "All"));

                        horizontalAdapter.notifyDataSetChanged(); */

                        /////////////////////////////////////////////////////////
                        JSONArray chatRoomsArray = obj.getJSONArray("chat_rooms");

                        for (int i = 0; i < chatRoomsArray.length(); i++) {
                            JSONObject chatRoomsObj = (JSONObject) chatRoomsArray.get(i);
                            ChatRoom cr = new ChatRoom();

                            subscription_status ObjSubscriptionstatus = new subscription_status();
                            Boolean Subscriptionstatus = ObjSubscriptionstatus.check_subscription_status(getApplicationContext(), chatRoomsObj.getString("chat_room_id"));
                            cr.setsubscribeStatus(Subscriptionstatus);
                            ObjSubscriptionstatus = null;


                            cr.setId(chatRoomsObj.getString("chat_room_id"));


                            cr.setId(chatRoomsObj.getString("chat_room_id"));
                            cr.setName(chatRoomsObj.getString("name"));
                            //Load Message from Shared Preference
                            //Log.v("Last Message","#"+ chatRoomsObj.getString("chat_room_id") +" :  " +MyApplication.getInstance().getPrefManager().get_last_message(chatRoomsObj.getString("chat_room_id")));
                            cr.setLastMessage(MyApplication.getInstance().getPrefManager().get_last_message(chatRoomsObj.getString("chat_room_id")));

                            cr.setUnreadCount(MyApplication.getInstance().getPrefManager().get_unread_count(chatRoomsObj.getString("chat_room_id")));
                            cr.setTimestamp(chatRoomsObj.getString("created_at"));

                            chatRoomArrayList.add(cr);
                        }

                    } else {
                        // error in fetching chat rooms
                        txtsent_0_row.setText("Check Internet Connection");
                        btnrequestsent_retry.setVisibility(View.VISIBLE);
                        try {
                            progress.setVisibility(View.GONE);
                        } catch (Exception e) {
                        }
                    }

                } catch (JSONException e) {
                    txtsent_0_row.setText("Check Internet Connection");
                    btnrequestsent_retry.setVisibility(View.VISIBLE);
                    try {
                        progress.setVisibility(View.GONE);
                    } catch (Exception xe) {
                    }
                }

                mAdapter.notifyDataSetChanged();
            //1    horizontalAdapter.notifyDataSetChanged();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                btnrequestsent_retry.setVisibility(View.VISIBLE);
                try {
                    progress.setVisibility(View.GONE);
                } catch (Exception e) {
                }
                txtsent_0_row.setText("Check Internet Connection");
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }



    private void launchLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));


      //  mTracker.setScreenName("Select_Tapri_1");
      //  mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    // starting the service to register with GCM


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int i = menuItem.getItemId();

        if (i == R.id.action_logout) {//Unsubscribe from Topic
            MyApplication.getInstance().logout();

        } else if (i == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

// Add data to the intent, the receiving app will decide what to do with it.
            intent.putExtra(Intent.EXTRA_SUBJECT, "GTU Ki Tapri");
            intent.putExtra(Intent.EXTRA_TEXT, "Lets share our thoughts on GTU Ki Tapri : https://play.google.com/store/apps/details?id=com.ambaitsystem.vgecchat");
            startActivity(Intent.createChooser(intent, "How do you want to share?"));

        } else if (i == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }


    //Context Menu : Register  to getAdapter :Step 3
    public ChatRoomsAdapter getAdapter() {
        return mAdapter;
    }

    private void showcaseDialogTutorial() {
        final SharedPreferences tutorialShowcases = getSharedPreferences("showcase_tapri_main", MODE_PRIVATE);

        boolean run;

        run = tutorialShowcases.getBoolean("run?", true);

        if (run) {//If the buyer already went through the showcases it won't do it again.
            //1 final ViewTarget horizontal_recycler_view = new ViewTarget(R.id.horizontal_recycler_view, this);//Variable holds the item that the showcase will focus on.
            final ViewTarget recycler_view = new ViewTarget(R.id.recycler_view, this);

            //This code creates a new layout parameter so the button in the showcase can move to a new spot.
            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.addRule(RelativeLayout.CENTER_IN_PARENT);
            lps.addRule(RelativeLayout.CENTER_IN_PARENT);
            int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
            lps.setMargins(margin, margin, margin, margin);

           //This creates the first showcase.
            final ShowcaseView ShowCase = new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(recycler_view)
                    .setContentTitle("Choose your Tapri")
                    .setContentText("\nIncrease your touch to student network.")
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .build();
            ShowCase.setButtonText("next");


            //When the button is clicked then the switch statement will check the counter and make the new showcase.
            ShowCase.overrideButtonClick(new View.OnClickListener() {
                int count1 = 0;

                @Override
                public void onClick(View v) {
                    count1++;
                    switch (count1) {

                        case 1:
                            ShowCase.setTarget(recycler_view);
                            ShowCase.setContentTitle("Tapri");
                            ShowCase.setContentText("\nSelect Tapri of your branch to get information about other colleges.");
                            ShowCase.setButtonText("Close");
                            break;


                        case 3:
                            SharedPreferences.Editor tutorialShowcasesEdit = tutorialShowcases.edit();
                            tutorialShowcasesEdit.putBoolean("run?", false);
                            tutorialShowcasesEdit.apply();

                            ShowCase.hide();
                            break;
                    }
                }
            });
        }
    }
}
