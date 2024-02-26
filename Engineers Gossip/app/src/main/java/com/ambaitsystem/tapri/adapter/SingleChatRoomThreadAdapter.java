package com.ambaitsystem.tapri.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.model.Message;
import com.ambaitsystem.vgecchat.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SingleChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = SingleChatRoomThreadAdapter.class.getSimpleName();

    private String userId;
    private int SELF = 100;


    private String SELF_MESSAGE = "5874";
    private String OTHER_MESSAGE = "4587";
    private static String today;

    private Context mContext;
    private ArrayList<Message> messageArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp, advertisement_title, advertisement_description, advertisement_grab;
        LinearLayout linlayout_advertisement;

        public ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            linlayout_advertisement = (LinearLayout) itemView.findViewById(R.id.linlayout_advertisement);

            //For Advertisement
            advertisement_title = (TextView) itemView.findViewById(R.id.advertisement_title);
            advertisement_description = (TextView) itemView.findViewById(R.id.advertisement_description);
            advertisement_grab = (TextView) itemView.findViewById(R.id.advertisement_grab);
        }
    }


    public SingleChatRoomThreadAdapter(Context mContext, ArrayList<Message> messageArrayList, String userId) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.userId = userId;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
        }


        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);

        if (message.getMessage().contains(SELF_MESSAGE)) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)

    {
        Message message = messageArrayList.get(position);

        //Code 14224 is for advertisement
        if (message.getMessage().contains("14224")) {
            //For Advertisement
            //Hide timestamp and message
            //Show linlayout_advertisement & Fill Data in it : Code 14224 is for advertisement
            ((ViewHolder) holder).message.setVisibility(View.GONE);
            ((ViewHolder) holder).timestamp.setVisibility(View.GONE);
            ((ViewHolder) holder).linlayout_advertisement.setVisibility(View.VISIBLE);

            //Advertisement Format : 14224#Title#Content#URL
            if (message.getMessage().contains("#")) {
                String Adv_details[] = message.getMessage().split("#");
                String Title = Adv_details[1];
                String Content = Adv_details[2];
                String URL = Adv_details[3];
                ((ViewHolder) holder).advertisement_title.setText(Title);
                ((ViewHolder) holder).advertisement_description.setText(Content);
                ((ViewHolder) holder).advertisement_grab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Uri uri = Uri.parse("http://www.google.com");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(mContext, "URL is not valid.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //Set Details in Associated Controls
            }

        } else {
            //Other than Advertisement
            //Show timestamp and message
            //Hide linlayout_advertisement

            ((ViewHolder) holder).message.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).timestamp.setVisibility(View.VISIBLE);

            //Remove Unnecessory things from Message before show
           String _Message = message.getMessage().replace(SELF_MESSAGE, "");
            _Message = _Message.replace(OTHER_MESSAGE, "");
            _Message = _Message.replace(":", "");
            _Message = _Message.replace("$#", "");

            ((ViewHolder) holder).message.setText( _Message);
            String timestamp = getTimeStamp(message.getCreatedAt());
           // if (message.getUser().getName() != null)
                //timestamp = message.getUser().getName() + ", " + timestamp;
            ((ViewHolder) holder).timestamp.setText(timestamp);
        }

    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            String NewDate = "";
            //From MySql Time shows pm in place of am vice-versa
            if (date1.contains("am")) {
                NewDate = date1.replace("am", "pm");
            } else {
                NewDate = date1.replace("pm", "am");
            }

            timestamp = NewDate.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
}

