package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
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
import com.ambaitsystem.tapri.helper.DbBasic;
import com.ambaitsystem.tapri.helper.single_chat_message;
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

import java.util.List;

public class adapter_single_chat_message extends RecyclerView.Adapter<adapter_single_chat_message.ViewHolder> {
    private int itemLayout;
    private List<single_chat_message> items;
    Context context;
    private String SELF_MESSAGE = "5874";
    private String OTHER_MESSAGE = "4587";
    private ProgressDialog progress;

    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;

    public adapter_single_chat_message(List<single_chat_message> items, int itemLayout, Context context) {
        this.items = items;
        this.itemLayout = itemLayout;
        this.context = context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final single_chat_message item_pos = items.get(position);

        holder.Single_chat_user_name.setText(Html.fromHtml("<b><font color='#000000'> " + item_pos.GetSingle_chat_user_name() + "</font></b>"));
        holder.Single_chat_user_id.setText(item_pos.GetSingle_chat_user_id());

        String _Message = item_pos.GetSingle_chat_message().replace(SELF_MESSAGE, "You -");

        //Remove Unnecessory things from Message before show
        _Message = _Message.replace(OTHER_MESSAGE, "");

        _Message = _Message.replace(":", "");
        _Message = _Message.replace("$#", "");

        try {
            if (_Message.contains("TEA OFFER RECEIVED")) {
                holder.layout_accept_reject.setVisibility(View.VISIBLE);
            } else {
                holder.layout_accept_reject.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }

        holder.Single_chat_message.setText(_Message);
        holder.Single_chat_user_email.setText(item_pos.GetStrSingle_chat_user_email());

        holder.Single_chat_timestamp.setText(item_pos.GetStrSingle_chat_timestamp());

        holder.Single_chat_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SingleChatRoomActivity.class);
                i.putExtra("user_id", item_pos.GetSingle_chat_user_id());
                i.putExtra("name", item_pos.GetSingle_chat_user_name());
                i.putExtra("message", item_pos.GetSingle_chat_message());
                context.startActivity(i);
            }
        });
        holder.txtviewsendchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SingleChatRoomActivity.class);
                i.putExtra("user_id", item_pos.GetSingle_chat_user_id());
                i.putExtra("name", item_pos.GetSingle_chat_user_name());
                i.putExtra("message", item_pos.GetSingle_chat_message());
                context.startActivity(i);
            }
        });


        holder.txtviewsendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{item_pos.GetStrSingle_chat_user_email()});
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT, "Your Email here");
                context.startActivity(Intent.createChooser(intent, ""));
            }
        });

        if (item_pos.GetSingle_chat_user_id().equalsIgnoreCase("0")) {
            holder.txtviewsendchat.setVisibility(View.GONE);
            holder.txtviewsendemail.setVisibility(View.GONE);
            holder.txtviewdelete.setVisibility(View.GONE);
        }

        //IF Pending : 3 , Rejected : 2 ,Accepted : 1
        holder.txtAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Accept_Reject_Request(holder, 1, item_pos.GetSingle_chat_user_id(), item_pos.GetSingle_chat_user_name());
            }
        });
        holder.txtreject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Accept_Reject_Request(holder, 2, item_pos.GetSingle_chat_user_id(), item_pos.GetSingle_chat_user_name());
            }
        });


        holder.txtviewdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete this user ID from Single Chat
                Delete_Singlechat(item_pos.GetSingle_chat_user_id());
                ((List_Single_chat_Messages) context).displatch_dataitem_change();
            }
        });

        set_user_Image(holder,item_pos.GetSingle_chat_user_id());
    }

    public void set_user_Image(ViewHolder holder,String MyId) {
        //Load Image of the user
        //Using Universal Image Loaded Load Image on Top Of Text1 : URL is URL_ForImage
        //////////////////Load image From Server//////////////////////////////////////////////////
        // creating connection detector class instance
        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {

            String URL_ForImage = "http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/" + MyId + ".jpg";
            ImageLoader imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                    .displayer(new RoundedBitmapDisplayer(1000))
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
            imageLoader.displayImage(URL_ForImage, holder.imguser_singlechat, options);
        } else {
            Toast.makeText(context, "Check connection and retry.", Toast.LENGTH_SHORT).show();
        }
    }

    public void Accept_Reject_Request(final adapter_single_chat_message.ViewHolder holder, int status, final String userid, final String Name) {
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

                        holder.layout_accept_reject.setVisibility(View.GONE);
                        holder.layout_status.setVisibility(View.VISIBLE);
                        holder.txtstatus_accept_reject.setText("Response sent to " + Name);
                        Toast.makeText(context, "Response sent to " + Name, Toast.LENGTH_SHORT).show();
                        holder.txtreject.setVisibility(View.INVISIBLE);

                        //Delete this user ID from Single Chat
                        Delete_Singlechat(userid);


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

    private void Delete_Singlechat(String userid) {
        SQLiteDatabase db = null;

        db = (new DbBasic(context)).getWritableDatabase();
        try {

            String Insert_Query = "DELETE FROM single_users_messages WHERE user_id = " + userid;
            db.execSQL(Insert_Query);
            db.close();
        } catch (Exception e) {
            db.close();
            Toast.makeText(context, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Single_chat_user_name, Single_chat_user_id, Single_chat_message, Single_chat_user_email, Single_chat_timestamp;
        LinearLayout singlechat_linlayout;
        CardView card_view;
        public TextView txtviewsendchat, txtviewsendemail, txtviewdelete;
        LinearLayout layout_accept_reject, layout_status;
        TextView txtstatus_accept_reject, txtAccept, txtreject;
        ImageView imguser_singlechat;

        public ViewHolder(View itemView) {
            super(itemView);
            Single_chat_user_name = (TextView) itemView.findViewById(R.id.Single_chat_user_name);
            Single_chat_user_id = (TextView) itemView.findViewById(R.id.Single_chat_user_id);
            imguser_singlechat = (ImageView) itemView.findViewById(R.id.imguser_singlechat);

            Single_chat_message = (TextView) itemView.findViewById(R.id.Single_chat_message);
            Single_chat_user_email = (TextView) itemView.findViewById(R.id.Single_chat_user_email);


            Single_chat_timestamp = (TextView) itemView.findViewById(R.id.Single_chat_timestamp);

            txtviewsendchat = (TextView) itemView.findViewById(R.id.txtviewsendchat);
            txtviewsendemail = (TextView) itemView.findViewById(R.id.txtviewsendemail);


            singlechat_linlayout = (LinearLayout) itemView.findViewById(R.id.singlechat_linlayout);

            card_view = (CardView) itemView.findViewById(R.id.card_view);

            txtAccept = (TextView) itemView.findViewById(R.id.txtAccept);

            txtreject = (TextView) itemView.findViewById(R.id.txtreject);

            layout_accept_reject = (LinearLayout) itemView.findViewById(R.id.layout_accept_reject);
            layout_status = (LinearLayout) itemView.findViewById(R.id.layout_status);
            txtstatus_accept_reject = (TextView) itemView.findViewById(R.id.txtstatus_accept_reject);
            txtviewdelete = (TextView) itemView.findViewById(R.id.txtviewdelete);

        }
    }
}