package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.vgecchat.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class adapter_news_sub extends RecyclerView.Adapter<adapter_news_sub.MyViewHolder> {


    List<Data> horizontalList = Collections.emptyList();
    Context context;
    public Integer Next_count = 0;
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    private ArrayList<Data> messageArrayList_rec;
    private ProgressDialog progress;


    public adapter_news_sub(List<Data> horizontalList, Context context, ArrayList<Data> receive_news) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.messageArrayList_rec = receive_news;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txtcreatedby, txtdetails, txtReadMore, txtshare, txtcomments_news;

        public MyViewHolder(View view) {
            super(view);
            txtdetails = (TextView) view.findViewById(R.id.txtdetails);
            txtcreatedby = (TextView) view.findViewById(R.id.txtcreatedby);
            txtReadMore = (TextView) view.findViewById(R.id.txtReadMore);
            txtshare = (TextView) view.findViewById(R.id.txtshare);
            txtcomments_news = (TextView) view.findViewById(R.id.txtcomments_news);

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


        //if 3

        if (horizontalList.get(position).txt.contains("#")) {
            String Name_withhas = horizontalList.get(position).txt;
            String[] Name_withouthash = Name_withhas.split("#");

            holder.txtdetails.setText(Name_withouthash[1].replace("252532532145282145","contributed"));
            holder.txtcreatedby.setText("By " + Name_withouthash[2]);

        } else {
            holder.txtdetails.setText(horizontalList.get(position).txt);
        }


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

        holder.txtshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        holder.txtcomments_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}