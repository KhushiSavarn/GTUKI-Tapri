package com.ambaitsystem.tapri.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;

public class Request_Friends extends Fragment {
    private RecyclerView received_recycler_view;
    public adapter_request_friends Received_Adaper;
    public ArrayList<Data> receive_friends;
    private ProgressBar Progress_for_received;
    TextView txtreceived_0_row;
    Button btnrequestrecived_retry,btnrequestfriend_letssee;
    private boolean isLoaded = false, isVisibleToUser;

    public Request_Friends() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.request_friends, container, false);

        ////////////////For Received REquest

        received_recycler_view = (RecyclerView) rootview.findViewById(R.id.received_recycler_view);
        RecyclerView.LayoutManager lm_Received = new GridLayoutManager(getContext(),2);
        received_recycler_view.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        received_recycler_view.setLayoutManager(lm_Received);

        receive_friends = new ArrayList<>();
        Received_Adaper = new adapter_request_friends(receive_friends, getContext());
        received_recycler_view.setAdapter(Received_Adaper);

        ///////////////////////////////////////
        Progress_for_received = (ProgressBar) rootview.findViewById(R.id.Progress_for_received);
        txtreceived_0_row = (TextView) rootview.findViewById(R.id.txtreceived_0_row);

        btnrequestrecived_retry = (Button) rootview.findViewById(R.id.btnrequestrecived_retry);
        btnrequestrecived_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fetch_Friends_Request_Received(0);
            }
        });

        btnrequestfriend_letssee = (Button) rootview.findViewById(R.id.btnrequestfriend_letssee);
        btnrequestfriend_letssee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), FriendsOnTapri.class));
            }
        });

        if (isVisibleToUser && (!isLoaded)) {
            Fetch_Friends_Request_Received(0);
            isLoaded = true;
        }
        // Inflate the layout for this fragment
        return rootview;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && isAdded()) {
            Fetch_Friends_Request_Received(0);
            isLoaded = true;
        }
    }

    public void Fetch_Friends_Request_Received(int start_page_count) {
        //Get FromId
        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        String FromId = Iam.getId();
        //If you click on More i.e. length of data_user is Greater than 0
        //So if it is Gt 0
        // Remove data at start_page_count -> This will remove More

        try {
            receive_friends.clear();
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
                        try{((Request_Status) getActivity()).dispatchInformations("" + result_count_part, "result_count_part");}catch(Exception e){}

                        /////////////////////////////////////////////////////////////////////////////////////
                        //Received Invitation from Users and And show it in Second tab
                        JSONArray received_usersarray = obj.getJSONArray("Friends");

                        //SEnd data to REsult_status
                       try{ ((Request_Status) getActivity()).dispatchInformations("" + received_usersarray.length(), "Friends");}catch (Exception e){}

                        if (received_usersarray.length() >= 1) {
                            for (int i = 0; i < received_usersarray.length(); i++) {
                                JSONObject randomuserObj = (JSONObject) received_usersarray.get(i);
                                receive_friends.add(new Data(R.drawable.user_top, randomuserObj.getString("user_id") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("institute") + "#" + randomuserObj.getString("batch") + "#" + randomuserObj.getString("branch") + "#" + randomuserObj.getString("likes") + "#" + randomuserObj.getString("isinterested") + "#" + randomuserObj.getString("status")));
                            }

                            receive_friends.add(new Data(R.drawable.user_top, "0#SEE ALL#-#-#-#0#0#0"));
                            Received_Adaper.notifyDataSetChanged();
                            /////////////////////////////////////////////////////////////////////////////////////

                        } else {
                            btnrequestfriend_letssee.setVisibility(View.VISIBLE);
                            txtreceived_0_row.setText("No Friends?\nView Profile,Offer a Tea and Make Friends");

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


}