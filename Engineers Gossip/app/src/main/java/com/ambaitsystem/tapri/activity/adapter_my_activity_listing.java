package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.vgecchat.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Collections;
import java.util.List;


public class adapter_my_activity_listing extends RecyclerView.Adapter<adapter_my_activity_listing.MyViewHolder> {


    List<Data> horizontalList = Collections.emptyList();
    Context context;

    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    private Integer Next_count = 0;
    private ProgressDialog progress;


    public adapter_my_activity_listing(List<Data> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView news_detail_image;
        TextView news_detail_user_id, news_detail_text;
        TextView news_detail_name, news_detail_date;
        LinearLayout news_updates;
        public MyViewHolder(View view) {
            super(view);
            news_detail_image = (ImageView) view.findViewById(R.id.news_detail_image);
            news_updates = (LinearLayout) view.findViewById(R.id.news_updates) ;
            news_detail_user_id = (TextView) view.findViewById(R.id.news_detail_user_id);
            news_detail_text = (TextView) view.findViewById(R.id.news_detail_text);
            news_detail_name = (TextView) view.findViewById(R.id.news_detail_name);
            news_detail_date = (TextView) view.findViewById(R.id.news_detail_date);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conent_news_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (horizontalList.get(position).txt.contains("#")) {
            /*randomuserObj.getString("news_detail_id")
            + "#" + randomuserObj.getString("news_id")
                    + "#" + randomuserObj.getString("details")
                    + "#" + randomuserObj.getString("createdon")
                    + "#" + randomuserObj.getString("name")*/
            String Name_withhas = horizontalList.get(position).txt;
            final String[] Name_withouthash = Name_withhas.split("#");
            ///////////////////////
            holder.news_detail_user_id.setText(Name_withouthash[0]);
            holder.news_detail_text.setText(Name_withouthash[2]);
            holder.news_detail_name.setText(Name_withouthash[4]);
            holder.news_detail_date.setText(Name_withouthash[3]);

            cd = new ConnectionDetector(context);
            isInternetPresent = cd.isConnectingToInternet();

            // check for Internet status
            if (isInternetPresent) {

                String URL_ForImage = "http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/news/" + Name_withouthash[0] + ".jpg";
                ImageLoader imageLoader = ImageLoader.getInstance();

                String MyId = MyApplication.getInstance().getPrefManager().getUser().getId();
                DisplayImageOptions options;
                options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .displayer(new RoundedBitmapDisplayer(1000))
                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                        .showImageForEmptyUri(R.drawable.no_activity)
                        .showImageOnFail(R.drawable.no_activity)
                        .showImageOnLoading(R.drawable.no_activity).build();

                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                        context)

                        .defaultDisplayImageOptions(options)
                        .memoryCache(new WeakMemoryCache())
                        .discCacheSize(100 * 1024 * 1024).build();

                ImageLoader.getInstance().init(config);
                //download and display image from url
                imageLoader.displayImage(URL_ForImage, holder.news_detail_image, options);

            }

            holder.news_updates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,ViewFullActivity.class);
                    i.putExtra("news_id",Name_withouthash[0]);
                    i.putExtra("description",Name_withouthash[2]);
                    context.startActivity(i);

                }
            });

        }

        /////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}