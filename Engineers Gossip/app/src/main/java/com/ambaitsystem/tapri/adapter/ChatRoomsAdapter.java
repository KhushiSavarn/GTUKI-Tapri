package com.ambaitsystem.tapri.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambaitsystem.tapri.activity.MainActivity;
import com.ambaitsystem.tapri.activity.activity_main_screen;
import com.ambaitsystem.tapri.model.ChatRoom;
import com.ambaitsystem.vgecchat.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ChatRoom> chatRoomArrayList;
    private static String today;
    int[] gridViewImageId = {
            R.drawable.i_aero, R.drawable.i_auto, R.drawable.i_biomedical, R.drawable.i_bio, R.drawable.i_chemical, R.drawable.i_civil,
            R.drawable.i_comp, R.drawable.i_electr, R.drawable.i_electro, R.drawable.i_electro, R.drawable.i_electro, R.drawable.i_environ, R.drawable.i_food,
            R.drawable.industrial, R.drawable.i_it, R.drawable.i_instru, R.drawable.i_mech, R.drawable.i_mech, R.drawable.i_maatta,
            R.drawable.i_min,R.drawable.i_plastic,R.drawable.i_power,R.drawable.production,R.drawable.i_rubber,R.drawable.i_testile,R.drawable.i_testile,R.drawable.i_ict,R.drawable.i_manu,
            R.drawable.i_environ,R.drawable.i_chemical,R.drawable.i_chemical,R.drawable.i_civil,R.drawable.i_civil,R.drawable.i_civil
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener
            {
        public TextView name, message, timestamp, count,followstatus;
                public ImageView imgview_letter,imgroomicon;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            count = (TextView) view.findViewById(R.id.count);
            followstatus = (TextView) view.findViewById(R.id.followstatus);
            imgroomicon = (ImageView) view.findViewById(R.id.imgroomicon);
            //Context Menu : Onclick Listener :Step 4
            view.setOnCreateContextMenuListener(this);


        }

        //Context Menu : Create Menu :Step 5
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle("Select The Action");
            menu.add(0, v.getId(), 0, "Unmute");//groupId, itemId, order, title
            menu.add(1, v.getId(), 0, "Mute");
            menu.add(2, v.getId(), 0, "Exit");

        }



    }




    public ChatRoomsAdapter(Context mContext, ArrayList<ChatRoom> chatRoomArrayList) {
        this.mContext = mContext;
        this.chatRoomArrayList = chatRoomArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView;
        if(mContext instanceof MainActivity)
            itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_rooms_list_row, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_rooms_status_row, parent, false);
        return new ViewHolder(itemView);
    }

    //Context Menu : Get & Set Position :Step 6
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        ChatRoom chatRoom = chatRoomArrayList.get(position);

        if(mContext instanceof activity_main_screen)
        {
            holder.count.setVisibility(View.GONE);
            holder.message.setVisibility(View.GONE);
        }
        if(chatRoom.getsubscribeStatus() == true) {
            holder.name.setText(chatRoom.getName());
            holder.imgroomicon.setImageResource(gridViewImageId[position]);
            holder.followstatus.setText("Following");
        }
        else {

            holder.name.setText(chatRoom.getName());
            holder.imgroomicon.setImageResource(gridViewImageId[position]);
            holder.followstatus.setText("Not Interested");
        }
        holder.message.setText(chatRoom.getLastMessage());
        if (chatRoom.getUnreadCount() > 0) {
            holder.count.setText(String.valueOf(chatRoom.getUnreadCount()));
            holder.count.setVisibility(View.VISIBLE);
        } else {
            holder.count.setVisibility(View.GONE);
        }


        ////End Set Image Letter

        holder.timestamp.setText(getTimeStamp(chatRoom.getTimestamp()));

        //Context Menu : OnBinding SetLong Click Listner :Step 7
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });


        // call Animation function
        setAnimation(holder.itemView, position);
    }
    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(1000));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }
    //Context Menu : Register :Step 8
    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
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
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ChatRoomsAdapter.ClickListener clickListener;

        public RecyclerTouchListener(final Context context, final RecyclerView recyclerView, final ChatRoomsAdapter.ClickListener clickListener)
        {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e)
                {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null)
                    {

                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));

                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }
}
