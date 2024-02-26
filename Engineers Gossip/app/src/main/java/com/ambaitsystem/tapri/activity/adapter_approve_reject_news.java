package com.ambaitsystem.tapri.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.tapri.model.User;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class adapter_approve_reject_news extends RecyclerView.Adapter<adapter_approve_reject_news.MyViewHolder> {


    List<Data> horizontalList = Collections.emptyList();
    Context context;
    public Integer Next_count = 0;
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    private ArrayList<Data> messageArrayList_rec;
    private ProgressDialog progress;


    public adapter_approve_reject_news(List<Data> horizontalList, Context context, ArrayList<Data> receive_news) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.messageArrayList_rec = receive_news;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txtcreatedby, txtdetails, txtReadMore, txtshare, txtcomments_news, txtTotalliked, txtTotaldisliked;
        CardView card_view;
        LinearLayout LinLayout_like_dislike;
        public MyViewHolder(View view) {
            super(view);
            txtdetails = (TextView) view.findViewById(R.id.txtdetails);
            txtcreatedby = (TextView) view.findViewById(R.id.txtcreatedby);
            txtReadMore = (TextView) view.findViewById(R.id.txtReadMore);
            txtshare = (TextView) view.findViewById(R.id.txtshare);
            txtcomments_news = (TextView) view.findViewById(R.id.txtcomments_news);
            txtTotalliked = (TextView) view.findViewById(R.id.txtTotalliked);
            txtTotaldisliked = (TextView) view.findViewById(R.id.txtTotaldisliked);

            card_view = (CardView) view.findViewById(R.id.card_view);
            LinLayout_like_dislike = (LinearLayout)view.findViewById(R.id.LinLayout_like_dislike);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 1)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_request_news_row_header, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_approve_reject_news_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Data message = messageArrayList_rec.get(position);
        String type = message.txt;
        Log.v("Message", "$#" + type);
        if (type.contains("0#COLLEGE#0"))
            return 1;
        else if (type.contains("0#DEPARTMENT#0"))
            return 1;
        else if (type.contains("0#HOSTEL#0"))
            return 1;
        else if (type.contains("0#CAREER#0"))
            return 1;
        else if (type.contains("0#EVENTS#0"))
            return 1;
        else if (type.contains("0#OTHER#0"))
            return 1;
        else if (type.contains("252532532145282145"))
            return 1;
        else
            return 0;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

            /*
            randomuserObj.getString("news_cat_id")
            + "#" + randomuserObj.getString("details")
            + "#" + randomuserObj.getString("name")
            + "#" + randomuserObj.getString("news_id")
            + "#" + randomuserObj.getString("liked")
            + "#" + randomuserObj.getString("dislike")
             */
        //if 3

        if (horizontalList.get(position).txt.contains("#")) {
            String Name_withhas = horizontalList.get(position).txt;
            String[] Name_withouthash = Name_withhas.split("#");

            holder.txtdetails.setText(Name_withouthash[1].replace("252532532145282145", "contributed"));
            holder.txtcreatedby.setText("By " + Name_withouthash[2]);

        } else {
            holder.txtdetails.setText(horizontalList.get(position).txt);
        }

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name_withhas = horizontalList.get(position).txt;
                String[] Name_withouthash = Name_withhas.split("#");
                Intent i = new Intent(context, activity_news_sub_list.class);
                i.putExtra("newsID", Name_withouthash[3]);
                i.putExtra("news_details", Name_withouthash[1].replace("252532532145282145", "contributed"));
                context.startActivity(i);
            }
        });
        holder.txtdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name_withhas = horizontalList.get(position).txt;
                String[] Name_withouthash = Name_withhas.split("#");
                Intent i = new Intent(context, activity_news_sub_list.class);
                i.putExtra("newsID", Name_withouthash[3]);
                i.putExtra("news_details", Name_withouthash[1].replace("252532532145282145", "contributed"));
                context.startActivity(i);
            }
        });
        holder.txtReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name_withhas = horizontalList.get(position).txt;
                String[] Name_withouthash = Name_withhas.split("#");

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(Name_withouthash[2] + " Reported :");
                builder.setMessage(Name_withouthash[1])
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

//call_approve_reject_news(context,liked_status, news_master_id,holder)
        holder.txtTotalliked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name_withhas = horizontalList.get(position).txt;
                String[] Name_withouthash = Name_withhas.split("#");
                call_approve_reject_news(context, 1, Name_withouthash[3],holder, Name_withouthash[1].replace("252532532145282145", "contributed"));
            }
        });

        holder.txtTotaldisliked.setOnClickListener(new View.OnClickListener()
                                                   {
                                                       @Override
                                                       public void onClick(View v) {
                                                           String Name_withhas = horizontalList.get(position).txt;
                                                           String[] Name_withouthash = Name_withhas.split("#");
                                                           call_approve_reject_news(context, 0,Name_withouthash[3],holder, Name_withouthash[1].replace("252532532145282145", "contributed"));
                                                       }
                                                   }

        );
        holder.txtshare.setOnClickListener(new View.OnClickListener()

                                           {
                                               @Override
                                               public void onClick (View v){
                                                   String Name_withhas = horizontalList.get(position).txt;
                                                   String[] Name_withouthash = Name_withhas.split("#");
                                                   Intent intent = new Intent(Intent.ACTION_SEND);
                                                   intent.setType("text/plain");
                                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

// Add data to the intent, the receiving app will decide what to do with it.
                                                   intent.putExtra(Intent.EXTRA_SUBJECT, "GTU Ki Tapri");
                                                   intent.putExtra(Intent.EXTRA_TEXT, "Updates of our college: \n" + Name_withouthash[1] + "\n\nLets gather On GTU Ki Tapri : https://play.google.com/store/apps/details?id=com.ambaitsystem.vgecchat  Exclusive social app for GTU Students.");
                                                   context.startActivity(Intent.createChooser(intent, "How do you want to share?"));

                                               }
                                           }

        );

        holder.txtcomments_news.setOnClickListener(new View.OnClickListener()

                                                   {
                                                       @Override
                                                       public void onClick (View v){
                                                           Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                                                       }
                                                   }

        );

    }

    private void call_approve_reject_news(final Context context, final int liked_status, final String news_master_id, final MyViewHolder holder,final String news_details)
    {
        //Get Own ID
        final String MyId = MyApplication.getInstance().getPrefManager().getUser().getId().trim();
        //Call Asynch task to offer a Tea
        progress = new ProgressDialog(context);
        progress.setMessage("Approving..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();
        String Details = news_details.replace(" ","%20");

        //Get FromId
        User Iam = MyApplication.getInstance().getPrefManager().getUser();
        String FromId = Iam.getId();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                EndPoints.APPROVE_REJECT_NEWS +"/"+ MyApplication.getInstance().getPrefManager().getUser().getEmail().toString().trim() +"/" + Details + "/"+liked_status + "/"+news_master_id, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    progress.dismiss();
                } catch (Exception e) {
                }

                try {

                    holder.LinLayout_like_dislike.animate()
                            .translationY(0)
                            .alpha(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    holder.LinLayout_like_dislike.setVisibility(View.GONE);
                                }
                            });
                } catch (Exception e)
                {
                    Toast.makeText(context, "Problem In approval,Please Retry."+e.toString(), Toast.LENGTH_LONG).show();
                    try {
                        progress.dismiss();
                    } catch (Exception e1) {
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                error.printStackTrace();
                Toast.makeText(context, "Problem In approval,Please Retry."+error.toString(), Toast.LENGTH_LONG).show();
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