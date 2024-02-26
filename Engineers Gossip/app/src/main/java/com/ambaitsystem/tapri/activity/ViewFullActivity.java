package com.ambaitsystem.tapri.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.vgecchat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ViewFullActivity extends AppCompatActivity
{
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    Context context;
    private Bitmap bitmap;
    ImageView news_imageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewfullactivity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        getWindow().setLayout(screenWidth, ActionBar.LayoutParams.WRAP_CONTENT);

        context = this;

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        //product_id,product_name,product_price,product_category,description,creator_name,
        // creator_id,creator_college,cell,isactive
        // get data via the key
        final String news_id = extras.getString("news_id");
        final String description = extras.getString("description");

        news_imageview = (ImageView) findViewById(R.id.profile_imageview);
        TextView txtname = (TextView)findViewById(R.id.txtdescription);
        txtname.setText(description);

        //Load Image Using ID
        LoadImage(this,news_id);

    }

    private void LoadImage(ViewFullActivity context, String id)
    {


        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent)
        {


            String URL_ForImage ="http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/news/" +id +".jpg";
            ImageLoader imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                    .cacheOnDisk(false).cacheInMemory(false)
                    .showImageForEmptyUri(R.drawable.no_activity)
                    .showImageOnFail(R.drawable.no_activity)
                    .showImageOnLoading(R.drawable.no_activity).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    context)
                    .defaultDisplayImageOptions(options)
                    .build()  ;

            ImageLoader.getInstance().init(config);
            //download and display image from url
            imageLoader.displayImage(URL_ForImage, news_imageview, options);

        }
        else
        {
            news_imageview.setImageResource(R.drawable.user_loading);
        }
    }






}