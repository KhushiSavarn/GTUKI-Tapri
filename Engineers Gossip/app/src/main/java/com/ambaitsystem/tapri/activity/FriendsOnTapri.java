package com.ambaitsystem.tapri.activity;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FriendsOnTapri extends AppCompatActivity {
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
    public HorizontalAdapter horizontalAdapter;
    public ArrayList<Data> data_user;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;

    TextView txtsent_0_row;
    TextView txtviewviewprofile;
    Button btnrequestsent_retry;
    ImageView btnsearch_friends;
    Context context;
    String StrBranch="",StrCollegeName="",StrAdmissionYear="",StrName="";
   // Tracker mTracker;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friends_on_tapri);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font color=\"#FFFFFF\">All Users</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Animate txtviewOpenTapriandChat to highlight
        txtviewviewprofile = (TextView) findViewById(R.id.txtviewviewprofile);
        animatedString(txtviewviewprofile, "View Profile , Offer a Tea N Make a friends....");

        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 2);
        horizontal_recycler_view.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        horizontal_recycler_view.setLayoutManager(lm);

        data_user = new ArrayList<>();
        horizontalAdapter = new HorizontalAdapter(data_user, this);
        horizontal_recycler_view.setAdapter(horizontalAdapter);

        txtsent_0_row = (TextView) findViewById(R.id.txtsent_0_row);

        btnrequestsent_retry = (Button) findViewById(R.id.btnrequestsent_retry);
        btnrequestsent_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchFriendsOnTapri(0, "", 3);
            }
        });

        fetchFriendsOnTapri(0, "", 5);
        // In top_hoizontal_image

        showcaseDialogTutorial();

       // mTracker = ((MyApplication) getApplication()).getDefaultTracker();

    }


    public boolean dialog_Search(final Context context)
    {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle("Search");

        LayoutInflater li = LayoutInflater.from(context);
        final View dialogView = li.inflate(R.layout.option, null);

        final EditText input = (EditText) dialogView.findViewById(R.id.txtname_search);

        /////////////////////////////////////////
        ///Spinner for College Name
        ////////////////////////////////////////
              final Spinner spincollegename = (Spinner) dialogView.findViewById(R.id.spincollegename);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        this, R.array.college_name, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spincollegename.setAdapter(adapter);
                spincollegename.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        int position = arg2 + 1;
                        //IF position is 1 i.e. Not Selected anything so pass $
                        //API convert $ to ""
                        if(position == 1)
                            StrCollegeName = "$";
                        else
                            StrCollegeName = spincollegename.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        StrCollegeName = "$";
                    }
                });
        ////////////////////////////////////////

        /////////////////////////////////////////
        ///Spinner for Branch Name
        ////////////////////////////////////////
       final Spinner spinnerbranch = (Spinner) dialogView.findViewById(R.id.spinbranch);
        ArrayAdapter<CharSequence> adapter_branch = ArrayAdapter.createFromResource(
                this, R.array.department, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbranch.setAdapter(adapter_branch);
        spinnerbranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int position = arg2 + 1;

                //IF position is 1 i.e. Not Selected anything so pass $
                //API convert $ to ""
                if(position == 1)
                    StrBranch = "$";
                else
                    StrBranch = spinnerbranch.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                StrBranch = "$";
            }
        });
        ////////////////////////////////////////

        /////////////////////////////////////////
        ///Spinner for Admission Year
        ////////////////////////////////////////
       final Spinner spinnerbatch = (Spinner) dialogView.findViewById(R.id.spinyear);
        ArrayAdapter<CharSequence> adapter_batch = ArrayAdapter.createFromResource(
                this, R.array.yearofadmission, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbatch.setAdapter(adapter_batch);
        spinnerbatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                int position = arg2 + 1;
                //IF position is 1 i.e. Not Selected anything so pass $
                //API convert $ to ""
                if(position == 1)
                    StrAdmissionYear = "$";
                else
                    StrAdmissionYear = spinnerbatch.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                StrAdmissionYear = "$";
            }
        });
        ////////////////////////////////////////
        alertDialogBuilder.setView(dialogView);

        alertDialogBuilder.setPositiveButton("Search",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id)
                    {
                        String Name = input.getText().toString().trim();

                        if(Name.equalsIgnoreCase(""))
                            Name = "$";

                        //4 Will Clear adapter + Do not Add "More" option to grid
                        SearchFriendsOnTapri(4,Name.trim(),StrCollegeName.trim(),StrAdmissionYear.trim(),StrBranch.trim());
                    }

                });

        alertDialogBuilder.setNegativeButton("ALL",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        //3 Will Clear adapter + Add "More" option to grid
                        fetchFriendsOnTapri(0, "", 3);
                    }

                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        return true;

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
        valueAnimator.setDuration(3000);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        /*
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query))
                    fetchFriendsOnTapri(0, query.trim(), 4);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

        }); */
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_search:
                dialog_Search(context);
                break;

            case R.id.action_lastonline:
                fetchFriendsOnTapri_lastonline(0,"$",4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchFriendsOnTapri_lastonline(int start_page_count, String Keyword, final int who_Calls) {

        //CAll If Internet is available
        cd = new ConnectionDetector(getBaseContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent)
        {
            if (who_Calls == 4 || who_Calls == 3)
            {
                data_user.clear();
            }

            if (Keyword.equalsIgnoreCase("")) {
                Keyword = "$";
            }

            txtviewviewprofile.setText("List : Most Recent Online Users.");
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
            progress.setMessage("Listing Recently Online users...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.show();

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    EndPoints.FRIENDS_ON_TAPRI_LASTONLINE + "/" + start_page_count + "/" + FromId + "/" + Keyword, new Response.Listener<String>() {

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
                            JSONArray random_usersarray = obj.getJSONArray("random_users");

                            for (int i = 0; i < random_usersarray.length(); i++) {
                                JSONObject randomuserObj = (JSONObject) random_usersarray.get(i);
                                data_user.add(new Data(R.drawable.user_top, randomuserObj.getString("user_id") + "#" + randomuserObj.getString("name") + "\nLast seen :" +randomuserObj.getString("last_online") + "#" + randomuserObj.getString("institute") + "#" + randomuserObj.getString("batch") + "#" + randomuserObj.getString("branch") + "#" + randomuserObj.getString("likes") +"#"+ randomuserObj.getString("isinterested")));
                            }

                            //If No Row Return Set ReturnRow = 0
                            //So Dont Show More Button
                            if (random_usersarray.length() <= 0) {
                                if (who_Calls != 4)
                                    data_user.add(new Data(R.drawable.next, "1#No More on Tapri"));
                                else
                                    data_user.add(new Data(R.drawable.next, "1#0 Search Result."));
                            } else {
                                if (who_Calls != 4) {
                                    data_user.add(new Data(R.drawable.next, "0#More"));
                                }
                            }
                            horizontalAdapter.notifyDataSetChanged();
                            MyApplication.getInstance().getRequestQueue().stop();
                            /////////////////////////////////////////////////////////
                        } else {
                            // error in fetching chat rooms
                            txtsent_0_row.setText("Not able to load Friends of Tapri,Please Retry.\n[Check Internet Connection.#1]");
                            btnrequestsent_retry.setVisibility(View.VISIBLE);
                            try {
                                progress.dismiss();
                            } catch (Exception e) {
                            }

                        }

                    } catch (JSONException e) {
                        txtsent_0_row.setText("Not able to load Friends of Tapri,Please Retry.\n[Check Internet Connection.#2]");
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
                    txtsent_0_row.setText("Not able to load Friends of Tapri,Please Retry.\n[Check Internet Connection.#3]");
                }
            });

            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);

        } else {
            txtsent_0_row.setText("Check Internet connection & Retry.");
            btnrequestsent_retry.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Check Internet connection & Retry.", Toast.LENGTH_SHORT).show();
        }

    }


    public void fetchFriendsOnTapri(int start_page_count, String Keyword, final int who_Calls) {

        //CAll If Internet is available
        cd = new ConnectionDetector(getBaseContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent)
        {
            if (who_Calls == 4 || who_Calls == 3)
            {
                data_user.clear();
            }

            if (Keyword.equalsIgnoreCase("")) {
                Keyword = "$";
            }

            btnrequestsent_retry.setVisibility(View.GONE);
            txtsent_0_row.setText("");

            txtviewviewprofile.setText("List : Users");
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
            progress.setMessage("Listing Friends...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.show();

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    EndPoints.FRIENDS_ON_TAPRI + "/" + start_page_count + "/" + FromId + "/" + Keyword, new Response.Listener<String>() {

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
                            JSONArray random_usersarray = obj.getJSONArray("random_users");

                            for (int i = 0; i < random_usersarray.length(); i++) {
                                JSONObject randomuserObj = (JSONObject) random_usersarray.get(i);
                                data_user.add(new Data(R.drawable.user_top, randomuserObj.getString("user_id") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("institute") + "#" + randomuserObj.getString("batch") + "#" + randomuserObj.getString("branch") + "#" + randomuserObj.getString("likes") + "#" + randomuserObj.getString("isinterested")));
                            }

                            //If No Row Return Set ReturnRow = 0
                            //So Dont Show More Button
                            if (random_usersarray.length() <= 0) {
                                if (who_Calls != 4)
                                    data_user.add(new Data(R.drawable.next, "1#No More on Tapri"));
                                else
                                    data_user.add(new Data(R.drawable.next, "1#0 Search Result."));
                            } else {
                                if (who_Calls != 4) {
                                    data_user.add(new Data(R.drawable.next, "0#More"));
                                }
                            }
                            horizontalAdapter.notifyDataSetChanged();

                            /////////////////////////////////////////////////////////
                        } else {
                            // error in fetching chat rooms
                            txtsent_0_row.setText("Not able to load Friends of Tapri,Please Retry.\n[Check Internet Connection.#1]");
                            btnrequestsent_retry.setVisibility(View.VISIBLE);
                            try {
                                progress.dismiss();
                            } catch (Exception e) {
                            }

                        }

                    } catch (JSONException e) {
                        txtsent_0_row.setText("Not able to load Friends of Tapri,Please Retry.\n[Check Internet Connection.#2]");
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
                    txtsent_0_row.setText("Not able to load Friends of Tapri,Please Retry.\n[Check Internet Connection.#3]");
                }
            });

            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);

        } else
        {
            btnrequestsent_retry.setVisibility(View.VISIBLE);
            txtsent_0_row.setText("Check Internet connection & Retry.");
            Toast.makeText(this, "Check Internet connection & Retry.", Toast.LENGTH_SHORT).show();
        }

    }

    public void SearchFriendsOnTapri(final int who_Calls,String name,String college,String year,String department) {

        //CAll If Internet is available
        cd = new ConnectionDetector(getBaseContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent)
        {
            if (who_Calls == 4 || who_Calls == 3)
            {
                data_user.clear();
            }

            if (name.equalsIgnoreCase(""))
                name = "$";
            if (college.equalsIgnoreCase(""))
                college = "$";
            if (year.equalsIgnoreCase(""))
                year = "$";
            if (department.equalsIgnoreCase(""))
                department = "$";

            btnrequestsent_retry.setVisibility(View.GONE);
            txtsent_0_row.setText("");

            txtviewviewprofile.setText("List : Search criteria applied.");

            //If you click on More i.e. length of data_user is Greater than 0
            //So if it is Gt 0
            // Remove data at start_page_count -> This will remove More

            if (!data_user.isEmpty()) {
                data_user.remove(data_user.size() - 1);
            }

            //Call Asynch task to offer a Tea
            progress = new ProgressDialog(this);
            progress.setMessage("Listing Friends...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.show();

            final String finalName = name;
            final String finalCollege = college;
            final String finalYear = year;
            final String finalDepartment = department;


            StringRequest strReq = new StringRequest(Request.Method.POST,
                    EndPoints.SEARCH_FRIENDS, new Response.Listener<String>() {

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

                            txtsent_0_row.setText("");
                            btnrequestsent_retry.setVisibility(View.GONE);
                            try {
                                progress.dismiss();
                            } catch (Exception e) {
                            }

                            //Get random_users And show it in Top Horizontal View
                            JSONArray random_usersarray = obj.getJSONArray("random_users");

                            for (int i = 0; i < random_usersarray.length(); i++) {
                                JSONObject randomuserObj = (JSONObject) random_usersarray.get(i);
                                data_user.add(new Data(R.drawable.user_top, randomuserObj.getString("user_id") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("institute") + "#" + randomuserObj.getString("batch") + "#" + randomuserObj.getString("branch") + "#" + randomuserObj.getString("likes") + "#" + randomuserObj.getString("isinterested")));
                            }

                            //If No Row Return Set ReturnRow = 0
                            //So Dont Show More Button
                            if (random_usersarray.length() <= 0) {
                                if (who_Calls != 4)
                                    data_user.add(new Data(R.drawable.next, "1#No More on Tapri"));
                                else
                                    data_user.add(new Data(R.drawable.next, "1#0 Search Result."));
                            } else {
                                if (who_Calls != 4) {
                                    data_user.add(new Data(R.drawable.next, "0#More"));
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
            }){

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("myid", MyApplication.getInstance().getPrefManager().getUser().getId());
                    params.put("name", finalName);
                    params.put("college", finalCollege);
                    params.put("year", finalYear);
                    params.put("department", finalDepartment);

                    //Log.e(TAG, "Params: " + params.toString());

                    return params;
                };
            };

            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);

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

            final Target homeTarget = new Target() {
                @Override
                public Point getPoint() {
                    // Get approximate position of home icon's center
                    int actionBarSize = toolbar.getHeight();
                    int x = toolbar.getWidth() - 100;
                    int y = actionBarSize ;
                    return new Point(x, y);
                }
            };
            //This creates the first showcase.
            final ShowcaseView ShowCase = new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(homeTarget)
                    .setContentTitle("View/Search Profile")
                    .setContentText("\nView Profile,Offer a tea and make friends")
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
                            ShowCase.setTarget(horizontal_recycler_view);
                            ShowCase.setContentTitle("Users On Tapri");
                            ShowCase.setContentText("\nView Profile..\n\nLiked??\n    Offer a cup of Tea.\n\nAccepted?\n    Hurrey,Go With One 2 One Chat.");
                            ShowCase.setButtonText("Close");
                            break;

                        case 2:
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

       // mTracker.setScreenName("Friend_list_Screen");
      //  mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}
