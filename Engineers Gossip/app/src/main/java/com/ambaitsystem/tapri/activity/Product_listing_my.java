package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.tapri.helper.SpacesItemDecoration;
import com.ambaitsystem.tapri.model.User;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Product_listing_my extends AppCompatActivity {
    /*
    Whocalls:
    Retry :1
    More / HorizontalAdapter : 2
    Cancel Search :3
    Search : 4
    Oncreate : 5
    adapter_request_Sent : 6
     */
    private RecyclerView horizontal_recycler_view;
    public adapter_my_product_listing horizontalAdapter;
    public ArrayList<Data> data_user;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;

    TextView txtsent_0_row;
    Button btnrequestsent_retry;
    Context context;
   // Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_products_on_tapri);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font color=\"#FFFFFF\">My Ads</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 2);
        horizontal_recycler_view.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        horizontal_recycler_view.setLayoutManager(lm);

        data_user = new ArrayList<>();
        horizontalAdapter = new adapter_my_product_listing(data_user, this);
        horizontal_recycler_view.setAdapter(horizontalAdapter);

        txtsent_0_row = (TextView) findViewById(R.id.txtsent_0_row);

        btnrequestsent_retry = (Button) findViewById(R.id.btnrequestsent_retry);
        btnrequestsent_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchProductOnTapri(0, 3);
            }
        });

        fetchProductOnTapri(0, 5);

        showcaseDialogTutorial();

      //  mTracker = ((MyApplication) getApplication()).getDefaultTracker();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent i = new Intent(Product_listing_my.this, Product_My_Upload.class);
                finish();
                startActivity(i);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_myads, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_addnewads:
                Intent i = new Intent(Product_listing_my.this, Product_My_Upload.class);
                finish();
                startActivity(i);
                return true;
            case R.id.action_refresh:
            fetchProductOnTapri(0,3);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchProductOnTapri(int start_page_count, final int who_Calls) {

        //CAll If Internet is available
        cd = new ConnectionDetector(getBaseContext());
        isInternetPresent = cd.isConnectingToInternet();


        // check for Internet status
        if (isInternetPresent) {
            if (who_Calls == 4 || who_Calls == 3) {
                data_user.clear();
            }

            btnrequestsent_retry.setVisibility(View.GONE);
            txtsent_0_row.setText("");

            //Get FromId
            User Iam = MyApplication.getInstance().getPrefManager().getUser();
            String FromId = Iam.getId();
            //If you click on More i.e. length of data_user is Greater than 0
            //So if it is Gt 0
            // Remove data at start_page_count -> This will remove More

            if (!data_user.isEmpty()) {
                data_user.remove(data_user.size() - 1);
            }

            //Call Asynch task to offer a Tea
            progress = new ProgressDialog(this);
            progress.setMessage("Listing Products...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.show();

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    EndPoints.LIST_MY_PRODUCTS_ON_TAPRI + "/" + start_page_count + "/" + FromId, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        progress.dismiss();
                    } catch (Exception e) {
                    }

                    try {

                        JSONObject obj = new JSONObject(response);

                        // check for error flag
                        if (obj.getBoolean("error") == false) {


                            btnrequestsent_retry.setVisibility(View.GONE);
                            try {
                                progress.dismiss();

                            } catch (Exception e) {
                            }

                            //Get random_users And show it in Top Horizontal View
                            JSONArray random_usersarray = obj.getJSONArray("product_listing");

                            for (int i = 0; i < random_usersarray.length(); i++) {
                                JSONObject randomuserObj = (JSONObject) random_usersarray.get(i);
                                //		//product_id,product_name,product_price,product_category,description,creator_name,creator_id,creator_college,cell,isactive

                                data_user.add(new Data(R.drawable.product, randomuserObj.getString("product_id")
                                        + "#" + randomuserObj.getString("product_name")
                                        + "#" + randomuserObj.getString("product_price")
                                        + "#" + randomuserObj.getString("product_category")
                                        + "#" + randomuserObj.getString("description")
                                        + "#" + randomuserObj.getString("creator_name")
                                        + "#" + randomuserObj.getString("creator_id")
                                        + "#" + randomuserObj.getString("creator_college")
                                        + "#" + randomuserObj.getString("cell")
                                        + "#" + randomuserObj.getString("isactive")
                                        + "#" + randomuserObj.getString("primium_product")

                                ));
                            }

                            //If No Row Return Set ReturnRow = 0
                            //So Dont Show More Button
                            if (random_usersarray.length() <= 0) {
                                if (who_Calls != 4)
                                    data_user.add(new Data(R.drawable.product, "1#No More products"));
                                else
                                    data_user.add(new Data(R.drawable.product, "1#0 Search Result."));
                            } else {
                                if (who_Calls != 4) {
                                    data_user.add(new Data(R.drawable.product, "0#More"));
                                }
                            }
                            horizontalAdapter.notifyDataSetChanged();

                            /////////////////////////////////////////////////////////
                        } else {
                            // error in fetching chat rooms
                            txtsent_0_row.setText("Check Internet Connection");
                            btnrequestsent_retry.setVisibility(View.VISIBLE);
                            try {
                                progress.dismiss();

                            } catch (Exception e) {
                            }

                        }

                    } catch (JSONException e) {
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
                    NetworkResponse networkResponse = error.networkResponse;

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
            txtsent_0_row.setText("Check Internet Connection");
            btnrequestsent_retry.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Check Internet connection & Retry.", Toast.LENGTH_SHORT).show();
        }

    }


    private void showcaseDialogTutorial() {
        final SharedPreferences tutorialShowcases = getSharedPreferences("showcase_tapri_friendsontapri", MODE_PRIVATE);

        boolean run;

        run = tutorialShowcases.getBoolean("run?", true);

        if (run) {//If the buyer already went through the showcases it won't do it again.
            final ViewTarget horizontal_recycler_view = new ViewTarget(R.id.horizontal_recycler_view, this);//Variable holds the item that the showcase will focus on.

            //This code creates a new layout parameter so the button in the showcase can move to a new spot.
            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.addRule(RelativeLayout.CENTER_IN_PARENT);
            lps.addRule(RelativeLayout.CENTER_IN_PARENT);
            int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
            lps.setMargins(margin, margin, margin, margin);

            //This creates the first showcase.
            final ShowcaseView ShowCase = new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(horizontal_recycler_view)
                    .setContentTitle("My Products")
                    .setContentText("Add your Unused Items here.")
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
    protected void onResume() {
        super.onResume();
     //   mTracker.setScreenName("Friend_list_Screen");
      //  mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}
