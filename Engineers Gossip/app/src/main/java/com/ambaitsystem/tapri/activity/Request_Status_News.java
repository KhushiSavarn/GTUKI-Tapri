package com.ambaitsystem.tapri.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
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
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.vgecchat.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Request_Status_News extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final String TAG_USER = "data";
    private static final String TAG_VERSION_CODE = "vcode";
    JSONArray user = null;
    private String myVersionName;
    ShowcaseView sv;
  //  Tracker mTracker;


    private RecyclerView horizontal_recycler_view;
    public ArrayList<Data> data_user;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_status_news);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font  color=\"#FFFFFF\">Updates</font></b>"));
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

        showcaseDialogTutorial();

      //  mTracker = ((MyApplication) getApplication()).getDefaultTracker();

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

    @Override
    protected void onResume() {
        super.onResume();

     //   mTracker.setScreenName("Status_screen_1");
     //   mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    //What is Invitation or Invites
    public void dispatchInformations(String count, String what) {
        try {

            if (what.equalsIgnoreCase("Updates")) {
                tabLayout.getTabAt(0).setText(count + "+\nUpdates");

                //animatedNumber(btn1_friends,Integer.valueOf(count));
            }

        } catch (Exception e) {
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        /*ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Request_News(), "College Updates");
        viewPager.setAdapter(adapter);*/
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

    private void showcaseDialogTutorial() {
        final SharedPreferences tutorialShowcases = getSharedPreferences("showcase_tapri_status_screen", MODE_PRIVATE);

        boolean run;

        run = tutorialShowcases.getBoolean("run?", true);

        if (run) {//If the buyer already went through the showcases it won't do it again.
            final ViewTarget btnusers = new ViewTarget(R.id.btn1_ontapri, this);//Variable holds the item that the showcase will focus on.
            final ViewTarget btntapri = new ViewTarget(R.id.btn1_gototapri, this);

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
                    .setTarget(btnusers)
                    .setContentTitle("")
                    .setContentText("")
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
                            ShowCase.setTarget(btnusers);
                            ShowCase.setContentTitle("Users");
                            ShowCase.setContentText("\n\n1. Open up Users List \n\n2. Send a Tea Offer \n\n3. Make Friends");
                            ShowCase.setButtonText("next");
                            break;

                        case 2:
                            ShowCase.setTarget(btntapri);
                            ShowCase.setContentTitle("Tapri");
                            ShowCase.setContentText("\n\nOpenup Tapri to Chat in Group of your branch.");
                            ShowCase.setButtonText("Next");
                            break;

                        case 3:

                            ShowCase.setTarget(homeTarget);
                            ShowCase.setContentTitle("One2One Chat");
                            ShowCase.setContentText("\n\nMessages from your friends.");
                            ShowCase.setButtonText("Close");
                            break;

                        case 4:
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
