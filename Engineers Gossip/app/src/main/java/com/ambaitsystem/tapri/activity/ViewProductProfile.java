package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
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

public class ViewProductProfile extends AppCompatActivity
{
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewproductprofile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        context = this;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        getWindow().setLayout(screenWidth, ActionBar.LayoutParams.WRAP_CONTENT);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        //product_id,product_name,product_price,product_category,description,creator_name,
        // creator_id,creator_college,cell,isactive
        // get data via the key
        final String product_id = extras.getString("product_id");
        final String product_name = extras.getString("product_name");

        String product_price = extras.getString("product_price");
  //      String product_category = extras.getString("product_category");

        String description = extras.getString("description");
        String creator_name = extras.getString("creator_name");

//        String creator_id = extras.getString("creator_id");
        String creator_college = extras.getString("creator_college");

       final String cell = extras.getString("cell");


        TextView txtname = (TextView)findViewById(R.id.txtName);
        txtname.setText(product_name);

        TextView txtcollegeName = (TextView)findViewById(R.id.txtCollege);
        txtcollegeName.setText(creator_college);

        TextView txtBatch = (TextView)findViewById(R.id.txtBatch);
        txtBatch.setText(description);

        TextView txtDepartment = (TextView)findViewById(R.id.txtDepartment);
        txtDepartment.setText(creator_name);

        TextView txtcontact = (TextView)findViewById(R.id.txtcontact);
        txtcontact.setText(cell);


        TextView txtviewNoOfTimesView = (TextView)findViewById(R.id.txtviewNoOfTimesView);
        txtviewNoOfTimesView.setText(product_price + " Rs. Only");
        TextView txtviewCalltobuy = (TextView)findViewById(R.id.txtviewCalltobuy);
        txtviewCalltobuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", cell, null));
                startActivity(intent);
                /*Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+cell));

                if (ActivityCompat.checkSelfPermission(ViewProductProfile.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);*/
            }
        });

        //Load Image Using ID
        LoadImage(this,product_id);

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
                        Toast.makeText(context, "Not able to offer tea now,Please Retry.#1", Toast.LENGTH_LONG).show();
                        try
                        { progress.dismiss();}catch (Exception e){}
                    }
                    else if(obj.getString("status_Of_Offer").equalsIgnoreCase("2"))
                    {
                        Toast.makeText(context, "Already offered a tea.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(context, "Not able to offer tea now,Please Retry.#2", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error In Offering tea,Please Retry.", Toast.LENGTH_LONG).show();
                    try
                    { progress.dismiss();}catch (Exception e1){}
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e("FriendsOnTapri", "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(context, "Error In Offering tea,Please Retry.", Toast.LENGTH_LONG).show();
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

    private void LoadImage(ViewProductProfile context, String id)
    {
        ImageView Imgview = (ImageView) findViewById(R.id.profile_imageview);

        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent)
        {

            String URL_ForImage ="http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/product/" +id +".jpg";
            ImageLoader imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.product)
                    .showImageOnFail(R.drawable.product)
                    .showImageOnLoading(R.drawable.product_loading).build();

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
}