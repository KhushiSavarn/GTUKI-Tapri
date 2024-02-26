package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Request_Friends_see_all extends AppCompatActivity {
    private RecyclerView received_recycler_view;
    public adapter_request_friends Received_Adaper;
    public ArrayList<Data> receive_friends;
    private ProgressBar Progress_for_received;
    TextView txtreceived_0_row;
    Button btnrequestrecived_retry, btnrequestfriend_letssee;
    private boolean isLoaded = false, isVisibleToUser;
    Context context;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;
    String StrBranch = "", StrCollegeName = "", StrAdmissionYear = "", StrName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.request_friends_all);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font color=\"#FFFFFF\">All Friends</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ////////////////For Received REquest

        received_recycler_view = (RecyclerView) findViewById(R.id.received_recycler_view);
        RecyclerView.LayoutManager lm_Received = new GridLayoutManager(this, 2);
        received_recycler_view.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        received_recycler_view.setLayoutManager(lm_Received);

        receive_friends = new ArrayList<>();
        Received_Adaper = new adapter_request_friends(receive_friends, this);
        received_recycler_view.setAdapter(Received_Adaper);

        ///////////////////////////////////////
        Progress_for_received = (ProgressBar) findViewById(R.id.Progress_for_received);
        txtreceived_0_row = (TextView) findViewById(R.id.txtreceived_0_row);

        btnrequestrecived_retry = (Button) findViewById(R.id.btnrequestrecived_retry);
        btnrequestrecived_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fetch_Friends_Request_Received(0);
            }
        });

        btnrequestfriend_letssee = (Button) findViewById(R.id.btnrequestfriend_letssee);
        btnrequestfriend_letssee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), FriendsOnTapri.class));
            }
        });
        Fetch_Friends_Request_Received(0);
    }


    public void Fetch_Friends_Request_Received(int start_page_count) {
        //Get FromId
        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        String FromId = Iam.getId();
        //If you click on More i.e. length of data_user is Greater than 0
        //So if it is Gt 0
        // Remove data at start_page_count -> This will remove More

        try {
            if (!receive_friends.isEmpty()) {
                receive_friends.remove(receive_friends.size() - 1);
            }

            Received_Adaper.notifyDataSetChanged();
            Progress_for_received.setVisibility(View.VISIBLE);
            btnrequestfriend_letssee.setVisibility(View.INVISIBLE);
            txtreceived_0_row.setText("");

        } catch (Exception e) {
        }

        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.REQUEST_STATUS + "/" + start_page_count + "/" + FromId + "/2", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Progress_for_received.setVisibility(View.GONE);
                } catch (Exception e) {
                }

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        btnrequestrecived_retry.setVisibility(View.GONE);

                        ///Toatal Users On Tapri
                        String result_count_part = obj.getString("result_count_part");
                        //SEnd data to REsult_status

                        /////////////////////////////////////////////////////////////////////////////////////
                        //Received Invitation from Users and And show it in Second tab
                        JSONArray received_usersarray = obj.getJSONArray("Friends");
                        //SEnd data to REsult_status
                        if (received_usersarray.length() >= 1) {
                            for (int i = 0; i < received_usersarray.length(); i++) {
                                JSONObject randomuserObj = (JSONObject) received_usersarray.get(i);
                                receive_friends.add(new Data(R.drawable.user_top, randomuserObj.getString("user_id") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("institute") + "#" + randomuserObj.getString("batch") + "#" + randomuserObj.getString("branch") + "#" + randomuserObj.getString("likes") + "#" + randomuserObj.getString("isinterested") + "#" + randomuserObj.getString("status")));
                            }

                            receive_friends.add(new Data(R.drawable.user_top, "0#MORE#-#-#-#0#0#0"));
                            Received_Adaper.notifyDataSetChanged();
                            /////////////////////////////////////////////////////////////////////////////////////

                        } else {
                            receive_friends.add(new Data(R.drawable.user_top, "0#NO NEW FRIENDS#-#-#-#0#0#0"));

                        }
                        /////////////////////////////////////////////////////////
                    } else {
                        btnrequestrecived_retry.setVisibility(View.VISIBLE);
                        // error in fetching chat rooms
                        txtreceived_0_row.setText("Check Internet Connection");
                        try {
                            Progress_for_received.setVisibility(View.GONE);
                        } catch (Exception e) {
                        }
                    }

                } catch (JSONException e) {
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

                Progress_for_received.setVisibility(View.GONE);
                btnrequestrecived_retry.setVisibility(View.VISIBLE);
                txtreceived_0_row.setText("Check Internet Connection");
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_friends, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean dialog_Search(final Context context) {

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
                if (position == 1)
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
                if (position == 1)
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
                if (position == 1)
                    StrAdmissionYear = "$";
                else
                    StrAdmissionYear = spinnerbatch.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                StrAdmissionYear = "$";
            }
        });
        ////////////////////////////////////////
        alertDialogBuilder.setView(dialogView);

        alertDialogBuilder.setPositiveButton("Search",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        String Name = input.getText().toString().trim();

                        if (Name.equalsIgnoreCase(""))
                            Name = "$";

                        //4 Will Clear adapter + Do not Add "More" option to grid
                        SearchFriendsOnTapri(4, Name.trim(), StrCollegeName.trim(), StrAdmissionYear.trim(), StrBranch.trim());
                    }

                });

        alertDialogBuilder.setNegativeButton("ALL",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //3 Will Clear adapter + Add "More" option to grid
                        receive_friends.clear();
                        Fetch_Friends_Request_Received(0);
                    }

                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        return true;

    }

    public void SearchFriendsOnTapri(final int who_Calls, String name, String college, String year, String department) {

        //CAll If Internet is available
        cd = new ConnectionDetector(getBaseContext());
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            if (who_Calls == 4 || who_Calls == 3) {
                receive_friends.clear();
            }

            if (name.equalsIgnoreCase(""))
                name = "$";
            if (college.equalsIgnoreCase(""))
                college = "$";
            if (year.equalsIgnoreCase(""))
                year = "$";
            if (department.equalsIgnoreCase(""))
                department = "$";

            //If you click on More i.e. length of data_user is Greater than 0
            //So if it is Gt 0
            // Remove data at start_page_count -> This will remove More

            if (!receive_friends.isEmpty()) {
                receive_friends.remove(receive_friends.size() - 1);
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
                    EndPoints.MY_SEARCH_FRIENDS, new Response.Listener<String>() {

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

                            txtreceived_0_row.setText("");
                            btnrequestrecived_retry.setVisibility(View.GONE);
                            try {
                                progress.dismiss();
                            } catch (Exception e) {
                            }

                            //Get random_users And show it in Top Horizontal View
                            JSONArray random_usersarray = obj.getJSONArray("random_users");

                            for (int i = 0; i < random_usersarray.length(); i++) {
                                JSONObject randomuserObj = (JSONObject) random_usersarray.get(i);
                                receive_friends.add(new Data(R.drawable.user_top, randomuserObj.getString("user_id") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("institute") + "#" + randomuserObj.getString("batch") + "#" + randomuserObj.getString("branch") + "#" + randomuserObj.getString("likes") + "#" + randomuserObj.getString("isinterested")));
                            }

                            //If No Row Return Set ReturnRow = 0
                            //So Dont Show More Button
                            if (random_usersarray.length() <= 0) {
                                if (who_Calls != 4)
                                    receive_friends.add(new Data(R.drawable.next, "1#No More on Tapri"));
                                else
                                    receive_friends.add(new Data(R.drawable.next, "1#0 Search Result."));
                            } else {
                                if (who_Calls != 4) {
                                    receive_friends.add(new Data(R.drawable.next, "0#More"));
                                }
                            }
                            Received_Adaper.notifyDataSetChanged();

                            /////////////////////////////////////////////////////////
                        } else {
                            // error in fetching chat rooms
                            txtreceived_0_row.setText("Check Internet Connection");
                            btnrequestrecived_retry.setVisibility(View.VISIBLE);
                            try {
                                progress.dismiss();
                            } catch (Exception e) {
                            }

                        }

                    } catch (JSONException e) {
                        txtreceived_0_row.setText("Check Internet Connection");
                        btnrequestrecived_retry.setVisibility(View.VISIBLE);
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

                    btnrequestrecived_retry.setVisibility(View.VISIBLE);
                    try {
                        progress.dismiss();
                    } catch (Exception e) {
                    }
                    txtreceived_0_row.setText("Check Internet Connection");
                }
            }) {

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
                }

                ;
            };

            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);

        } else {
            btnrequestrecived_retry.setVisibility(View.VISIBLE);
            txtreceived_0_row.setText("Check Internet connection & Retry.");
            Toast.makeText(this, "Check Internet connection & Retry.", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}