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
import com.ambaitsystem.vgecchat.R;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class adapter_request_news extends RecyclerView.Adapter<adapter_request_news.MyViewHolder> {


    List<Data> horizontalList = Collections.emptyList();
    Context context;
    public Integer Next_count = 0;
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    private ArrayList<Data> messageArrayList_rec;
    private ProgressDialog progress;


    public adapter_request_news(List<Data> horizontalList, Context context, ArrayList<Data> receive_news) {
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
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_request_news_row, parent, false);
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

            if (Name_withouthash[4].equalsIgnoreCase("null"))
                holder.txtTotalliked.setText(" 0");
            else
                holder.txtTotalliked.setText(" " + Name_withouthash[4]);

            if (Name_withouthash[5].equalsIgnoreCase("null"))
                holder.txtTotaldisliked.setText(" 0");
            else
                holder.txtTotaldisliked.setText(" " + Name_withouthash[5]);

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


        holder.txtTotalliked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name_withhas = horizontalList.get(position).txt;
                String[] Name_withouthash = Name_withhas.split("#");

                call_like_news(context, 1, Name_withouthash[3],holder);
            }
        });

        holder.txtTotaldisliked.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String Name_withhas = horizontalList.get(position).txt;
                String[] Name_withouthash = Name_withhas.split("#");

                int val = Integer.valueOf(holder.txtTotaldisliked.getText().toString().trim());
                if (val != 0) {
                    val = val - 1;
                    holder.txtTotaldisliked.setText(" " + val);
                }
            call_like_news(context, 0,Name_withouthash[3],holder);
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

    private void call_like_news(final Context context, final int liked_status, final String news_master_id, final MyViewHolder holder)
    {
        //Get Own ID
        final String MyId = MyApplication.getInstance().getPrefManager().getUser().getId().trim();
        Toast.makeText(context, "Wait..", Toast.LENGTH_SHORT).show();
        //Call Volley to set liked or disliked
        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.LIKE_ACTIVITY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response)
            {
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
                Toast.makeText(context, "Okay.", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No server connection", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("news_master_id", news_master_id);
                params.put("user_id", MyId);
                params.put("isliked", Integer.toString(liked_status));
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
    public int getItemCount() {
        return horizontalList.size();
    }
}