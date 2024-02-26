package com.ambaitsystem.tapri.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
import com.ambaitsystem.tapri.helper.DbBasic;
import com.ambaitsystem.vgecchat.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.Collections;
import java.util.List;


public class adapter_my_cart extends RecyclerView.Adapter<adapter_my_cart.MyViewHolder> {


        List<Data> horizontalList = Collections.emptyList();
        Context context;

        Boolean isInternetPresent = false;
        // Connection detector class
        ConnectionDetector cd;
public Integer Next_count = 0;
private ProgressDialog progress;


public adapter_my_cart(List<Data> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
        }


public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView txtview, txtcall;
    TextView txtview_likes, txtdeletefromcart;
    LinearLayout Layout_forBottomDetails, entrieslistcontent;

    public MyViewHolder(View view) {
        super(view);
        imageView = (ImageView) view.findViewById(R.id.imageview);
        txtview_likes = (TextView) view.findViewById(R.id.txtviewNoOfTimesView);
        txtdeletefromcart = (TextView) view.findViewById(R.id.txtdeletefromcart);
        txtcall = (TextView) view.findViewById(R.id.txtcall);
        Layout_forBottomDetails = (LinearLayout) view.findViewById(R.id.thumbnail);
        entrieslistcontent = (LinearLayout) view.findViewById(R.id.entrieslistcontent);
        txtview = (TextView) view.findViewById(R.id.txtview);
    }
}


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_listing_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //Data recieved in following format :
        //user_id"#"name#institute"#"batch"#"branch#likes#isinterested
        //holder.imageView.setImageResource(horizontalList.get(position).imageId);

        if (horizontalList.get(position).txt.contains("#"))
        {
        // product_id,product_name,creator_name,cell
              String Name_withhas = horizontalList.get(position).txt;
              String[] Name_withouthash = Name_withhas.split("#");

            if(Name_withouthash.length >=7) {
                holder.txtview.setText(Name_withouthash[1]);
                try {
                    holder.txtview_likes.setText(Name_withouthash[2]);
                } catch (Exception e) {
                }
            }
            else
                holder.txtview.setText(Name_withouthash[1]);


            if (context instanceof My_Cart) {
                holder.Layout_forBottomDetails.setVisibility(View.VISIBLE);
                holder.txtcall.setVisibility(View.VISIBLE);
                holder.txtdeletefromcart.setVisibility(View.VISIBLE);
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

                String URL_ForImage = "http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/product/" + Name_withouthash[0] + ".jpg";
                ImageLoader imageLoader = ImageLoader.getInstance();

                String MyId = MyApplication.getInstance().getPrefManager().getUser().getId();
                DisplayImageOptions options;
                if(Name_withouthash[0].equalsIgnoreCase(MyId)) {
                    options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                            .displayer(new RoundedBitmapDisplayer(1000))
                            .showImageForEmptyUri(R.drawable.product)
                            .showImageOnFail(R.drawable.product)
                            .showImageOnLoading(R.drawable.product_loading).build();
                }
                else
                {
                    options = new DisplayImageOptions.Builder().cacheInMemory(true)
                            .displayer(new RoundedBitmapDisplayer(1000))
                            .cacheOnDisc(true).resetViewBeforeLoading(true)
                            .showImageForEmptyUri(R.drawable.product)
                            .showImageOnFail(R.drawable.product)
                            .showImageOnLoading(R.drawable.product_loading).build();
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
                holder.Layout_forBottomDetails.setVisibility(View.GONE);
                holder.txtcall.setVisibility(View.GONE);
                holder.txtdeletefromcart.setVisibility(View.GONE);
                holder.imageView.setImageResource(R.drawable.product);
            }

            /////////////////////////////////////////////////////////////////////////////
        } else {
            holder.txtview.setText(horizontalList.get(position).txt);
        }


        holder.txtcall.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String list = horizontalList.get(position).txt.toString();
                        // product_id,product_name,creator_name,cell

                        if (list.contains("#"))
                        {
                            String Id_Name_cell[] = list.split("#");
                            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);

                            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(
                                        (Activity) context,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        123);
                            } else {
                                context.startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+Id_Name_cell[Id_Name_cell.length - 1])));
                            }

                        } else {
                            Toast.makeText(context, "Call can not be placed,please retry.", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

        holder.txtdeletefromcart.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String list = horizontalList.get(position).txt.toString();
                        if (list.contains("#")) {
                            String ProductId[] = list.split("#");
                            Delete_From_DB(context,ProductId[0]);
                            ((My_Cart) context).fetchProductFromCart(0,3);
                        }
                        else
                        {
                            Toast.makeText(context, "Retry.", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    private void Delete_From_DB(Context context, String ProductId)
    {
        SQLiteDatabase db = null;

        db = (new DbBasic(context)).getWritableDatabase();
        try {
            db.execSQL("DELETE FROM cart WHERE product_id =" + ProductId);
            db.close();
            Toast.makeText(context, "Deleted From Cart.", Toast.LENGTH_SHORT).show();
        } catch (Exception e)
        {
            Toast.makeText(context, ""+e.toString(), Toast.LENGTH_SHORT).show();
            db.close();
        }
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
