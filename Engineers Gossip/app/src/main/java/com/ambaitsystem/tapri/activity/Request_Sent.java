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

public class Request_Sent extends Fragment {
    private RecyclerView sent_recycler_view;
    public adapter_request_Sent Send_Adapter;
    public ArrayList<Data> sent_user;
    private ProgressBar progress;
    TextView txtsent_0_row;
    Button btnrequestsent_retry,btnrequestsent_letssee;
    private boolean isLoaded = false, isVisibleToUser;

    public Request_Sent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sent_user = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.request_sent, container, false);

        ////////////////For Sent Request

        sent_recycler_view = (RecyclerView) rootview.findViewById(R.id.sent_recycler_view);
        RecyclerView.LayoutManager lm_sent = new GridLayoutManager(getContext(), 2);
        sent_recycler_view.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        sent_recycler_view.setLayoutManager(lm_sent);


        Send_Adapter = new adapter_request_Sent(sent_user, getContext());
        sent_recycler_view.setAdapter(Send_Adapter);

        ///////////////////////////////////////
        progress = (ProgressBar) rootview.findViewById(R.id.Progress_for_sent);
        txtsent_0_row = (TextView) rootview.findViewById(R.id.txtsent_0_row);

        btnrequestsent_retry = (Button) rootview.findViewById(R.id.btnrequestsent_retry);
        btnrequestsent_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fetch_Request_Sent(0);
            }
        });

        btnrequestsent_letssee = (Button) rootview.findViewById(R.id.btnrequestsent_letssee);
        btnrequestsent_letssee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), FriendsOnTapri.class));
            }
        });

        if (isVisibleToUser && (!isLoaded)) {
            Fetch_Request_Sent(0);
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
            Fetch_Request_Sent(0);
            isLoaded = true;
        }
    }

    public void Fetch_Request_Sent(int start_page_count) {
        //Get FromId
        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        String FromId = Iam.getId();
        //If you click on More i.e. length of data_user is Greater than 0
        //So if it is Gt 0
        // Remove data at start_page_count -> This will remove More

        try {
            sent_user.clear();
            Send_Adapter.notifyDataSetChanged();
        } catch (Exception e) {
        }
        btnrequestsent_letssee.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        txtsent_0_row.setText("");

        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.REQUEST_STATUS + "/" + start_page_count + "/" + FromId + "/0", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    progress.setVisibility(View.GONE);
                } catch (Exception e) {
                }

                try {
                    JSONObject obj = new JSONObject(response);
                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        btnrequestsent_retry.setVisibility(View.GONE);
                        /////////////////////////////////////////////////////////////////////////////////////
                        //Sent Invitation to User and And show it in First tab
                        JSONArray sent_usersarray = obj.getJSONArray("Invited_users");
                        try {
                            ((Request_Status) getActivity()).dispatchInformations("" + sent_usersarray.length(), "invited");
                        } catch (Exception e) {
                        }

                        if (sent_usersarray.length() >= 1) {
                            for (int i = 0; i < sent_usersarray.length(); i++) {
                                JSONObject randomuserObj = (JSONObject) sent_usersarray.get(i);
                                sent_user.add(new Data(R.drawable.user_top, randomuserObj.getString("user_id") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("institute") + "#" + randomuserObj.getString("batch") + "#" + randomuserObj.getString("branch") + "#" + randomuserObj.getString("likes") + "#" + randomuserObj.getString("isinterested") + "#" + randomuserObj.getString("status")));
                            }

                            //If No Row Return Set ReturnRow = 0
                            //So Dont Show More Button
                               /* if(sent_usersarray.length() <= 0)
                                    sent_user.add(new Data(R.drawable.next,"1#No More on Tapri"));
                                else
                                    sent_user.add(new Data(R.drawable.next,"0#More"));*/
                            Send_Adapter.notifyDataSetChanged();
                            /////////////////////////////////////////////////////////////////////////////////////
                        } else {
                            btnrequestsent_letssee.setVisibility(View.VISIBLE);
                            txtsent_0_row.setText("Not offered a tea yet?\nView Profile,Offer a Tea and Make Friends");
                        }
                    } else {
                        btnrequestsent_retry.setVisibility(View.VISIBLE);
                        // error in fetching chat rooms
                        txtsent_0_row.setText("Check Internet Connection");
                        try {
                            progress.setVisibility(View.GONE);
                        } catch (Exception e) {
                        }
                    }

                } catch (JSONException e) {
                    Log.e("FriendsOnTapri", "json parsing error: " + e.getMessage());
                    btnrequestsent_retry.setVisibility(View.VISIBLE);
                    txtsent_0_row.setText("Check Internet Connection");
                    try {
                        progress.setVisibility(View.GONE);
                    } catch (Exception e1) {
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                progress.setVisibility(View.GONE);
                btnrequestsent_retry.setVisibility(View.VISIBLE);
                txtsent_0_row.setText("Check Internet Connection");
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

}