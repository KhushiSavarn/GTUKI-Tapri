package com.ambaitsystem.tapri.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.helper.Data;
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


public class adapter_my_product_listing extends RecyclerView.Adapter<adapter_my_product_listing.MyViewHolder> {


    List<Data> horizontalList = Collections.emptyList();
    Context context;

    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    private Integer Next_count = 0;
    private ProgressDialog progress;


    public adapter_my_product_listing(List<Data> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView,Premium_dimond;
        TextView txtview, txtdelete;
        TextView txtview_likes, txtproducts_markpremium,txtview12;
        LinearLayout Layout_forBottomDetails, entrieslistcontent;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
            Premium_dimond= (ImageView) view.findViewById(R.id.Premium_dimond);
            txtview_likes = (TextView) view.findViewById(R.id.txtviewNoOfTimesView);
            txtproducts_markpremium = (TextView) view.findViewById(R.id.txtproducts_markpremium);
            txtview12 = (TextView) view.findViewById(R.id.txtview12);
            txtdelete = (TextView) view.findViewById(R.id.txtdelete);
            Layout_forBottomDetails = (LinearLayout) view.findViewById(R.id.thumbnail);
            entrieslistcontent = (LinearLayout) view.findViewById(R.id.entrieslistcontent);
            txtview = (TextView) view.findViewById(R.id.txtview);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_product_listing_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //Data recieved in following format :
        //user_id"#"name#institute"#"batch"#"branch#likes#isinterested
        //holder.imageView.setImageResource(horizontalList.get(position).imageId);

        if (horizontalList.get(position).txt.contains("#"))
        {
            //product_id,product_name,product_price,product_category,description,creator_name,creator_id,creator_college,cell,isactive
            String Name_withhas = horizontalList.get(position).txt;
            String[] Name_withouthash = Name_withhas.split("#");

            //If My Product is Premium show Dimond
            if(Name_withouthash[Name_withouthash.length - 1].equalsIgnoreCase("1"))
                    holder.Premium_dimond.setVisibility(View.VISIBLE);
            else
                holder.Premium_dimond.setVisibility(View.GONE);
///////////////////////
            if(Name_withouthash.length >=7) {
                holder.txtview.setText(Name_withouthash[1]);
                holder.txtview12.setText(get_category_name(Name_withouthash[3]));

                try {
                    holder.txtview_likes.setText(Name_withouthash[2] + " Rs.");
                } catch (Exception e) {
                }
            }
            else {
                holder.txtview.setText(Name_withouthash[1]);
                holder.txtview12.setText("-");
            }


            if (context instanceof MainActivity) {
                Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
                int width = ((display.getWidth() * 30) / 100);
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
                holder.entrieslistcontent.setLayoutParams(parms);
            }
            if (context instanceof Product_listing_my) {
                holder.Layout_forBottomDetails.setVisibility(View.VISIBLE);
                holder.txtdelete.setVisibility(View.VISIBLE);
                holder.txtproducts_markpremium.setVisibility(View.VISIBLE);
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
                holder.txtdelete.setVisibility(View.GONE);
                holder.txtproducts_markpremium.setVisibility(View.GONE);
                holder.imageView.setImageResource(R.drawable.product);
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
                                Intent intent = new Intent(context, Product_listing.class);
                                context.startActivity(intent);
                            }
                          else if (list.equalsIgnoreCase("0#more"))
                            {
                                Next_count = Next_count + 10;
                                if (context instanceof Product_listing_my) {
                                    ((Product_listing_my) context).fetchProductOnTapri(Next_count,2);
                                }
                            }

                            else if (list.equalsIgnoreCase("1#No More"))
                            {
                                holder.imageView.setImageResource(R.drawable.next);
                            }
                            else
                            {
                                //product_id,product_name,product_price,product_category,description,creator_name,
                                // creator_id,creator_college,cell,isactive

                                if (list.contains("#")) {
                                    try {
                                        String Id_Name[] = list.split("#");
                                        //0 the part is ID & 1 is NAme
                                        // 2 is College  3 Batch
                                        //4 is Branch
                                        Intent i = new Intent(context, ViewProductProfile.class);
                                        i.putExtra("product_id", Id_Name[0]);
                                        i.putExtra("product_name", Id_Name[1]);
                                        i.putExtra("product_price", Id_Name[2]);
                                        i.putExtra("product_category", Id_Name[3]);
                                        i.putExtra("description", Id_Name[4]);
                                        i.putExtra("creator_name", Id_Name[5]);

                                        i.putExtra("creator_id", Id_Name[6]);
                                        i.putExtra("creator_college", Id_Name[7]);
                                        i.putExtra("cell", Id_Name[8]);
                                        i.putExtra("isactive", Id_Name[9]);

                                        context.startActivity(i);
                                    } catch (Exception e) {
                                        Toast.makeText(context, "No Product details available.#1", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(context, "No Product details available.#2", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            //`Toast.makeText(context, "Something went wrong,Please Retry!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

        holder.txtdelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String list = horizontalList.get(position).txt.toString();

                        if (list.contains("#")) {
                            String Id_Name_cell[] = list.split("#");
                            Yes_no_Dialog_for_Remove(context,holder,  Id_Name_cell[0], Id_Name_cell[1]);


                        } else {
                            Toast.makeText(context, "Not able to add product to cart,Please Retry!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

        holder.txtproducts_markpremium.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String list = horizontalList.get(position).txt.toString();
                        Log.v("List","#$"+list);
                        if (list.contains("#")) {
                            try {
                                String Id_Name[] = list.split("#");
                                //0 the part is ID & 1 is NAme : Id_Name[0]
                                Ask_To_Make_It_Premium(Id_Name[0],Id_Name[1],Id_Name[2]);
                            } catch (Exception e) {
                                Toast.makeText(context, "Not able to mark product as premium,Please Retry!#1", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, "Not able to mark product as premium,Please Retry!#2", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    private void Ask_To_Make_It_Premium(final  String ProductId,final String ProductName,final String Price)
    {
        //Set ProductId To Premium
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.custom_dialog_premium);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                context.startActivity(new Intent(context,activity_mark_premium.class).putExtra("ProductId",ProductId).putExtra("Name",ProductName).putExtra("Price",Price));

                dialog.dismiss();

            }
        });

        Button dialogButtonNotNow = (Button) dialog.findViewById(R.id.dialogButtonNotNow);
        // if button is clicked, close the custom dialog
        dialogButtonNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                dialog.dismiss();

            }
        });

        dialog.show();
    }


    private void Yes_no_Dialog_for_Remove(final Context context, final adapter_my_product_listing.MyViewHolder holder, final String s, final String s1)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Remove Product?");
        builder.setMessage("Are you sure to remove " + s1 + " from product list? \n\nOnce you Remove,it will not be undone.");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                delete_my_item(context,s,s1);
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

    private void delete_my_item(final Context context, String ProductId, final String ProductName)
    {
        //Remove Product from cart
        //CAll If Internet is available
        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();


        // check for Internet status
        if (isInternetPresent)
        {
            //Call Asynch task to offer a Tea
            progress = new ProgressDialog(context);
            progress.setMessage("Listing Products...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.show();

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    EndPoints.DELETE_MY_PRODUCTS_ON_TAPRI+ "/" + ProductId  , new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        progress.dismiss();
                    } catch (Exception e) {
                    }

                    try {

                        JSONObject obj = new JSONObject(response);
                        try {
                            progress.dismiss();
                        } catch (Exception e) {
                        }
                        // check for error flag
                        if (obj.getString("status").equalsIgnoreCase("Succesfull"))
                        {
                            ((Product_listing_my) context).fetchProductOnTapri(0,3);
                            Toast.makeText(context, ProductName + " Deleted.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            // error in fetching chat rooms
                            Toast.makeText(context, "Please Retry.\n" +
                                    "[Check Internet Connection.#1]", Toast.LENGTH_SHORT).show();
                            try {
                                progress.dismiss();
                            } catch (Exception e) {
                            }

                        }

                    } catch (JSONException e) {
                        Toast.makeText(context, "Please Retry.\n" +
                                "[Check Internet Connection.#2]", Toast.LENGTH_SHORT).show();
                        try {
                            progress.dismiss();
                        } catch (Exception exc) {
                        }

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;

                    try {
                        progress.dismiss();
                    } catch (Exception e) {
                    }
                    Toast.makeText(context, "Please Retry.\n" +
                            "[Check Internet Connection.#3]", Toast.LENGTH_SHORT).show();
                }
            });

            //Adding request to request queue
            MyApplication.getInstance().addToRequestQueue(strReq);

        } else {
            Toast.makeText(context, "Please Retry.\n" +
                    "Check Internet Connection.", Toast.LENGTH_SHORT).show();
        }

    }

    private String get_category_name(String s)
    {
        if(s.equalsIgnoreCase("1"))
            return "BOOKS";
        if(s.equalsIgnoreCase("2"))
            return "STATIONARY";
        if(s.equalsIgnoreCase("3"))
            return "FOOD ZONE";
        if(s.equalsIgnoreCase("4"))
            return "PG";
        if(s.equalsIgnoreCase("5"))
            return "GADGETS";
        if(s.equalsIgnoreCase("6"))
            return "ACCESSORIES";
        if(s.equalsIgnoreCase("7"))
            return "OTHER";
        return "-";
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