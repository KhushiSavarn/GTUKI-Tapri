package com.ambaitsystem.tapri.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class activity_mark_premium extends AppCompatActivity {

    public String ProductId = null,Name,Price;
TextView Message,Balance;
    LinearLayout layout_done;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_premium);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Bundle extras = getIntent().getExtras();
        context  =this;

        if (extras != null) {
            ProductId = extras.getString("ProductId");
            Name = extras.getString("Name");
            Price = extras.getString("Price");
            // and get whatever type user account id is
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font  color=\"#FFFFFF\">Premium</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView Product_details= (TextView) findViewById(R.id.Product_Details);
        Product_details.setText("Product Details:\n\nName : "+Name +"\nPrice : "+ Price);


        layout_done = (LinearLayout) findViewById(R.id.layout_done);
        Message = (TextView) findViewById(R.id.Message);
        Balance = (TextView) findViewById(R.id.Balance);

        TextView txtview_done = (TextView) findViewById(R.id.txtview_done);
        txtview_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView txtview_Buy_balance = (TextView) findViewById(R.id.txtview_Buy_balance);
        txtview_Buy_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_For_Buy_Credit();
            }
        });

        TextView mark_premium = (TextView) findViewById(R.id.mark_premium);
        mark_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mark_Premium(ProductId);
            }
        });

    }

    private void Dialog_For_Buy_Credit()
    {
        //Set ProductId To Premium
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.custom_dialog_buy_balance);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","info.aitsystems@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Buy credit for listing as a Premium user");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Write your Phone Number and details here");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                dialog.dismiss();

            }
        });

        Button dialogButtonNotNow = (Button) dialog.findViewById(R.id.dialogButtonNotNow);
        // if button is clicked, close the custom dialog
        dialogButtonNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void Mark_Premium(final String productId) {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Making Product as premium");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.UPDATE_TO_PRIME, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    layout_done.setVisibility(View.VISIBLE);
                    progress.dismiss();
                    JSONObject obj = new JSONObject(response);
                    Balance.setVisibility(View.VISIBLE);
                    Message.setVisibility(View.VISIBLE);

                    Balance.setText("Remaining Balance is : " +obj.getString("balance"));
                    Message.setText(obj.getString("message"));

                } catch (JSONException e) {
                    progress.dismiss();

                    Toast.makeText(getBaseContext(), "Check Connection and retry." + e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("productid", productId);
                params.put("id", MyApplication.getInstance().getPrefManager().getUser().getId());
                params.put("planeddays", "7");
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
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}