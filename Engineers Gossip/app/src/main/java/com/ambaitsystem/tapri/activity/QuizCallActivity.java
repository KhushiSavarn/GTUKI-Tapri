package com.ambaitsystem.tapri.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.tapri.helper.SpacesItemDecoration;
import com.ambaitsystem.vgecchat.R;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;


public class QuizCallActivity extends AppCompatActivity {

    private RecyclerView horizontal_recycler_view;
    public QuizCallAdapter horizontalAdapter;
    public ArrayList<Data> data_user;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    Context context;
   // Tracker mTracker;
    private ArrayList<String> TopicList=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_call);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font color=\"#FFFFFF\">Quiz Topics</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        horizontal_recycler_view = (RecyclerView) findViewById(R.id.recycler_view_quiz_topic);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 2);
        horizontal_recycler_view.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        horizontal_recycler_view.setLayoutManager(lm);

        //Prepare Data
        TopicList.add(0,"6#AGRICULTIRE");
        TopicList.add(1,"5#CONSTITUTION");
        TopicList.add(2,"4#PANCHAYATI RAJ");
        TopicList.add(3,"3#WOMAN EMPOERMENT");
        TopicList.add(4,"2#SPORTS");
        TopicList.add(5,"1#GEOGRAPHY");

        horizontalAdapter = new QuizCallAdapter(this,TopicList);
        horizontal_recycler_view.setAdapter(horizontalAdapter);
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

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }

}
