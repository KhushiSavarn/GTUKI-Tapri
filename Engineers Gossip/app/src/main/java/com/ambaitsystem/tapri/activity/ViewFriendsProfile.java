package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.model.User;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewFriendsProfile extends AppCompatActivity
{
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewfriendsprofile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        context = this;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        getWindow().setLayout(screenWidth, ActionBar.LayoutParams.WRAP_CONTENT);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        // get data via the key
        final String id = extras.getString("id");
        final String name = extras.getString("name");
        String college = extras.getString("college");
        String batch = extras.getString("batch");
        String timesview = extras.getString("timesview");
        String department = extras.getString("department");
       //Increment the View COunt
        try {
            Increase_View(id);
        }catch (Exception e){}

        TextView txtname = (TextView)findViewById(R.id.txtName);
        txtname.setText(name);

        TextView txtcollegeName = (TextView)findViewById(R.id.txtCollege);
        txtcollegeName.setText(college);

        TextView txtBatch = (TextView)findViewById(R.id.txtBatch);
        txtBatch.setText(batch + " Batch");

        TextView txtDepartment = (TextView)findViewById(R.id.txtDepartment);
        txtDepartment.setText(department);

        TextView txtviewNoOfTimesView = (TextView)findViewById(R.id.txtviewNoOfTimesView);
        txtviewNoOfTimesView.setText(timesview + " Views");

        //Load Image Using ID
        LoadImage(this,id);


        TextView OfferTea = (TextView)findViewById(R.id.OfferTea);
        OfferTea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Offer_Tea(context, id, name);
            }
        });

    }

    private void Offer_Tea(final Context context, String ToId, final String Name)
    {
        //Call Asynch task to offer a Tea
        progress=new ProgressDialog(context);
        progress.setMessage("Offering tea to " + Name);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();

        //Get FromId
        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        String FromId = Iam.getId();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.OFFER_TEA + "/"  + FromId + "/" + ToId , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try{ progress.dismiss();}catch (Exception e){}

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getString("status_Of_Offer").equalsIgnoreCase("1"))
                    {

                        Toast.makeText(context, "Tea Offered to "+ Name, Toast.LENGTH_LONG).show();
                        /////////////////////////////////////////////////////////
                    } else if(obj.getString("status_Of_Offer").equalsIgnoreCase("0")) {
                        // error in fetching chat rooms
                        Toast.makeText(context, "Check connection and retry", Toast.LENGTH_LONG).show();
                        try
                        { progress.dismiss();}catch (Exception e){}
                    }
                    else if(obj.getString("status_Of_Offer").equalsIgnoreCase("2"))
                    {
                        Toast.makeText(context, "Already offered a tea.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(context, "Check connection and retry", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    try{ progress.dismiss();}catch (Exception e1){}
                    Toast.makeText(context, "Check connection and retry", Toast.LENGTH_LONG).show();
                    try
                    { progress.dismiss();}catch (Exception e1){}
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                try{ progress.dismiss();}catch (Exception e){}

                Toast.makeText(context, "Check connection and retry", Toast.LENGTH_LONG).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void Increase_View(String ToId)
    {

        //Get FromId
        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        String FromId = Iam.getId();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.INCREASE_VIEW_COUNT + "/"  + ToId  , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void LoadImage(ViewFriendsProfile context, String id)
    {
        ImageView Imgview = (ImageView) findViewById(R.id.profile_imageview);

        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent)
        {

            String URL_ForImage ="http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/" +id +".jpg";
            ImageLoader imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.user_top)
                    .showImageOnFail(R.drawable.user_top)
                    .showImageOnLoading(R.drawable.user_loading).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    context)
                    .defaultDisplayImageOptions(options)
                    .memoryCache(new WeakMemoryCache())
                    .discCacheSize(100 * 1024 * 1024).build()  ;

            ImageLoader.getInstance().init(config);
            //download and display image from url
            imageLoader.displayImage(URL_ForImage, Imgview, options);

        }
        else
        {
            Imgview.setImageResource(R.drawable.user_loading);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}