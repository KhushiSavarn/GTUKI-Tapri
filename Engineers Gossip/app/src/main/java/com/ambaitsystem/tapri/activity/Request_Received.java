package com.ambaitsystem.tapri.activity;

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

public class Request_Received extends Fragment{
    private RecyclerView received_recycler_view;
    public adapter_request_received Received_Adaper;
    public  ArrayList<Data> receive_user ;
    private ProgressBar Progress_for_received;
    TextView txtreceived_0_row;
    Button btnrequestrecived_retry;
    public Request_Received() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receive_user = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootview = inflater.inflate(R.layout.request_received, container, false);

        ////////////////For Received REquest

        received_recycler_view= (RecyclerView)rootview.findViewById(R.id.received_recycler_view);
        RecyclerView.LayoutManager lm_Received = new GridLayoutManager(getContext(),2);
        received_recycler_view.addItemDecoration(new SpacesItemDecoration(EndPoints.Grid_spacing));
        received_recycler_view.setLayoutManager(lm_Received);


        Received_Adaper = new adapter_request_received(receive_user , getContext());
        received_recycler_view.setAdapter(Received_Adaper);

        ///////////////////////////////////////
        Progress_for_received = (ProgressBar)rootview.findViewById(R.id.Progress_for_received);

        txtreceived_0_row =(TextView) rootview.findViewById(R.id.txtreceived_0_row);

        btnrequestrecived_retry = (Button)rootview.findViewById(R.id.btnrequestrecived_retry);
        btnrequestrecived_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fetch_Request_Received(0);
            }
        });


        // Inflate the layout for this fragment
        return rootview;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Fetch_Request_Received(0);
        }
    }
    public void Fetch_Request_Received(int start_page_count)
    {
        //Get FromId
        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        String FromId = Iam.getId();
        //If you click on More i.e. length of data_user is Greater than 0
        //So if it is Gt 0
        // Remove data at start_page_count -> This will remove More

        try {
            receive_user.clear();
            Received_Adaper.notifyDataSetChanged();
            Progress_for_received.setVisibility(View.VISIBLE);
            txtreceived_0_row.setText("");
        }catch (Exception e){}



        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.REQUEST_STATUS + "/"  + start_page_count +"/" + FromId +"/1", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try{ Progress_for_received.setVisibility(View.GONE);}catch (Exception e){}

                try {
                    JSONObject obj = new JSONObject(response);
                      // check for error flag
                    if (obj.getBoolean("error") == false)
                    {
                        btnrequestrecived_retry.setVisibility(View.GONE);

                        //Set Version Code
                        MyApplication.getInstance().getPrefManager().storeversion_code (obj.getString("vcode"));

                        //Set
                        /////////////////////////////////////////////////////////////////////////////////////
                        //Received Invitation from Users and And show it in Second tab
                        JSONArray received_usersarray = obj.getJSONArray("Invitation_From_users");

                        //SEnd data to REsult_status
                       try{ ((Request_Status)getActivity()).dispatchInformations(""+received_usersarray.length(),"invitation");}catch (Exception e){}

                        if(received_usersarray.length() >=1)
                        {
                            for (int i = 0; i < received_usersarray.length(); i++)
                            {
                                JSONObject randomuserObj = (JSONObject) received_usersarray.get(i);
                                receive_user.add(new Data(R.drawable.user_top, randomuserObj.getString("user_id") + "#" + randomuserObj.getString("name") + "#" + randomuserObj.getString("institute") + "#" + randomuserObj.getString("batch") + "#" + randomuserObj.getString("branch") + "#" + randomuserObj.getString("likes") + "#" + randomuserObj.getString("isinterested")+ "#" + randomuserObj.getString("status")));
                            }

                            //If No Row Return Set ReturnRow = 0
                            //So Dont Show More Button
                       /* if(received_usersarray.length() <= 0)
                            receive_user.add(new Data(R.drawable.next,"1#No More on Tapri"));
                        else
                            receive_user.add(new Data(R.drawable.next,"0#More"));*/
                            Received_Adaper.notifyDataSetChanged();
                            /////////////////////////////////////////////////////////////////////////////////////

                        }
                        else
                        {txtreceived_0_row.setText("No Tea request received yet.");

                        }
                        /////////////////////////////////////////////////////////
                    } else {
                        btnrequestrecived_retry.setVisibility(View.VISIBLE);
                        // error in fetching chat rooms
                        txtreceived_0_row.setText("Check Internet Connection");

                        try
                        { Progress_for_received.setVisibility(View.GONE);}catch (Exception e){}
                    }

                } catch (JSONException e) {
                    Log.e("FriendsOnTapri", "json parsing error: " + e.getMessage());
                    btnrequestrecived_retry.setVisibility(View.VISIBLE);
                    txtreceived_0_row.setText("Check Internet Connection");
                    try
                    {Progress_for_received.setVisibility(View.GONE);}catch (Exception e1){}
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