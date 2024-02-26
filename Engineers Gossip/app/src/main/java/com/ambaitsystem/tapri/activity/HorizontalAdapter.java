package com.ambaitsystem.tapri.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;


public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {


    List<Data> horizontalList = Collections.emptyList();
    Context context;

    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    public Integer Next_count = 0;
    private ProgressDialog progress;


    public HorizontalAdapter(List<Data> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView txtview, txtoffertea;
        TextView txtview_likes, txtviewfriends;
        LinearLayout Layout_forBottomDetails, entrieslistcontent;

        public MyViewHolder(View view)
        {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
            txtview_likes = (TextView) view.findViewById(R.id.txtviewNoOfTimesView);
            txtviewfriends = (TextView) view.findViewById(R.id.txtviewfriends);
            txtoffertea = (TextView) view.findViewById(R.id.txtoffertea);
            Layout_forBottomDetails = (LinearLayout) view.findViewById(R.id.thumbnail);
            entrieslistcontent = (LinearLayout) view.findViewById(R.id.entrieslistcontent);
            txtview = (TextView) view.findViewById(R.id.txtview);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_friends_on_tapri_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //Data recieved in following format :
        //user_id"#"name#institute"#"batch"#"branch#likes#isinterested
        //holder.imageView.setImageResource(horizontalList.get(position).imageId);

        if (horizontalList.get(position).txt.contains("#"))
        {
            String Name_withhas = horizontalList.get(position).txt;
            String[] Name_withouthash = Name_withhas.split("#");
            holder.txtview.setText(Name_withouthash[1]);

            try {
                holder.txtview_likes.setText(Name_withouthash[5] + " Views");
            } catch (Exception e) {
            }

            if (context instanceof MainActivity) {
                Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
                int width = ((display.getWidth() * 30) / 100);
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
                holder.entrieslistcontent.setLayoutParams(parms);
            }
            if (context instanceof FriendsOnTapri) {
                holder.Layout_forBottomDetails.setVisibility(View.VISIBLE);
                holder.txtoffertea.setVisibility(View.VISIBLE);
                holder.txtviewfriends.setVisibility(View.VISIBLE);
            }
            if (context instanceof Request_Status) {
                holder.Layout_forBottomDetails.setVisibility(View.VISIBLE);

            }
            //Load Image of the user
            //Using Universal Image Loaded Load Image on Top Of Text1 : URL is URL_ForImage
            //////////////////Load image From Server//////////////////////////////////////////////////
            // creating connection detector class instance
            cd = new ConnectionDetector(context);
            isInternetPresent = cd.isConnectingToInternet();

            // check for Internet status
            if (isInternetPresent && !Name_withouthash[1].contains("More"))
            {

                String URL_ForImage = "http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/" + Name_withouthash[0] + ".jpg";
                ImageLoader imageLoader = ImageLoader.getInstance();

                String MyId = MyApplication.getInstance().getPrefManager().getUser().getId();
                DisplayImageOptions options;
                if(Name_withouthash[0].equalsIgnoreCase(MyId)) {
                   options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                           .displayer(new RoundedBitmapDisplayer(1000))
                            .showImageForEmptyUri(R.drawable.user_top)
                            .showImageOnFail(R.drawable.user_top)
                            .showImageOnLoading(R.drawable.user_loading).build();
                }
                else
                {
                    options = new DisplayImageOptions.Builder().cacheInMemory(true)
                            .displayer(new RoundedBitmapDisplayer(1000))
                            .cacheOnDisc(true).resetViewBeforeLoading(true)
                            .showImageForEmptyUri(R.drawable.user_top)
                            .showImageOnFail(R.drawable.user_top)
                            .showImageOnLoading(R.drawable.user_loading).build();
                }
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                        context)
                        .defaultDisplayImageOptions(options)
                        .memoryCache(new WeakMemoryCache())
                        .discCacheSize(100 * 1024 * 1024).build();

                ImageLoader.getInstance().init(config);
                //download and display image from url
                imageLoader.displayImage(URL_ForImage, holder.imageView, options);

            }
            else
            {
                holder.txtview_likes.setText(" ");
                holder.Layout_forBottomDetails.setVisibility(View.VISIBLE);
                holder.txtoffertea.setVisibility(View.GONE);
                holder.txtviewfriends.setVisibility(View.GONE);
                holder.imageView.setImageResource(R.drawable.user_top);
            }

            /////////////////////////////////////////////////////////////////////////////
        } else {
            holder.txtview.setText(horizontalList.get(position).txt);
        }


        holder.imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {

                        try {
                            String list = horizontalList.get(position).txt.toString();
                            if (list.equalsIgnoreCase("all"))
                            {
                                Intent intent = new Intent(context, FriendsOnTapri.class);
                                context.startActivity(intent);
                            }
                          else if (list.equalsIgnoreCase("0#more"))
                            {
                                Next_count = Next_count + 10;
                                if (context instanceof FriendsOnTapri) {
                                    ((FriendsOnTapri) context).fetchFriendsOnTapri(Next_count,"",2);
                                }
                            }

                            else if (list.equalsIgnoreCase("1#No More"))
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
                                        Toast.makeText(context, "No Profile available.#1", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(context, "No Profile available.#2", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            //`Toast.makeText(context, "Something went wrong,Please Retry!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

        holder.txtoffertea.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String list = horizontalList.get(position).txt.toString();

                        if (list.contains("#")) {
                            String Id_Name[] = list.split("#");
                            //0 the part is ID & 1 is NAme
                            Offer_Tea(context, Id_Name[0], Id_Name[1]);

                        } else {
                            Toast.makeText(context, "Not able to offer tea now,Please Retry!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

        holder.txtviewfriends.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String list = horizontalList.get(position).txt.toString();
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

                });

    }

    private void Offer_Tea(final Context context, String ToId, final String Name) {
        //Call Asynch task to offer a Tea
        progress = new ProgressDialog(context);
        progress.setMessage("Offering tea to " + Name);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();

        //Get FromId
        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        String FromId = Iam.getId();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.OFFER_TEA + "/" + FromId + "/" + ToId, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    progress.dismiss();
                } catch (Exception e) {
                }

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getString("status_Of_Offer").equalsIgnoreCase("1")) {
                        Dialog_Information(context,"Tea Offer", "Tea Offered to " + Name);
                        /////////////////////////////////////////////////////////
                    } else if (obj.getString("status_Of_Offer").equalsIgnoreCase("0")) {
                        Toast.makeText(context, "Not able to offer tea now,Please Retry.#1", Toast.LENGTH_LONG).show();
                        try {
                            progress.dismiss();
                        } catch (Exception e) {
                        }
                    } else if (obj.getString("status_Of_Offer").equalsIgnoreCase("2")) {
                        Dialog_Information(context,"Already offered a Tea", "You may be the friend of this person or request is pending / rejected.");
                        } else {
                        Toast.makeText(context, "Not able to offer tea now,Please Retry.#2", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Problem In Offering tea,Please Retry." + e.toString(), Toast.LENGTH_LONG).show();
                    try {
                        progress.dismiss();
                    } catch (Exception e1) {
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e("FriendsOnTapri", "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(context, "Problem In Offering tea,Please Retry.", Toast.LENGTH_LONG).show();
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }



    public void Dialog_Information(Context context,String Title,String Message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Title);
        builder.setMessage(Message);
        builder.setCancelable(false);
        builder.setNegativeButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}