package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class activity_address_for_gift extends AppCompatActivity {

    TextView address,btnaddress,phonenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_for_gift);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font  color=\"#FFFFFF\">Address for gift</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        address = (TextView) findViewById(R.id.address);
        address.setText(MyApplication.getInstance().getPrefManager().get_address());

        phonenumber = (TextView) findViewById(R.id.phonenumber);
        phonenumber.setText(MyApplication.getInstance().getPrefManager().get_cellnumber());

        btnaddress= (TextView) findViewById(R.id.address_save);
        btnaddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MyApplication.getInstance().getPrefManager().store_cellnumber(phonenumber.getText().toString());

                if(!address.getText().toString().equalsIgnoreCase("") && !phonenumber.getText().toString().equalsIgnoreCase(""))
                    Upload_Address_For_Gift(address.getText().toString() + " Contact:" + phonenumber.getText().toString());
                else
                    Toast.makeText(activity_address_for_gift.this, "Address and contact details are required.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int i = menuItem.getItemId();

       if (i == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }
    private void Upload_Address_For_Gift(final String address)
    {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Storing Address for your gift claim.");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.ADDRESS_FOR_GIFT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    progress.dismiss();
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false)
                    {
                        MyApplication.getInstance().getPrefManager().store_flag_gift_claimed(true);
                        MyApplication.getInstance().getPrefManager().store_address(address);
                        Toast.makeText(getBaseContext(), "Address Stored.Will get back soon.", Toast.LENGTH_LONG).show();
                    } else {
                        progress.dismiss();
                        // login error - simply toast the message
                        Toast.makeText(getBaseContext(), "Check Connection and retry."+response, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    progress.dismiss();

                    Toast.makeText(getBaseContext(), "Check Connection and retry."+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Toast.makeText(getBaseContext(), "Check Connection and retry.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", MyApplication.getInstance().getPrefManager().getUser().getName());
                params.put("college", MyApplication.getInstance().getPrefManager().getUser().getcollegename());
                params.put("address", address);
                params.put("myid", MyApplication.getInstance().getPrefManager().getUser().getId());
                params.put("email", MyApplication.getInstance().getPrefManager().getUser().getEmail());
                return params;
            }
        };


        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}