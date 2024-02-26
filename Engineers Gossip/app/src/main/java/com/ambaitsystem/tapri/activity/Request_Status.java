package com.ambaitsystem.tapri.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.vgecchat.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Request_Status extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final String TAG_USER = "data";
    private static final String TAG_VERSION_CODE = "vcode";
    JSONArray user = null;
    private String myVersionName;
    ShowcaseView sv;
 //   Tracker mTracker;


    private RecyclerView horizontal_recycler_view;
    public ArrayList<Data> data_user;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_status);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font  color=\"#FFFFFF\">Friends</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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
            try {
                myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
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

     //   mTracker = ((MyApplication) getApplication()).getDefaultTracker();
     //   mTracker = ((MyApplication) getApplication()).getDefaultTracker();

       FloatingActionButton fab_addfriends = (FloatingActionButton)findViewById(R.id.fab_addfriends);
        fab_addfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Request_Status.this,FriendsOnTapri.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

      //  mTracker.setScreenName("Status_screen_1");
      //  mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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

    //What is Invitation or Invites
    public void dispatchInformations(String count, String what) {
        try {

            if (what.equalsIgnoreCase("invitation")) {
                tabLayout.getTabAt(2).setText(count + "\nReceived");
                //animatedNumber(btn1_invitation,Integer.valueOf(count));
            }
            if (what.equalsIgnoreCase("invited")) {
                tabLayout.getTabAt(1).setText(count + "\nSent");

                //animatedNumber(btn1_invited,Integer.valueOf(count));
            }
            if (what.equalsIgnoreCase("Friends")) {
                tabLayout.getTabAt(0).setText(count + "+\nFriends");

                //animatedNumber(btn1_friends,Integer.valueOf(count));
            }


        } catch (Exception e) {
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Request_Friends(), "-\nFriends");
        adapter.addFragment(new Request_Sent(), "-\nSent");
        adapter.addFragment(new Request_Received(), "-\nReceived");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
