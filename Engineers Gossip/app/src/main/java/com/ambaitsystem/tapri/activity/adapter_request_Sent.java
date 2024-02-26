package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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


public class adapter_request_Sent extends RecyclerView.Adapter<adapter_request_Sent.MyViewHolder> {


    List<Data> horizontalList = Collections.emptyList();
    Context context;

    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    public  Integer Next_count=0;
    private ProgressDialog progress;


    public adapter_request_Sent(List<Data> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView txtview,txtoffertea;
        TextView txtstatus,txtviewfriends;
        LinearLayout Layout_forBottomDetails,entrieslistcontent;

        public MyViewHolder(View view) {
            super(view);
            imageView=(ImageView) view.findViewById(R.id.imageview);
            txtstatus = (TextView) view.findViewById(R.id.txtstatus);
            txtviewfriends = (TextView) view.findViewById(R.id.txtviewfriends);
            txtoffertea = (TextView) view.findViewById(R.id.txtoffertea);
            Layout_forBottomDetails = (LinearLayout) view.findViewById(R.id.request_sent_bottom);
            entrieslistcontent = (LinearLayout) view.findViewById(R.id.entrieslistcontent);
            txtview =(TextView) view.findViewById(R.id.txtview);

        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_request_sent_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //Data recieved in following format :
        //user_id"#"name#institute"#"batch"#"branch#likes#isinterested
        //holder.imageView.setImageResource(horizontalList.get(position).imageId);


        //In this case offer a tea should be status
        //if 3

        if(horizontalList.get(position).txt.contains("#"))
        {
            String Name_withhas = horizontalList.get(position).txt;
            String[] Name_withouthash = Name_withhas.split("#");
            holder.txtview.setText(Name_withouthash[1]);

            ////////////////////////////////////SET STATUS
            String Status = Name_withouthash[Name_withouthash.length -1];
            if(Status.equalsIgnoreCase("3")) {
                holder.txtstatus.setText("PENDING");
                holder.txtstatus.setTextColor(Color.parseColor("#0000FF"));
                holder.txtstatus.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_sentiment_neutral,0,0);
            }
            else if(Status.equalsIgnoreCase("2")) {
                holder.txtstatus.setText("REJECTED");
                holder.txtstatus.setTextColor(Color.parseColor("#FF0000"));
                holder.txtstatus.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_sentiment_dissatisfied,0,0);
            }

            else if(Status.equalsIgnoreCase("1")) {
                holder.txtstatus.setText("ACCEPTED");
                holder.txtstatus.setTextColor(Color.parseColor("#000000"));
                holder.txtstatus.setTextColor(Color.parseColor("#00FF00"));
                holder.txtstatus.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_sentiment_satisfied,0,0);
            }
            else
                holder.txtstatus.setText("No Status");

            ///////////////////////////////////
            //Load Image of the user
            //Using Universal Image Loaded Load Image on Top Of Text1 : URL is URL_ForImage
            //////////////////Load image From Server//////////////////////////////////////////////////
            // creating connection detector class instance
            cd = new ConnectionDetector(context);
            isInternetPresent = cd.isConnectingToInternet();

            // check for Internet status
            if (isInternetPresent && !Name_withouthash[1].contains("More"))
            {

                String URL_ForImage ="http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/" + Name_withouthash[0]+".jpg";
                ImageLoader imageLoader = ImageLoader.getInstance();

                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .displayer(new RoundedBitmapDisplayer(1000))
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
                imageLoader.displayImage(URL_ForImage, holder.imageView, options);

            }
            else
            {
                holder.imageView.setImageResource(R.drawable.user_loading);
            }

            /////////////////////////////////////////////////////////////////////////////
        }else {
            holder.txtview.setText(horizontalList.get(position).txt);
        }



        holder.imageView.setOnClickListener(
                new View.OnClickListener() {
            @Override

            public void onClick(View v)
            {
                String list = horizontalList.get(position).txt.toString();


                if(list.equalsIgnoreCase("all"))
                {
                    Intent intent = new Intent(context, FriendsOnTapri.class);
                    context.startActivity(intent);
                }
                else if(list.equalsIgnoreCase("0#more"))
                {
                    Next_count = Next_count+10;
                    if (context instanceof FriendsOnTapri)
                    {
                        ((FriendsOnTapri) context).fetchFriendsOnTapri(Next_count,"",6);
                    }
                }
                else if(list.equalsIgnoreCase("1#No More"))
                {
                    holder.imageView.setImageResource(R.drawable.next);
                }
                else
                {
                    if (list.contains("#")) {
                        try {
                            String Id_Name[] = list.split("#");
                            //0 the part is ID & 1 is NAme
                            // 2 is College  3 Batch
                            //4 is Branch
                            Intent i = new Intent(context, ViewFriendsProfile.class);
                            i.putExtra("id", Id_Name[0]);
                            i.putExtra("name", Id_Name[1]);

                            i.putExtra("college", Id_Name[2]);
                            i.putExtra("batch", Id_Name[3]);
                            i.putExtra("department", Id_Name[4]);
                            i.putExtra("timesview", Id_Name[5]);

                            context.startActivity(i);
                        } catch (Exception e) {
                            Toast.makeText(context, "Not able to load profile,Please Retry!#1", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(context, "Not able to load profile,Please Retry!#2", Toast.LENGTH_SHORT).show();
                    }

                }

            }

        });



        holder.txtviewfriends.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v)
                    {
                        String list = horizontalList.get(position).txt.toString();
                        if(list.contains("#"))
                        {
                            try {
                                String Id_Name[] = list.split("#");
                                //0 the part is ID & 1 is NAme
                                // 2 is College  3 Batch
                                //4 is Branch
                                Intent i = new Intent(context, ViewFriendsProfile.class);
                                i.putExtra("id", Id_Name[0]);
                                i.putExtra("name", Id_Name[1]);

                                i.putExtra("college", Id_Name[2]);
                                i.putExtra("batch", Id_Name[3]);
                                i.putExtra("department", Id_Name[4]);
                                i.putExtra("timesview", Id_Name[5]);

                                context.startActivity(i);
                            }catch (Exception e)
                            {
                                Toast.makeText(context, "Not able to load profile,Please Retry!#1", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        {
                            Toast.makeText(context, "Not able to load profile,Please Retry!#2", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }



    @Override
    public int getItemCount()
    {
        return horizontalList.size();
    }
}