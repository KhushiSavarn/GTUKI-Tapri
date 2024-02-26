package com.ambaitsystem.tapri.activity;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ambaitsystem.vgecchat.R;

import java.util.ArrayList;


public class QuizCallAdapter extends RecyclerView.Adapter<QuizCallAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> Topics;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtviewname;
        public CardView card_view_topics;

        public ViewHolder(View view) {
            super(view);
            txtviewname = (TextView) view.findViewById(R.id.txtviewname);
            card_view_topics = (CardView)view.findViewById(R.id.card_view_topics);
        }

    }


    public QuizCallAdapter(Context mContext, ArrayList<String> TopicListing) {
        this.mContext = mContext;
        this.Topics = TopicListing;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_quiz_call_row, parent, false);
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
        final String StrTopics = Topics.get(position);
        final String TopicWithoutHash[] = StrTopics.split("#");
        holder.txtviewname.setText(TopicWithoutHash[1]);

        holder.card_view_topics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext.getApplicationContext(), QuizActivity.class);
                intent.putExtra("Subject_Of_Quiz",TopicWithoutHash[0]);
                Toast.makeText(mContext, ""+TopicWithoutHash[0], Toast.LENGTH_SHORT).show();
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return Topics.size();
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private QuizCallAdapter.ClickListener clickListener;

        public RecyclerTouchListener(final Context context, final RecyclerView recyclerView, final QuizCallAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {

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
