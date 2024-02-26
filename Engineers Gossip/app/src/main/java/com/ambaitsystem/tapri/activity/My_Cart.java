package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.tapri.helper.DbBasic;
import com.ambaitsystem.tapri.helper.SpacesItemDecoration;
import com.ambaitsystem.vgecchat.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by USER on 3/20/2017.
 */

public class My_Cart extends AppCompatActivity {
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
    public adapter_my_cart horizontalAdapter;
    public ArrayList<Data> data_user;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;

    TextView txtsent_0_row;
    Button btnrequestsent_retry;
    ImageView btnsearch_friends;
    Context context;
   // Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_cart);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font color=\"#FFFFFF\">My Cart</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 2);
        horizontal_recycler_view.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        horizontal_recycler_view.setLayoutManager(lm);

        data_user = new ArrayList<>();
        horizontalAdapter = new adapter_my_cart(data_user, My_Cart.this);
        horizontal_recycler_view.setAdapter(horizontalAdapter);

        txtsent_0_row = (TextView) findViewById(R.id.txtsent_0_row);

        btnrequestsent_retry = (Button) findViewById(R.id.btnrequestsent_retry);
        btnrequestsent_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchProductFromCart(0, 1);
            }
        });

        fetchProductFromCart(0, 5);

        showcaseDialogTutorial();

      //  mTracker = ((MyApplication) getApplication()).getDefaultTracker();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.menu_myads, menu);

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

    public void fetchProductFromCart(int start_page_count, final int who_Calls)
    {

        //CAll If Internet is available
        cd = new ConnectionDetector(getBaseContext());
        isInternetPresent = cd.isConnectingToInternet();

        btnrequestsent_retry.setVisibility(View.GONE);
        txtsent_0_row.setText("");

        // check for Internet status
        if (isInternetPresent)
        {
            if (who_Calls == 4 || who_Calls == 3)
            {
                data_user.clear();
            }

            if (!data_user.isEmpty()) {
                data_user.remove(data_user.size() - 1);
            }

            SQLiteDatabase db = null;

            db =(new DbBasic(this)).getReadableDatabase();
            Cursor constantsCursor = db.rawQuery("SELECT DISTINCT product_id,product_name,creator_name,cell FROM cart",null);

            if(constantsCursor.getCount() >0) {
                constantsCursor.moveToFirst();
                startManagingCursor(constantsCursor);

                try {
                    while (!constantsCursor.isAfterLast()) {
                        data_user.add(new Data(R.drawable.product, constantsCursor.getString(0).toString() + "#" + constantsCursor.getString(1).toString() + "#" + constantsCursor.getString(2).toString() + "#" + constantsCursor.getString(3).toString()));
                        constantsCursor.moveToNext();
                    }
                } catch (Exception e)
                {
                    Toast.makeText(context, "" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }else {
                data_user.add(new Data(R.drawable.product, 0 + "#" + "No More Products" + "#" + "-" + "#" + "-"));
            }
            horizontalAdapter.notifyDataSetChanged();

        } else {
            btnrequestsent_retry.setVisibility(View.VISIBLE);
            txtsent_0_row.setText("Check Internet connection & Retry.");
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
                    .setContentTitle("Cart")
                    .setContentText("\nDirectly get in touch of the person and buy an items.")
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

                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

      //  mTracker.setScreenName("Friend_list_Screen");
      //  mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}