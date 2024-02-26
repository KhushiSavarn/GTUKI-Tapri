package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.tapri.model.User;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class activity_news_sub_list extends AppCompatActivity {

    private RecyclerView news_sub_recycler_view;
    public adapter_my_activity_listing horizontalAdapter;
    public ArrayList<Data> data_user;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;
    Context context;
    TextView txtsent_0_row;
    Button btnrequestsent_retry;
    String newsID = null,news_details=null;
    SwipeRefreshLayout swiperefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_sub);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        context = this;
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            newsID = extras.getString("newsID");
            news_details = extras.getString("news_details");
            // and get whatever type user account id is
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml(news_details));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        news_sub_recycler_view = (RecyclerView) findViewById(R.id.news_sub_recycler_view);
        news_sub_recycler_view.setLayoutManager(new LinearLayoutManager(this));

        data_user = new ArrayList<>();
        horizontalAdapter = new adapter_my_activity_listing(data_user, this);
        news_sub_recycler_view.setAdapter(horizontalAdapter);

        txtsent_0_row = (TextView) findViewById(R.id.txtsent_0_row);
        /*TextView txtNews = (TextView) findViewById(R.id.txtNews);
        txtNews.setText(news_details);*/

                btnrequestsent_retry = (Button) findViewById(R.id.btnrequestsent_retry);
        btnrequestsent_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fetch_news_details(newsID);
            }
        });

        Fetch_news_details(newsID);

        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swiperefresh.setColorScheme(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fetch_news_details(newsID);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_addnewsdetail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent i = new Intent(activity_news_sub_list.this, activity_news_detail_upload.class);
                i.putExtra("newsID",newsID);
                startActivity(i);
            }
        });
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
    private void Fetch_news_details(String news_master_id)
    {
        //CAll If Internet is available
        cd = new ConnectionDetector(getBaseContext());
        isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent)
        {
            data_user.clear();
            btnrequestsent_retry.setVisibility(View.GONE);
            txtsent_0_row.setText("");

            //Get FromId
            User Iam = MyApplication.getInstance().getPrefManager().getUser();
            String FromId = Iam.getId();
            //If you click on More i.e. length of data_user is Greater than 0
            //So if it is Gt 0
            // Remove data at start_page_count -> This will remove More

            //Call Asynch task to offer a Tea
            progress = new ProgressDialog(this);
            progress.setMessage("Listing News...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.show();

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    EndPoints.LIST_MY_NACTIVITY + "/" + news_master_id + "/" + FromId, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        swiperefresh.setRefreshing(false);
                        progress.dismiss();
                    } catch (Exception e) {
                    }

                    try {

                        JSONObject obj = new JSONObject(response);

                        // check for error flag
                        if (obj.getBoolean("error") == false)
                        {
                            btnrequestsent_retry.setVisibility(View.GONE);
                            try {
                                progress.dismiss();

                            } catch (Exception e) {
                            }

                            //Get random_users And show it in Top Horizontal View
                            JSONArray random_usersarray = obj.getJSONArray("activity_listing");
                            if (random_usersarray.length() <= 0) {
                                data_user.add(new Data(R.drawable.product, "1#0#No activity available.#0#0"));
                            }
                            for (int i = 0; i < random_usersarray.length(); i++)
                            {
                                JSONObject randomuserObj = (JSONObject) random_usersarray.get(i);

                                if(i==random_usersarray.length() - 1)
                                {
                                    //load image in imgbackground
                                    ImageView imgbackground = (ImageView)findViewById(R.id.imgbackground);
                                    String URL_ForImage = "http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/news/" + randomuserObj.getString("news_detail_id") + ".jpg";
                                    ImageLoader imageLoader = ImageLoader.getInstance();

                                    DisplayImageOptions options;
                                    options = new DisplayImageOptions.Builder().cacheInMemory(true)
                                            .cacheOnDisc(true).resetViewBeforeLoading(true)
                                            .showImageForEmptyUri(R.drawable.background)
                                            .showImageOnFail(R.drawable.background)
                                            .showImageOnLoading(R.drawable.background).build();

                                    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                                            context)

                                            .defaultDisplayImageOptions(options)
                                            .memoryCache(new WeakMemoryCache())
                                            .discCacheSize(100 * 1024 * 1024).build();

                                    ImageLoader.getInstance().init(config);
                                    //download and display image from url
                                    imageLoader.displayImage(URL_ForImage, imgbackground, options);
                                }
                                //		//product_id,product_name,product_price,product_category,description,creator_name,creator_id,creator_college,cell,isactive
                                data_user.add(new Data(R.drawable.product, randomuserObj.getString("news_detail_id")
                                        + "#" + randomuserObj.getString("news_id")
                                        + "#" + randomuserObj.getString("details")
                                        + "#" + randomuserObj.getString("createdon")
                                        + "#" + randomuserObj.getString("name")
                                ));
                            }

                            horizontalAdapter.notifyDataSetChanged();

                            /////////////////////////////////////////////////////////
                        } else {
                            swiperefresh.setRefreshing(false);

                            // error in fetching chat rooms
                            txtsent_0_row.setText("Check Internet Connection");
                            btnrequestsent_retry.setVisibility(View.VISIBLE);
                            try {
                                progress.dismiss();

                            } catch (Exception e) {
                            }

                        }

                    } catch (JSONException e) {
                        swiperefresh.setRefreshing(false);

                        txtsent_0_row.setText("Check Internet Connection");
                        btnrequestsent_retry.setVisibility(View.VISIBLE);
                        try {
                            progress.dismiss();

                        } catch (Exception exc) {
                        }

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    swiperefresh.setRefreshing(false);
                    btnrequestsent_retry.setVisibility(View.VISIBLE);
                    try {
                        progress.dismiss();

                    } catch (Exception e) {
                    }
                    txtsent_0_row.setText("Check Internet Connection");
                }
            });

            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);

        } else {
            swiperefresh.setRefreshing(false);
            txtsent_0_row.setText("Check Internet Connection");
            btnrequestsent_retry.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Check Internet connection & Retry.", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}