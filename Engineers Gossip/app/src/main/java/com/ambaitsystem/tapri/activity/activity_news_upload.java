package com.ambaitsystem.tapri.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class activity_news_upload extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    TextView txtcollege_news,news_save;
    EditText Edittxtnews;
    private Spinner Spinner_category;
    String strnews_category;
    int intnews_category_index=0;
Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_news);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font  color=\"#FFFFFF\">Upload Updates and Get a Gift</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtcollege_news = (TextView) findViewById(R.id.txtcollege_news);
        txtcollege_news.setText("Report "+ EndPoints.Thrashold_gift_on_news +" college Updates like achievements,conference,workshops etc and get gurranted gift.");
                Edittxtnews = (EditText) findViewById(R.id.Edittxtnews);

        Spinner_category = (Spinner) findViewById(R.id.spinner_news_category);
        Spinner_category.setOnItemSelectedListener(this);
        bind_category_To_Spinner();

        news_save= (TextView) findViewById(R.id.news_save);
        news_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(!Edittxtnews.getText().toString().equalsIgnoreCase("") && intnews_category_index !=0 ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Report Updates?");
                    builder.setMessage("Are you sure to report this Updates?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Upload_news(Edittxtnews.getText().toString(), intnews_category_index);
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else
                    Toast.makeText(activity_news_upload.this, "Updates Details and category are required.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bind_category_To_Spinner()
    {

        String[] mTestArray;

        try {
            mTestArray = getResources().getStringArray(R.array.newscategory);

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, mTestArray);

            // attaching data adapter to spinner
            Spinner_category.setAdapter(dataAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Updates Category Not Bound,Please Retry!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {


            switch (parent.getId()) {
                case R.id.spinner_news_category:
                    // On selecting a spinner item
                    strnews_category= parent.getItemAtPosition(position).toString();
                    intnews_category_index = position;
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        strnews_category = "Select";
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

    private void Upload_news(final String news_details, final int intnews_category_index)
    {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Reporting Updates...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.REPORTING_NEWS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    progress.dismiss();
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false)
                    {
                        Toast.makeText(getBaseContext(), "Updates reported.It Will be in Listing in a while.", Toast.LENGTH_LONG).show();
                        txtcollege_news.setText("Updates reported.\nIt Will be available in Listing after approval.");
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
                params.put("myid", MyApplication.getInstance().getPrefManager().getUser().getId());
                params.put("college_index",String.valueOf(fetch_college_Index_fromName()));
                params.put("news_category_index", String.valueOf(intnews_category_index));
                params.put("news_details",news_details);
                params.put("my_email", MyApplication.getInstance().getPrefManager().getUser().getEmail());

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

    private Integer fetch_college_Index_fromName()
    {
        Integer College_Index=0;
        String[] mTestArray;
        mTestArray = getResources().getStringArray(R.array.college_name);
        String CollegeName = MyApplication.getInstance().getPrefManager().getUser().getcollegename().trim();
        int index = -1;
        for (int i = 0; i < mTestArray.length; i++) {
            if (mTestArray[i].equals(CollegeName)) {
                index = i;
                College_Index = index;
                break;
            }
        }
        return  College_Index;
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}