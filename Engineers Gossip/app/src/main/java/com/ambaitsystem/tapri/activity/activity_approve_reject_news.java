package com.ambaitsystem.tapri.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.tapri.helper.SpacesItemDecoration;
import com.ambaitsystem.tapri.model.User;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class activity_approve_reject_news extends AppCompatActivity {
    public adapter_approve_reject_news Received_Adaper;
    public ArrayList<Data> receive_news;
    private Toolbar toolbar;

    private ProgressBar Progress_for_received;
    TextView txtreceived_0_row, txtviewGo_fetch_news;
    Button btnrequestrecived_retry;
    private boolean isLoaded = false, isVisibleToUser;

    SwipeRefreshLayout swipeView;
    LinearLayout  No_newsLayout;
    SwipeRefreshLayout swiperefresh;
    Boolean flag_shown = false;
    String[] ArrDateList;
    int checkedItem = 0;
    Context Objcontext;

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////////////////For Received REquest
        setContentView(R.layout.activity_approve_reject);
        Objcontext = this;
        RecyclerView received_recycler_view = (RecyclerView) findViewById(R.id.received_recycler_view);
        RecyclerView.LayoutManager lm_Received = new GridLayoutManager(this, 1);
        received_recycler_view.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        received_recycler_view.setLayoutManager(lm_Received);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font  color=\"#FFFFFF\">News Approval</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        receive_news = new ArrayList<>();
        Received_Adaper = new adapter_approve_reject_news(receive_news, this, receive_news);
        received_recycler_view.setAdapter(Received_Adaper);

        ///////////////////////////////////////
        Progress_for_received = (ProgressBar) findViewById(R.id.Progress_for_received);
        txtreceived_0_row = (TextView) findViewById(R.id.txtreceived_0_row);



        btnrequestrecived_retry = (Button) findViewById(R.id.btnrequestrecived_retry);
        btnrequestrecived_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fetch_News_Request_Received(Objcontext);
            }
        });


        Fetch_News_Request_Received(Objcontext);

        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        No_newsLayout = (LinearLayout) findViewById(R.id.No_newsLayout);


        swipeView = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeView.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fetch_News_Request_Received(Objcontext);
            }
        });


    }

    private int Set_Spinner_default_to_own_college_name() {
        String[] mTestArray;
        mTestArray = getResources().getStringArray(R.array.college_name);
        String CollegeName = MyApplication.getInstance().getPrefManager().getUser().getcollegename().trim();
        int index = -1;
        for (int i = 0; i < mTestArray.length; i++) {
            if (mTestArray[i].equals(CollegeName)) {
                index = i;
                break;
            }
        }
        return  index;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    public void Fetch_News_Request_Received(final Context context) {

        //Get FromId
        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        final String MYCollege = MyApplication.getInstance().getPrefManager().getUser().getcollegename().trim();
        String FromId = Iam.getId();
        int College_index = Set_Spinner_default_to_own_college_name();
        //If you click on More i.e. length of data_user is Greater than 0
        //So if it is Gt 0
        // Remove data at start_page_count -> This will remove More
        try {
            receive_news.clear();
            Received_Adaper.notifyDataSetChanged();
            Progress_for_received.setVisibility(View.VISIBLE);

            txtreceived_0_row.setText("");

        } catch (Exception e) {
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());


        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.REQUEST_ALL_NEWS_TO_APPROVE + "/" + FromId + "/" + formattedDate + "/" + College_index, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    swipeView.setRefreshing(false);
                    Progress_for_received.setVisibility(View.GONE);
                } catch (Exception e) {
                }

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false)

                    {
                        int left = 121 - obj.getInt("my_uploaded_news_count");
                        Boolean flag_claimed = MyApplication.getInstance().getPrefManager().get_flag_gift_claimed();
                        if (obj.getInt("my_uploaded_news_count") >= EndPoints.Thrashold_gift_on_news && flag_shown == false && flag_claimed == false) {
                            flag_shown = true;
                            Show_Dialog_to_Claim_Gift(context, obj.getInt("my_uploaded_news_count"));
                        }
                        //Parse all news
                        //Received Invitation from Users and And show it in Second tab

                        JSONArray received_usersarray = obj.getJSONArray("DATES");
                        if (received_usersarray.length() >= 1) {
                            ArrDateList = new String[received_usersarray.length()];
                            for (int i = 0; i < received_usersarray.length(); i++) {
                                JSONObject randomuserObj = (JSONObject) received_usersarray.get(i);
                                ArrDateList[i] = randomuserObj.getString("dates");
                            }
                        } else {
                            ArrDateList = new String[1];
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            String formattedDate = df.format(c.getTime());
                            ArrDateList[0] = formattedDate;
                        }

                        if (obj.getInt("news_count") > 0) {
                            No_newsLayout.setVisibility(View.GONE);
                            swiperefresh.setVisibility(View.VISIBLE);
                            btnrequestrecived_retry.setVisibility(View.GONE);

                            //---------------------------------
                            received_usersarray = obj.getJSONArray("COLLEGE");

                            if (received_usersarray.length() >= 1)
                                receive_news.add(new Data(R.drawable.user_top, "0#COLLEGE#0#0#0#0"));
                            if (received_usersarray.length() >= 1) {
                                for (int i = 0; i < received_usersarray.length(); i++) {
                                    JSONObject randomuserObj = (JSONObject) received_usersarray.get(i);
                                    receive_news.add(new Data(R.drawable.user_top, randomuserObj.getString("news_cat_id") + "#" + randomuserObj.getString("details") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("news_id") + "#" + randomuserObj.getString("liked") + "#" + randomuserObj.getString("dislike")));
                                }

                                /////////////////////////////////////////////////////////////////////////////////////

                            }
                            /////////////////////////////////////////////////////////
                            received_usersarray = obj.getJSONArray("DEPARTMENT");
                            if (received_usersarray.length() >= 1)
                                receive_news.add(new Data(R.drawable.user_top, "0#DEPARTMENT#0#0#0#0"));
                            if (received_usersarray.length() >= 1) {
                                for (int i = 0; i < received_usersarray.length(); i++) {
                                    JSONObject randomuserObj = (JSONObject) received_usersarray.get(i);
                                    receive_news.add(new Data(R.drawable.user_top, randomuserObj.getString("news_cat_id") + "#" + randomuserObj.getString("details") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("news_id") + "#" + randomuserObj.getString("liked") + "#" + randomuserObj.getString("dislike")));
                                }


                                /////////////////////////////////////////////////////////////////////////////////////

                            }

                            /////////////////////////////////////////////////////////
                            received_usersarray = obj.getJSONArray("HOSTEL");
                            if (received_usersarray.length() >= 1)
                                receive_news.add(new Data(R.drawable.user_top, "0#HOSTEL#0#0#0#0"));
                            if (received_usersarray.length() >= 1) {
                                for (int i = 0; i < received_usersarray.length(); i++) {
                                    JSONObject randomuserObj = (JSONObject) received_usersarray.get(i);
                                    receive_news.add(new Data(R.drawable.user_top, randomuserObj.getString("news_cat_id") + "#" + randomuserObj.getString("details") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("news_id") + "#" + randomuserObj.getString("liked") + "#" + randomuserObj.getString("dislike")));
                                }

                                /////////////////////////////////////////////////////////////////////////////////////

                            }
                            /////////////////////////////////////////////////////////
                            received_usersarray = obj.getJSONArray("CAREER");
                            if (received_usersarray.length() >= 1)
                                receive_news.add(new Data(R.drawable.user_top, "0#CAREER#0#0#0#0"));
                            if (received_usersarray.length() >= 1) {
                                for (int i = 0; i < received_usersarray.length(); i++) {
                                    JSONObject randomuserObj = (JSONObject) received_usersarray.get(i);
                                    receive_news.add(new Data(R.drawable.user_top, randomuserObj.getString("news_cat_id") + "#" + randomuserObj.getString("details") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("news_id") + "#" + randomuserObj.getString("liked") + "#" + randomuserObj.getString("dislike")));
                                }

                                /////////////////////////////////////////////////////////////////////////////////////

                            }

                            /////////////////////////////////////////////////////////
                            received_usersarray = obj.getJSONArray("EVENTS");
                            if (received_usersarray.length() >= 1)
                                receive_news.add(new Data(R.drawable.user_top, "0#EVENTS#0#0#0#0"));
                            if (received_usersarray.length() >= 1) {
                                for (int i = 0; i < received_usersarray.length(); i++) {
                                    JSONObject randomuserObj = (JSONObject) received_usersarray.get(i);
                                    receive_news.add(new Data(R.drawable.user_top, randomuserObj.getString("news_cat_id") + "#" + randomuserObj.getString("details") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("news_id") + "#" + randomuserObj.getString("liked") + "#" + randomuserObj.getString("dislike")));
                                }

                                /////////////////////////////////////////////////////////////////////////////////////

                            }
                            /////////////////////////////////////////////////////////
                            received_usersarray = obj.getJSONArray("OTHER");
                            if (received_usersarray.length() >= 1)
                                receive_news.add(new Data(R.drawable.user_top, "0#OTHER#0#0#0#0"));
                            if (received_usersarray.length() >= 1) {
                                for (int i = 0; i < received_usersarray.length(); i++) {
                                    JSONObject randomuserObj = (JSONObject) received_usersarray.get(i);
                                    receive_news.add(new Data(R.drawable.user_top, randomuserObj.getString("news_cat_id") + "#" + randomuserObj.getString("details") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("news_id") + "#" + randomuserObj.getString("liked") + "#" + randomuserObj.getString("dislike")));
                                }

                                /////////////////////////////////////////////////////////////////////////////////////

                            }

                        } else {
                            No_newsLayout.setVisibility(View.VISIBLE);

                            swiperefresh.setVisibility(View.GONE);
                        }
                        Received_Adaper.notifyDataSetChanged();

                    } else {
                        swipeView.setRefreshing(false);

                        btnrequestrecived_retry.setVisibility(View.VISIBLE);
                        // error in fetching chat rooms
                        txtreceived_0_row.setText("Check Internet Connection");
                        try {
                            Progress_for_received.setVisibility(View.GONE);
                        } catch (Exception e) {
                        }
                    }

                } catch (JSONException e) {
                    swipeView.setRefreshing(false);
                    Log.e("FriendsOnTapri", "json parsing error: " + e.getMessage());
                    btnrequestrecived_retry.setVisibility(View.VISIBLE);
                    txtreceived_0_row.setText("Check Internet Connection");
                    try {
                        Progress_for_received.setVisibility(View.GONE);
                    } catch (Exception e1) {
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                swipeView.setRefreshing(false);
                Progress_for_received.setVisibility(View.GONE);
                btnrequestrecived_retry.setVisibility(View.VISIBLE);
                txtreceived_0_row.setText("Check Internet Connection");
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void Show_Dialog_to_Claim_Gift(Context activity, int my_uploaded_news_count) {
        // custom dialog
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.custom_dialog);

        TextView txtview = (TextView) dialog.findViewById(R.id.text1);
        txtview.setText("Hurrey.. " + my_uploaded_news_count + " Reached.");

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_approve_reject_news.this, activity_address_for_gift.class));
                dialog.dismiss();

            }
        });

        Button dialogButtonNotNow = (Button) dialog.findViewById(R.id.dialogButtonNotNow);
        // if button is clicked, close the custom dialog
        dialogButtonNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}