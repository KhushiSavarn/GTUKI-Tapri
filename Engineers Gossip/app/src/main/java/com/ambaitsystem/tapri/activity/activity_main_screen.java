package com.ambaitsystem.tapri.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ambaitsystem.tapri.adapter.ChatRoomsAdapter;
import com.ambaitsystem.tapri.app.Config;
import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.tapri.helper.SimpleDividerItemDecoration;
import com.ambaitsystem.tapri.helper.SpacesItemDecoration;
import com.ambaitsystem.tapri.helper.Utils2;
import com.ambaitsystem.tapri.helper.subscription_status;
import com.ambaitsystem.tapri.model.ChatRoom;
import com.ambaitsystem.tapri.model.Message;
import com.ambaitsystem.tapri.model.User;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class activity_main_screen extends AppCompatActivity {

    private Toolbar toolbar;

    private static final String TAG_USER = "data";
    private static final String TAG_VERSION_CODE = "vcode";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    JSONArray user = null;
    private String myVersionName;
    private String myVersionCode;

    ShowcaseView sv;
    //Tracker mTracker;
    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;
    public adapter_main_top_users horizontalAdapter;
    public ArrayList<Data> data_user;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private RecyclerView horizontal_recycler_view;
    public adapter_premium_product_listing horizontalAdapter_product;
    private RecyclerView horizontal_recycler_view_product;
    public ArrayList<Data> data_product;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;
    LinearLayout retry_layout_main_screen;
    ProgressBar Progress_for_main;
    TextView txtmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font  color=\"#FFFFFF\">Gtu Ki Tapri</font>"));
        //Check For Update
        //GEt VErsion Code
        //If This activity called on Skip of App_Update than do not verify updates


        Bundle extras = getIntent().getExtras();
        String skip = null;

        if (extras != null) {
            skip = extras.getString("skip");
            // and get whatever type user account id is
        }
        if (skip.equalsIgnoreCase("0")) {
            final Context context = getApplicationContext(); // or activity.getApplicationContext()
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();

            myVersionName = "0"; // initialize String
            myVersionCode  = "0";
            try {
                myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
                myVersionCode= String.valueOf(packageManager.getPackageInfo(packageName, 0).versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (myVersionName.equalsIgnoreCase(MyApplication.getInstance().getPrefManager().getversion_code(context))) {

            } else {
                Intent i = new Intent(getBaseContext(), App_update.class);
                startActivity(i);
                finish();
            }
        }

        retry_layout_main_screen = (LinearLayout) findViewById(R.id.retry_layout_main_screen);
        Progress_for_main = (ProgressBar) findViewById(R.id.Progress_for_main);
        txtmessage = (TextView) findViewById(R.id.txtmessage);

        FloatingActionMenu material_design_android_floating_action_menu = (FloatingActionMenu)findViewById(R.id.material_design_android_floating_action_menu);
        material_design_android_floating_action_menu.setClosedOnTouchOutside(true);

        CardView card_view_tapri = (CardView) findViewById(R.id.card_view_tapri);
        card_view_tapri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        });

        CardView card_view_users = (CardView) findViewById(R.id.card_view_users);
        card_view_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), FriendsOnTapri.class));
            }
        });

        CardView card_view_buy = (CardView) findViewById(R.id.card_view_buy);
        card_view_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), Product_listing.class));
            }
        });

        FloatingActionButton fab_USERS = (FloatingActionButton) findViewById(R.id.fab_USERS);
        fab_USERS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_main_screen.this, FriendsOnTapri.class));
            }
        });

        FloatingActionButton fab_BUY = (FloatingActionButton) findViewById(R.id.fab_BUY);
        fab_BUY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_main_screen.this, Product_listing.class));
            }
        });

        FloatingActionButton fab_sell = (FloatingActionButton) findViewById(R.id.fab_sell);
        fab_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_main_screen.this, Product_listing_my.class));
            }
        });

        FloatingActionButton fab_my_cart = (FloatingActionButton) findViewById(R.id.fab_my_cart);
        fab_my_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_main_screen.this, My_Cart.class));
            }
        });

        FloatingActionButton fab_NEWS = (FloatingActionButton) findViewById(R.id.fab_NEWS);
        fab_NEWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_main_screen.this, Request_Status_News.class).putExtra("skip", "0"));
            }
        });

        TextView txtviewaddsfriend = (TextView)findViewById(R.id.txtviewaddsfriend);
        txtviewaddsfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_main_screen.this, FriendsOnTapri.class));
            }
        });
        TextView txtviewaddsell = (TextView)findViewById(R.id.txtviewaddsell);
        txtviewaddsell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_main_screen.this, Product_listing_my.class));
            }
        });
        //mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        ///Set Permium Product Listing
        horizontal_recycler_view_product = (RecyclerView) findViewById(R.id.offer_recycler_view);
       /* RecyclerView.LayoutManager lm = new GridLayoutManager(this, 1);
        horizontal_recycler_view_product.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        horizontal_recycler_view_product.setLayoutManager(lm);*/
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        horizontal_recycler_view_product.setLayoutManager(layoutManager);

        data_product = new ArrayList<>();
        horizontalAdapter_product = new adapter_premium_product_listing(data_product, this);
        horizontal_recycler_view_product.setAdapter(horizontalAdapter_product);


        //Horizontal Recycler View
        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(activity_main_screen.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.addItemDecoration(new SpacesItemDecoration(2));
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);

        data_user = new ArrayList<>();
        horizontalAdapter = new adapter_main_top_users(data_user, this);
        horizontal_recycler_view.setAdapter(horizontalAdapter);

        //Set Tapris
        chatRoomArrayList = new ArrayList<>();
        mAdapter = new ChatRoomsAdapter(this, chatRoomArrayList);

        recyclerView = (RecyclerView) findViewById(R.id.tapri_recycler_view);
        LinearLayoutManager layoutManager_tapri
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager_tapri);

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
                Intent intent = new Intent(activity_main_screen.this, ChatRoomActivity.class);
                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));


        System.setProperty("http.keepAlive","false");

        //Check Internet and retry
        cd = new ConnectionDetector(getBaseContext());

        Button btnrequestrecived_retry = (Button)findViewById(R.id.btnrequestrecived_retry);
        btnrequestrecived_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPlayServices())
                {
                    isInternetPresent = cd.isConnectingToInternet();

                    //CAll If Internet is available
                    // check for Internet status
                    if (isInternetPresent)
                    {
                        fetchChatRooms();
                    } else {
                        retry_layout_main_screen.setVisibility(View.VISIBLE);
                        txtmessage.setText("Check Internet connection and retry.");
                    }
                }
            }
        });

        if (checkPlayServices())
        {
            isInternetPresent = cd.isConnectingToInternet();
            //CAll If Internet is available
            // check for Internet status
            if (isInternetPresent) {
                fetchChatRooms();
            } else {
                retry_layout_main_screen.setVisibility(View.VISIBLE);
                txtmessage.setText("Check Internet connection and retry.");
            }
        }
        registerForContextMenu(recyclerView);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                } else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                }
            }
        };

        TextView name_updates = (TextView) findViewById(R.id.name_updates);
        name_updates.setText(MyApplication.getInstance().getPrefManager().getUser().getName());
       // name_updates.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.gift_small,0,0);

        TextView know_about_college = (TextView) findViewById(R.id.know_about_college);
        know_about_college.setText("TAP TO KNOW WHAT JUST HAPPENED AT " + MyApplication.getInstance().getPrefManager().getUser().getcollegename());

        CardView know = (CardView) findViewById(R.id.know);
        know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_main_screen.this, Request_News.class);
                intent.putExtra("skip", "0");
                startActivity(intent);
            }
        });

        CardView card_view_quiz= (CardView) findViewById(R.id.card_view_quiz);
        card_view_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_effect));
                Intent intent = new Intent(activity_main_screen.this, QuizCallActivity.class);
                startActivity(intent);
            }
        });
        try {
            showcaseDialogTutorial();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Load ProfileImage
        Load_Profile_Image(this);

        //Friends and Invide Image Button
        CardView card_view_friends = (CardView)findViewById(R.id.card_view_friends);
        card_view_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_effect));

                Intent intent = new Intent(activity_main_screen.this, Request_Status.class);
                intent.putExtra("skip", "0");
                startActivity(intent);
            }
        });

        //Friends and Invide Image Button
        CardView card_view_sell = (CardView)findViewById(R.id.card_view_sell);
        card_view_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_effect));
                startActivity(new Intent(activity_main_screen.this, Product_listing_my.class));
            }
        });

        CardView card_view_invite = (CardView)findViewById(R.id.card_view_invite);
        card_view_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_effect));

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                // Add data to the intent, the receiving app will decide what to do with it.
                intent.putExtra(Intent.EXTRA_SUBJECT, "GTU Ki Tapri");
                intent.putExtra(Intent.EXTRA_TEXT, "Lets gather On GTU Ki Tapri : https://play.google.com/store/apps/details?id=com.ambaitsystem.vgecchat  Exclusive social app for GTU Students.");
                startActivity(Intent.createChooser(intent, "How do you want to share?"));
            }
        });

        TextView gift_note = (TextView) findViewById(R.id.gift_note);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(gift_note, "alpha",  1f, .3f);
        fadeOut.setDuration(2000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(gift_note, "alpha", .3f, 1f);
        fadeIn.setDuration(2000);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn).after(fadeOut);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimationSet.start();
            }
        });
        mAnimationSet.start();

    }

    private void Load_Profile_Image(activity_main_screen context)
    {
        ImageView imgMyProfile = (ImageView)findViewById(R.id.imgMyProfile);
        imgMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_main_screen.this, ViewOwnProfile.class);
                startActivity(intent);
            }
        });

        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent)
        {
            User user = MyApplication.getInstance().getPrefManager().getUser();
            // get data via the key
            String id = user.getId();

            String URL_ForImage ="http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/" +id +".jpg";
            ImageLoader imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                    .displayer(new RoundedBitmapDisplayer(1000))
                    .cacheOnDisk(false).cacheInMemory(false)
                    .showImageForEmptyUri(R.drawable.user_top)
                    .showImageOnFail(R.drawable.user_top)
                    .showImageOnLoading(R.drawable.user_loading).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    context)
                    .defaultDisplayImageOptions(options)
                    .build();

            //ImageLoader.getInstance().init(config);
            //download and display image from url
           // imageLoader.displayImage(URL_ForImage, imgMyProfile, options);

        }
        else
        {
            imgMyProfile.setImageResource(R.drawable.user_loading);
        }
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

    //Context Menu : Register  to getAdapter :Step 3
    public ChatRoomsAdapter getAdapter() {
        return mAdapter;
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;

        try {
            position = ((ChatRoomsAdapter) getAdapter()).getPosition();
        } catch (Exception e) {
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


        private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void fetchChatRooms()
    {
        retry_layout_main_screen.setVisibility(View.GONE);
        Progress_for_main.setVisibility(View.VISIBLE);

        //Before Loading Data Clear all
        data_product.clear();
        data_user.clear();
        chatRoomArrayList.clear();

        String college_name = MyApplication.getInstance().getPrefManager().getUser().getcollegename().replace(" ", "_");
        String MyID = MyApplication.getInstance().getPrefManager().getUser().getId();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.CHAT_ROOMS + "/" + college_name + "/" + MyID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    //Store is admin
                    MyApplication.getInstance().getPrefManager().storeisadmin_code( String.valueOf(obj.getInt("isadmin")));
                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        try {
                            Progress_for_main.setVisibility(View.GONE);
                        } catch (Exception e) {
                        }

                        //Fetch Premium Products
                        //Get random_users And show it in Top Horizontal View
                        JSONArray premium_product_listingarray = obj.getJSONArray("premium_product_listing");

                        for (int i = 0; i < premium_product_listingarray.length(); i++) {
                            JSONObject randomuserObj = (JSONObject) premium_product_listingarray.get(i);
                            //		//product_id,product_name,product_price,product_category,description,creator_name,creator_id,creator_college,cell,isactive

                            data_product.add(new Data(R.drawable.product, randomuserObj.getString("product_id")
                                    + "#" + randomuserObj.getString("product_name")
                                    + "#" + randomuserObj.getString("product_price")
                                    + "#" + randomuserObj.getString("product_category")
                                    + "#" + randomuserObj.getString("description")
                                    + "#" + randomuserObj.getString("creator_name")
                                    + "#" + randomuserObj.getString("creator_id")
                                    + "#" + randomuserObj.getString("creator_college")
                                    + "#" + randomuserObj.getString("cell")
                                    + "#" + randomuserObj.getString("isactive")

                            ));
                        }

                        data_product.add(new Data(R.drawable.next, "0#ALL"));
                        horizontalAdapter_product.notifyDataSetChanged();

                        ///////////////////////////////////////////
                        //Before Loading Clear Top Users Scroll & Chat Rooms
                        data_user.clear();
                        //Get random_users And show it in Top Horizontal View
                        JSONArray random_usersarray = obj.getJSONArray("random_users");
                        data_user.add(new Data(R.drawable.user_top, "ALL#ALL#0#0#0#"));
                        for (int i = 0; i < random_usersarray.length(); i++) {
                            JSONObject randomuserObj = (JSONObject) random_usersarray.get(i);
                            data_user.add(new Data(R.drawable.user_top, randomuserObj.getString("user_id") + "#" + randomuserObj.getString("name") + "\nLast seen : -" + "#" + randomuserObj.getString("institute") + "#" + randomuserObj.getString("batch") + "#" + randomuserObj.getString("branch") + "#" + randomuserObj.getString("likes") + "#" + randomuserObj.getString("isinterested")));
                        }

                        if (random_usersarray.length() <= 0)
                            data_user.add(new Data(R.drawable.user_top, "0#NONE#0"));


                        horizontalAdapter.notifyDataSetChanged();
                        /////////////////////////////////////////////////////////

                        chatRoomArrayList.clear();

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

                        MyApplication.getInstance().getRequestQueue().stop();

                    } else {
                        try {
                            Progress_for_main.setVisibility(View.GONE);
                            retry_layout_main_screen.setVisibility(View.VISIBLE);
                            txtmessage.setText("Check Connection and Retry.");
                        } catch (Exception e) {
                        }
                        // error in fetching chat rooms
                        Toast.makeText(activity_main_screen.this, "Check Internet connection and retry.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    try {
                        Progress_for_main.setVisibility(View.GONE);
                        retry_layout_main_screen.setVisibility(View.VISIBLE);
                        txtmessage.setText("Check Connection and Retry.");
                    } catch (Exception e1) {
                    }
                    Toast.makeText(activity_main_screen.this, "Check Internet connection and retry.", Toast.LENGTH_SHORT).show();
                }

                mAdapter.notifyDataSetChanged();
                //1    horizontalAdapter_product.notifyDataSetChanged();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Progress_for_main.setVisibility(View.GONE);
                    retry_layout_main_screen.setVisibility(View.VISIBLE);
                    txtmessage.setText("Check Connection and Retry." +error.toString());
                } catch (Exception e) {
                }
                error.printStackTrace();

                Toast.makeText(activity_main_screen.this, "Check Internet connection and retry.", Toast.LENGTH_SHORT).show();
            }
        });

        // disabling retry policy so that it won't make
        // multiple http calls
        RetryPolicy policy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*5,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

       strReq.setRetryPolicy(policy);
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    // subscribing to global topic

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_status, menu);
        String IsAdmin = MyApplication.getInstance().getPrefManager().getisadmin_code(getApplicationContext());

        if(IsAdmin.equalsIgnoreCase("0"))
        {
            MenuItem item_approve = menu.findItem(R.id.action_approve);
            item_approve.setVisible(false);
        }
        //Id of Menu : action_message
        MenuItem item = menu.findItem(R.id.action_message);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        //Read Count From Database
        List_Single_chat_Messages ObjLst = new List_Single_chat_Messages();
        int Count = ObjLst.get_DB_count(getBaseContext());
        // Update LayerDrawable's BadgeDrawable
        Utils2.setBadgeCount(this, icon, Count);

        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //mTracker.setScreenName("Status_screen_1");
       // mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int i = menuItem.getItemId();

        if (i == R.id.action_message) {//Unsubscribe from Topic

            Intent intent = new Intent(this, List_Single_chat_Messages.class);
            startActivity(intent);

        } else if (i == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

// Add data to the intent, the receiving app will decide what to do with it.
            intent.putExtra(Intent.EXTRA_SUBJECT, "GTU Ki Tapri");
            intent.putExtra(Intent.EXTRA_TEXT, "Lets gather On GTU Ki Tapri : https://play.google.com/store/apps/details?id=com.ambaitsystem.vgecchat  Exclusive social app for GTU Students.");
            startActivity(Intent.createChooser(intent, "How do you want to share?"));

        } else if (i == R.id.action_profile) {
            Intent intent = new Intent(this, ViewOwnProfile.class);
            startActivity(intent);
        }else if(i == R.id.action_approve) {
            Intent intent = new Intent(this, activity_approve_reject_news.class);
            startActivity(intent);
        }
        else if (i == R.id.action_logout_status) {
            MyApplication.getInstance().logout();
        }


        return super.onOptionsItemSelected(menuItem);
    }

    private void showcaseDialogTutorial() {
        final SharedPreferences tutorialShowcases = getSharedPreferences("showcase_tapri_status_screen", MODE_PRIVATE);

        boolean run;

        run = tutorialShowcases.getBoolean("run?", true);

        if (run) {//If the buyer already went through the showcases it won't do it again.
            final ViewTarget name_updates = new ViewTarget(R.id.name_updates, this);//Variable holds the item that the showcase will focus on.
            final ViewTarget Friends = new ViewTarget(R.id.horizontal_recycler_view, this);//Variable holds the item that the showcase will focus on.
            final ViewTarget offer_recycler_view = new ViewTarget(R.id.offer_recycler_view, this);//Variable holds the item that the showcase will focus on.
            final ViewTarget tapri_recycler_view = new ViewTarget(R.id.tapri_recycler_view, this);//Variable holds the item that the showcase will focus on.


            final Target homeTarget = new Target() {
                @Override
                public Point getPoint() {
                    // Get approximate position of home icon's center
                    int actionBarSize = toolbar.getHeight();
                    int x = toolbar.getWidth() - toolbar.getWidth() / 3;
                    int y = actionBarSize;
                    return new Point(x, y);
                }
            };

            //This code creates a new layout parameter so the button in the showcase can move to a new spot.
            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.addRule(RelativeLayout.CENTER_IN_PARENT);
            lps.addRule(RelativeLayout.CENTER_IN_PARENT);
            int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
            lps.setMargins(margin, margin, margin, margin);

            //This creates the first showcase.
            final ShowcaseView ShowCase = new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(name_updates)
                    .setContentTitle("Write Updates & Get Gift")
                    .setContentText("Write updates regarding your college and get assured gift.")
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
                            ShowCase.setTarget(Friends);
                            ShowCase.setContentTitle("Make Friends & Increase your network.");
                            ShowCase.setContentText("\n\n1. Open up People List \n\n2. Send a Tea Offer \n\n3. Make Friends");
                            ShowCase.setButtonText("next");
                            break;

                        case 2:
                            ShowCase.setTarget(offer_recycler_view);
                            ShowCase.setContentTitle("Buy Or Sell");
                            ShowCase.setContentText("On Tapri you can SELL your unused and BUY directly from people.");
                            ShowCase.setButtonText("Next");
                            break;

                        case 3:
                            ShowCase.setTarget(tapri_recycler_view);
                            ShowCase.setContentTitle("Choose your Tapri \n\n[ Tapri is a Tea Stall where friends share their thoughts]");
                            ShowCase.setContentText("\n\nSelect your branch and start sharing your ideas and it helps you to know whats going in other colleges.");
                            ShowCase.setButtonText("Next");
                            break;

                        case 4:

                            ShowCase.setTarget(homeTarget);
                            ShowCase.setContentTitle("One2One Chat");
                            ShowCase.setContentText("\n\nMessages from your friends.");
                            ShowCase.setButtonText("Close");
                            break;

                        case 5:
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
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}
