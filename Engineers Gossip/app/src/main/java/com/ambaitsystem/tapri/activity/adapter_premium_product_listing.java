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
import android.util.Log;
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


public class adapter_premium_product_listing extends RecyclerView.Adapter<adapter_premium_product_listing.MyViewHolder> {


    List<Data> horizontalList = Collections.emptyList();
    Context context;

    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    public Integer Next_count = 0;
    private ProgressDialog progress;


    public adapter_premium_product_listing(List<Data> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView txtview, txtaddtocard, txtcall;
        TextView txtview_likes, txtviewproducts, txtview12, txtview_college;
        LinearLayout Layout_forBottomDetails,details_layout,ALL_layout_layout;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
            txtview_likes = (TextView) view.findViewById(R.id.txtviewNoOfTimesView);
            txtviewproducts = (TextView) view.findViewById(R.id.txtviewproducts);
            txtaddtocard = (TextView) view.findViewById(R.id.txtaddtocard);
            txtview12 = (TextView) view.findViewById(R.id.txtview12);
            Layout_forBottomDetails = (LinearLayout) view.findViewById(R.id.thumbnail);
            details_layout = (LinearLayout) view.findViewById(R.id.details_layout);
            ALL_layout_layout= (LinearLayout) view.findViewById(R.id.ALL_layout_layout);
            txtview = (TextView) view.findViewById(R.id.txtview);
            txtcall = (TextView) view.findViewById(R.id.txtcall);
            txtview_college = (TextView) view.findViewById(R.id.txtview_college);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_advertise_row, parent, false);

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

            if (Name_withouthash.length >= 7)
            {
                holder.details_layout.setVisibility(View.VISIBLE);
                holder.ALL_layout_layout.setVisibility(View.GONE);
                holder.txtview.setText(Name_withouthash[1]);
                holder.txtview12.setText(get_category_name(Name_withouthash[3]));
                holder.txtview_college.setText(Name_withouthash[7]);
                try {
                    holder.txtview_likes.setText(Name_withouthash[2] + " Rs.");
                } catch (Exception e) {
                }
            } else {
                if (Name_withhas.contains("ALL")) {
                    holder.details_layout.setVisibility(View.GONE);
                    holder.ALL_layout_layout.setVisibility(View.VISIBLE);
                }

            }


            //Load Image of the user
            //Using Universal Image Loaded Load Image on Top Of Text1 : URL is URL_ForImage
            //////////////////Load image From Server//////////////////////////////////////////////////
            // creating connection detector class instance
            cd = new ConnectionDetector(context);
            isInternetPresent = cd.isConnectingToInternet();

            // check for Internet status
            if (isInternetPresent) {
                String URL_ForImage = "http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/product/" + Name_withouthash[0] + ".jpg";
                ImageLoader imageLoader = ImageLoader.getInstance();

                String MyId = MyApplication.getInstance().getPrefManager().getUser().getId();
                DisplayImageOptions options;
                if (Name_withouthash[0].equalsIgnoreCase(MyId)) {
                    options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                            .displayer(new RoundedBitmapDisplayer(1000))
                            .showImageForEmptyUri(R.drawable.product)
                            .showImageOnFail(R.drawable.product)
                            .showImageOnLoading(R.drawable.product_loading).build();
                } else {
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
                            if (list.contains("ALL")) {
                                Intent intent = new Intent(context, Product_listing.class);
                                context.startActivity(intent);
                            } else if (list.equalsIgnoreCase("0#more")) {
                                Next_count = Next_count + 10;

                                if (context instanceof Product_listing) {
                                    ((Product_listing) context).fetchProductOnTapri(Next_count, 2);
                                }
                            } else if (list.equalsIgnoreCase("1#No More")) {
                                holder.imageView.setImageResource(R.drawable.next);
                            } else {
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
                        } catch (Exception e) {
                            //`Toast.makeText(context, "Something went wrong,Please Retry!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

        holder.txtaddtocard.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String list = horizontalList.get(position).txt.toString();
                        if (list.contains("#")) {
                            String Id_Name_cell[] = list.split("#");
                            //0:Id 1 :ProductName 2:product_price 5:creator_name 7:creator_college 8:cell
                            Add_to_Cart(context, Id_Name_cell[0], Id_Name_cell[1], Id_Name_cell[2], Id_Name_cell[5], Id_Name_cell[7], Id_Name_cell[8]);
                        } else {
                            Toast.makeText(context, "Not able to add product to cart,Please Retry!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

        holder.txtcall.setOnClickListener(
                new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        String list = horizontalList.get(position).txt.toString();
                        if (list.contains("#")) {
                            String Id_Name_cell[] = list.split("#");
                            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);

                            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(
                                        (Activity) context,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        123);
                            } else {
                                context.startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+ Id_Name_cell[8])));
                            }
                        } else {
                            Toast.makeText(context, "Not able to Place call,Please allow call from settings!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);



        holder.txtviewproducts.setOnClickListener(
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
                                Toast.makeText(context, "Not able to load Product details,Please Retry!#1", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, "Not able to load Product details,Please Retry!#2", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    private String get_category_name(String s) {
        if (s.equalsIgnoreCase("1"))
            return "BOOKS";
        if (s.equalsIgnoreCase("2"))
            return "STATIONARY";
        if (s.equalsIgnoreCase("3"))
            return "FOOD ZONE";
        if (s.equalsIgnoreCase("4"))
            return "PG";
        if (s.equalsIgnoreCase("5"))
            return "GADGETS";
        if (s.equalsIgnoreCase("6"))
            return "ACCESSORIES";
        if (s.equalsIgnoreCase("7"))
            return "OTHER";
        return "-";
    }

    //0:Id 1 :ProductName 2:product_price 5:creator_name 7:creator_college 8:cell
    private void Add_to_Cart(final Context context, String ProductId, final String ProductName, final String product_price, String creator_name, String creator_college, String cell) {

        //Insert Into Table
        //---------------------------------------------------------------------------------------------------------
        SQLiteDatabase db = null;

        db = (new DbBasic(context)).getWritableDatabase();
        try {
            db.execSQL("INSERT INTO cart (product_id,product_name,creator_name,cell) VALUES ( '" + ProductId + "','" + ProductName + " Of " + product_price + " Rs." + "','" + creator_name + "','" + cell + "');");
            db.close();
            Toast.makeText(context, ProductName + " Added to cart.", Toast.LENGTH_SHORT).show();

            Product_listing.Set_cart_count(context);

        } catch (Exception e) {
            Log.v("EXception", "#" + e.toString());
            db.close();
        }

    }


    public void Dialog_Information(Context context, String Title, String Message) {
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
