package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
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
import com.ambaitsystem.tapri.model.Message;
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


public class adapter_request_friends extends RecyclerView.Adapter<adapter_request_friends.MyViewHolder> {


    List<Data> horizontalList = Collections.emptyList();
    Context context;
    public Integer Next_count = 0;
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;

    private ProgressDialog progress;


    public adapter_request_friends(List<Data> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView txtview, txtview_remove_friend, txtview_open_singleChat;
        TextView txtAccept, txtviewfriends;
        LinearLayout request_received_1;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
            txtviewfriends = (TextView) view.findViewById(R.id.txtviewfriends);
            txtAccept = (TextView) view.findViewById(R.id.txtAccept);
            request_received_1 = (LinearLayout) view.findViewById(R.id.request_recieved_bottom);
            txtview = (TextView) view.findViewById(R.id.txtview);
            txtview_remove_friend = (TextView) view.findViewById(R.id.txtview_remove_friend);
            txtview_open_singleChat = (TextView) view.findViewById(R.id.txtview_open_singleChat);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_request_friends_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //Data recieved in following format :
        //user_id"#"name#institute"#"batch"#"branch#likes#isinterested
        //holder.imageView.setImageResource(horizontalList.get(position).imageId);


        //In this case offer a tea should be status
        //if 3

        if (horizontalList.get(position).txt.contains("#")) {
            String Name_withhas = horizontalList.get(position).txt;
            String[] Name_withouthash = Name_withhas.split("#");
            holder.txtview.setText(Name_withouthash[1]);

            if(horizontalList.get(position).txt.contains("SEE ALL"))
            {
                holder.txtview_remove_friend.setVisibility(View.GONE);
                holder.txtviewfriends.setVisibility(View.GONE);
                holder.txtview_open_singleChat.setVisibility(View.GONE);
            }
           else if(horizontalList.get(position).txt.contains("MORE"))
            {
                holder.txtview_remove_friend.setVisibility(View.GONE);
                holder.txtviewfriends.setVisibility(View.GONE);
                holder.txtview_open_singleChat.setVisibility(View.GONE);
            }

            else if(horizontalList.get(position).txt.contains("NO NEW FRIENDS"))
            {
                holder.txtview_remove_friend.setVisibility(View.GONE);
                holder.txtviewfriends.setVisibility(View.GONE);
                holder.txtview_open_singleChat.setVisibility(View.GONE);
            }
            else
            {
                holder.txtview_remove_friend.setVisibility(View.VISIBLE);
                holder.txtviewfriends.setVisibility(View.GONE);
                holder.txtview_open_singleChat.setVisibility(View.VISIBLE);
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
                        .discCacheSize(100 * 1024 * 1024).build();

                ImageLoader.getInstance().init(config);
                //download and display image from url
                imageLoader.displayImage(URL_ForImage, holder.imageView, options);

            } else {
                holder.imageView.setImageResource(R.drawable.user_loading);
            }

            /////////////////////////////////////////////////////////////////////////////
            if (!Name_withouthash[Name_withouthash.length - 1].contains("3")) {
                if (Name_withouthash[Name_withouthash.length - 1].contains("1"))
                    holder.txtAccept.setText("FRIENDS");
                else
                    holder.txtAccept.setText("REJECTED");
            } else {
                holder.txtAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (horizontalList.get(position).txt.toString().contains("#")) {
                            String list[] = horizontalList.get(position).txt.toString().split("#");
                            //Accept_Reject_Request(int status,int userid,int grid_index)
                            Accept_Reject_Request(holder, 1, list[0], position, list[1]);
                        }
                    }
                });
            }

            //Remove Friends
            holder.txtview_remove_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    String list = horizontalList.get(position).txt.toString();
                    if (list.contains("#")) {
                        try {
                            String Id_Name[] = list.split("#");
                            //0 the part is ID & 1 is NAme
                            // 2 is College  3 Batch
                            //4 is Branch
                            // Remove Id_Name[0] & My ID combination from from_id and To_id
                            //Open Dialog for yes and no for remove
                            Yes_no_Dialog_for_Remove(context,holder, Id_Name[0], Id_Name[1]);

                        } catch (Exception e) {
                            Toast.makeText(context, "Not able to Remove friend,Please Retry!#1", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Not able to Remove friend,Please Retry!#2", Toast.LENGTH_SHORT).show();
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
        } else {
            holder.txtview.setText(horizontalList.get(position).txt);
        }

        holder.imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String list = horizontalList.get(position).txt.toString();

                        if (list.equalsIgnoreCase("all")) {
                            Intent intent = new Intent(context, FriendsOnTapri.class);
                            context.startActivity(intent);
                        }

                       else if (list.contains("SEE ALL"))
                        {
                            Intent i = new Intent(context, Request_Friends_see_all.class);
                            context.startActivity(i);
                        }
                        else if(list.contains("0#MORE"))
                        {
                            Next_count = Next_count + 10;

                            if (context instanceof Request_Friends_see_all) {
                                ((Request_Friends_see_all) context).Fetch_Friends_Request_Received(Next_count);
                            }
                        }
                        else {
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

        holder.txtview_open_singleChat.setOnClickListener(
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
                                Intent i = new Intent(context, SingleChatRoomActivity.class);
                                i.putExtra("user_id", Id_Name[0]);
                                i.putExtra("name", Id_Name[1]);
                                i.putExtra("message", new Message("0", "0", "-", null));
                                context.startActivity(i);
                            } catch (Exception e) {
                                Toast.makeText(context, "Not able to load chat window ,Please Retry!#1", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, "Not able to load chat window,Please Retry!#2", Toast.LENGTH_SHORT).show();
                        }


                    }

                });


    }

    private void Yes_no_Dialog_for_Remove(Context context, final MyViewHolder holder,final String s,final String s1)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Remove Friend");
        builder.setMessage("Are you sure to remove " + s1 + " from friend list? \n\nOnce you Remove,You will not be allowed to offer a tea in future.");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                Remove_from_friend_list(holder, s, s1);
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


    public void Accept_Reject_Request(final MyViewHolder holder, int status, String userid, int grid_index, final String Name) {
        //IF Pending : 3 , Rejected : 2 ,Accepted : 1

        progress = new ProgressDialog(context);
        progress.setMessage("Responding to " + Name);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();

        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        String FromId = Iam.getId();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.RESPOND_STATUS + "/" + FromId + "/" + userid + "/" + status, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    try {
                        progress.dismiss();
                    } catch (Exception e) {
                    }

                    // check for error flag
                    if (obj.getString("status_Of_Offer").equalsIgnoreCase("0")) {
                        // error in fetching chat rooms
                        Toast.makeText(context, "Check Connection and Retry! [#1]", Toast.LENGTH_SHORT).show();
                        try {
                            progress.dismiss();
                        } catch (Exception e) {
                        }

                        /////////////////////////////////////////////////////////
                    } else {

                        if (obj.getString("status_Of_Offer").equalsIgnoreCase("1"))
                            holder.txtAccept.setText("FRIENDS");
                        else
                            holder.txtAccept.setText("REJECTED");

                        Toast.makeText(context, "Response sent to " + Name, Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    Toast.makeText(context, "Check Connection and Retry! [#2]", Toast.LENGTH_SHORT).show();
                    try {
                        progress.dismiss();
                    } catch (Exception x) {
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(context, "Check Connection and Retry! [#3]", Toast.LENGTH_SHORT).show();
                try {
                    progress.dismiss();
                } catch (Exception e) {
                }
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    public void Remove_from_friend_list(final MyViewHolder holder, String friend_userid, final String friend_name) {
        //IF Pending : 3 , Rejected : 2 ,Accepted : 1

        progress = new ProgressDialog(context);
        progress.setMessage("Removing  " + friend_name);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();

        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        String FromId = Iam.getId();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.REMOVE_FRIEND + "/" + FromId + "/" + friend_userid, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    try {
                        progress.dismiss();
                    } catch (Exception e) {
                    }

                    // check for error flag
                    if (obj.getString("status_remove_friends").equalsIgnoreCase("0")) {
                        // error in fetching chat rooms
                        Toast.makeText(context, "Check Connection and Retry! [#1]", Toast.LENGTH_SHORT).show();
                        try {
                            progress.dismiss();
                        } catch (Exception e) {
                        }

                        /////////////////////////////////////////////////////////
                    } else {
                        Toast.makeText(context, "Removed " + friend_name + "From list,List will updated on next refresh.", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    Toast.makeText(context, "Check Connection and Retry! [#2]", Toast.LENGTH_SHORT).show();
                    try {
                        progress.dismiss();
                    } catch (Exception x) {
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(context, "Check Connection and Retry! [#3]", Toast.LENGTH_SHORT).show();
                try {
                    progress.dismiss();
                } catch (Exception e) {
                }
            }
        });

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}