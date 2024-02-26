package com.ambaitsystem.tapri.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.model.User;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Product_My_Upload extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;
    Context context;

    public DonutProgress donut_progress;
    String imagepath, productcategory;
    File sourceFile;
    int totalSize = 0;
    private Bitmap bitmap;
    ImageView product_imageview;
    TextView txtviewChangeproductPic, txtsave,txtviewstatus;
    private Spinner spinnerproduct_category;
    LinearLayout details_layout, image_layout;
    EditText edittext_product_title, edittext_product_price, edittext_contact, edittext_description;
    String ProductId;
    Uri mCropImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.productupload);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        context = this;


        User user = MyApplication.getInstance().getPrefManager().getUser();
        // get data via the key
        final String id = user.getId();
        final String name = user.getName();
        String college = user.getcollegename();

        imagepath="";
        //Increment the View COunt

        donut_progress = (DonutProgress) findViewById(R.id.donut_progress);
        donut_progress.setTextColor(R.color.background_buysellcolor);
        product_imageview = (ImageView) findViewById(R.id.product_imageview);

        spinnerproduct_category = (Spinner) findViewById(R.id.spinnerproduct_category);
        spinnerproduct_category.setOnItemSelectedListener(this);

        txtviewstatus = (TextView) findViewById(R.id.txtviewstatus);

        txtviewChangeproductPic = (TextView) findViewById(R.id.txtviewChangeproductPic);
        txtviewChangeproductPic.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CropImage.startPickImageActivity(Product_My_Upload.this);
             /*   Intent intent = new Intent();
                intent.setType("image/*"); // intent.setType("video/*"); to select videos to upload
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);*/
                    }
                });


        txtsave = (TextView) findViewById(R.id.txtviewsave);
        TextView txtviewclose = (TextView) findViewById(R.id.txtviewclose);

        //Create Object of all the controls
        edittext_product_title = (EditText) findViewById(R.id.edittext_product_title);
        edittext_product_price = (EditText) findViewById(R.id.edittext_product_price);
        edittext_contact = (EditText) findViewById(R.id.edittext_contact);
        edittext_description = (EditText) findViewById(R.id.edittext_description);
        //Set LAst Phone number to edittextcontact
        edittext_contact.setText(MyApplication.getInstance().getPrefManager().get_cellnumber());
        image_layout = (LinearLayout) findViewById(R.id.image_layout);
        details_layout = (LinearLayout) findViewById(R.id.details_layout);

        image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(Product_My_Upload.this);
            }
        });
        //Show & Hide
        //details_layout.setVisibility(View.VISIBLE);
        // image_layout.setVisibility(View.GONE);

        txtsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if (txtsave.getText().toString().contains("Save")) {

                    //Get Values
                    final String title = edittext_product_title.getText().toString();
                    final String price = edittext_product_price.getText().toString();
                    final String contact = edittext_contact.getText().toString();
                    final String description = edittext_description.getText().toString();
                    //Validate

                    //Toast.makeText(context, title + " " + price + " " +contact + " "+productcategory, Toast.LENGTH_SHORT).show();
                    if (title.equalsIgnoreCase("") || price.equalsIgnoreCase("") || contact.equalsIgnoreCase("") || productcategory.equalsIgnoreCase("0") || imagepath.equalsIgnoreCase("") ) {
                        txtviewstatus.setText("Product Picture Product Name,Price,Contact and Category type is required.");
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setTitle("Upload product details");
                        builder.setMessage("Once you upload product detail,it can not be change.upload it?");

                        builder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        //Store Cell Number
                                        MyApplication.getInstance().getPrefManager().store_cellnumber(contact);
                                        //Post Values
                                        txtviewstatus.setText("Start saving product details.");
                                        UploadProductDetails(title, price, contact, productcategory, MyApplication.getInstance().getPrefManager().getUser().getName(), MyApplication.getInstance().getPrefManager().getUser().getcollegename(), MyApplication.getInstance().getPrefManager().getUser().getId(), description);
                                        dialog.dismiss();
                                    }
                                });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                    }

                } else {
                    Done_alert(getBaseContext());
                }
            }

        });

        txtviewclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Product_My_Upload.this, Product_listing_my.class);
                finish();
                startActivity(i);
            }
        });

        ////////////////////
        bind_producttype_To_Spinner();

    }

    private void Done_alert(Context baseContext)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Have you done?");
        builder.setMessage("Product photo & Details are mandatory.Have you done?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                Intent i = new Intent(Product_My_Upload.this, Product_listing_my.class);
                finish();
                startActivity(i);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void UploadProductDetails(final String title, final String price, final String contact, final String productcategory, final String name, final String getcollegename, final String id, final String description) {
        progress = new ProgressDialog(context);
        progress.setMessage("Saving " + title);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(true);
        progress.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.CREATE_MY_PRODUCTS_ON_TAPRI, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    progress.dismiss();
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in
                        txtviewstatus.setText("product details saved.");
                        JSONObject userObj = obj.getJSONObject("product_group");
                        ProductId = userObj.getString("product_id");
                        final String ProductName = userObj.getString("product_name");

                        txtviewChangeproductPic.setVisibility(View.GONE);
                        //Show Upload Image for product layout
                        Toast.makeText(context, ProductName + " Saved Successfully.Now Uploading Photo.", Toast.LENGTH_LONG).show();

                        txtviewstatus.setText("Start uploading product picture.");
                        //Upload Image To Server
                        new Product_My_Upload.UploadFileToServer().execute();
                        //Show & Hide
                        //details_layout.setVisibility(View.GONE);
                        //image_layout.setVisibility(View.VISIBLE);

                    } else {
                        progress.dismiss();

                        // login error - simply toast the message
                        Toast.makeText(getApplicationContext(), "Not able to Store Product,Please Retry. #1", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    progress.dismiss();

                    Toast.makeText(getApplicationContext(), "Not able to Store Product,Please Retry. #2", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();

                Toast.makeText(getApplicationContext(), "Something went wrong,please retry.", Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                //String title, String price, String contact, String productcategory, String name, String getcollegename, String id) {
                Map<String, String> params = new HashMap<>();
                params.put("product_name", title);
                params.put("product_category", productcategory);
                params.put("product_price", price);
                params.put("description", description);
                params.put("creator_name", name);
                params.put("creator_college", getcollegename);
                params.put("creator_id", id);
                params.put("cell", contact);

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

    public void bind_producttype_To_Spinner() {
        String[] mTestArray;

        try {
            mTestArray = getResources().getStringArray(R.array.productcategory);

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, mTestArray);

            // attaching data adapter to spinner
            spinnerproduct_category.setAdapter(dataAdapter);


        } catch (Exception e) {
            Toast.makeText(this, "product type not bound,Please Retry!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {


            switch (parent.getId()) {
                case R.id.spinnerproduct_category:
                    // On selecting a spinner item
                    productcategory = String.valueOf(position);
                    //Toast.makeText(context, "Position" + position, Toast.LENGTH_SHORT).show();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void LoadImage(Product_My_Upload context, String id) {


        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {


            String URL_ForImage = "http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/product/" + id + ".jpg";
            ImageLoader imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.product)
                    .showImageOnFail(R.drawable.product)
                    .showImageOnLoading(R.drawable.product_loading).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    context)
                    .defaultDisplayImageOptions(options)
                    .build();

            ImageLoader.getInstance().init(config);
            //download and display image from url
            imageLoader.displayImage(URL_ForImage, product_imageview, options);

        } else {

        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dpWidth = displayMetrics.widthPixels;

        CropImage.activity(imageUri)
                .setAspectRatio(dpWidth, dpWidth)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            mCropImageUri = imageUri;
            // For API >= 23 we need to check specifically that we have permissions to read external storage.

            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);

            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            //Getting the Bitmap from Gallery
            try {
                ((ImageView) findViewById(R.id.product_imageview)).setImageURI(result.getUri());
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                imagepath = saveOutput(bitmap);

                //Setting the Bitmap to ImageView
            } catch (IOException e) {
                Toast.makeText(context, "" + e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        }

        // handle result of CropImageActivity
     /*   if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //Getting the Bitmap from Gallery
                try {
                    ((ImageView) findViewById(R.id.product_imageview)).setImageURI(result.getUri());
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    imagepath = saveOutput(bitmap);
                    new Product_My_Upload.UploadFileToServer().execute();
                    //Setting the Bitmap to ImageView
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed.Change Image,Retry", Toast.LENGTH_LONG).show();
            }
        }


        if (requestCode == 1 && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            startCropImageActivity(selectedImageUri);


        }*/
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String saveOutput(Bitmap croppedImage) {
        Uri saveUri = null;
        String path = null;
        // Saves the image in cache, you may want to modify this to save it to Gallery
        File file = new File(getCacheDir(), "cropped.jpg");
        OutputStream outputStream = null;
        try {
            file.getParentFile().mkdirs();
            saveUri = Uri.fromFile(file);
            path = file.getAbsolutePath();

            outputStream = getContentResolver().openOutputStream(saveUri);
            if (outputStream != null) {
                croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            }
        } catch (IOException e) {
            // log the error
        }
        product_imageview.setImageBitmap(bitmap);

        return path;
    }


    private class UploadFileToServer extends AsyncTask<String, String, String> {
        String line = "12";

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            donut_progress.setProgress(0);
            product_imageview.setVisibility(View.VISIBLE); // Making the uploader area screen invisible
            donut_progress.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
            sourceFile = new File(imagepath);
            totalSize = (int) sourceFile.length();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            Log.d("PROG", progress[0]);
            donut_progress.setProgress(Integer.parseInt(progress[0])); //Updating progress
        }

        @Override
        protected String doInBackground(String... args) {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = null;
            String fileName = ProductId + ".jpg";

            try {
                connection = (HttpURLConnection) new URL(EndPoints.PRODUCT_FILE_UPLOAD_URL + "?filename=" + fileName).openConnection();
                connection.setRequestMethod("POST");
                String boundary = "---------------------------boundary";
                String tail = "\r\n--" + boundary + "--\r\n";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setDoOutput(true);

                String metadataPart = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                        + "" + "\r\n";

                String fileHeader1 = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + fileName + "\"\r\n"
                        + "Content-Type: application/octet-stream\r\n"
                        + "Content-Transfer-Encoding: binary\r\n";

                long fileLength = sourceFile.length() + tail.length();
                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
                String stringData = metadataPart + fileHeader;

                long requestLength = stringData.length() + fileLength;
                connection.setRequestProperty("Content-length", "" + requestLength);
                connection.setFixedLengthStreamingMode((int) requestLength);
                connection.connect();

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(stringData);
                out.flush();

                int progress = 0;
                int bytesRead = 0;
                byte buf[] = new byte[1024];
                BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFile));
                while ((bytesRead = bufInput.read(buf)) != -1) {
                    // write output
                    out.write(buf, 0, bytesRead);
                    out.flush();
                    progress += bytesRead; // Here progress is total uploaded bytes

                    publishProgress("" + (int) ((progress * 100) / totalSize)); // sending progress percent to publishProgress
                }

                // Write closing boundary and close stream
                out.writeBytes(tail);
                out.flush();
                out.close();

                // Get server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                line = reader.readLine();
            } catch (Exception e) {
                return "0";
            } finally {
                if (connection != null) connection.disconnect();
            }
            return line;
        }


        @Override
        protected void onPostExecute(String result) {
            //1 : for Scuucess
            if (result.equalsIgnoreCase("1")) {
                product_imageview.setVisibility(View.VISIBLE); // Making the uploader area screen invisible
                donut_progress.setVisibility(View.GONE); // Showing the stylish material progressbar
                txtsave.setText("Done");
                txtviewChangeproductPic.setText("Product Picture Uploaded");
                Toast.makeText(Product_My_Upload.this, "Product picture set.", Toast.LENGTH_SHORT).show();
                txtviewstatus.setText("Product picture uploaded");
                //Now Ask for Make it premium
                Ask_To_Make_It_Premium();
                //LoadImage(ViewOwnProfile.this,id);

            } else {

                donut_progress.setProgress(0);
                product_imageview.setVisibility(View.VISIBLE); // Making the uploader area screen invisible
                donut_progress.setVisibility(View.GONE); // Showing the stylish material progressbar
                txtviewstatus.setText("Failed to upload product picture.retry");
                Toast.makeText(Product_My_Upload.this, "Check connection and Retry.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }


    private void Ask_To_Make_It_Premium()
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
                startActivity(new Intent(Product_My_Upload.this,activity_mark_premium.class).putExtra("ProductId",ProductId).putExtra("Name",edittext_product_title.getText().toString()).putExtra("Price",edittext_product_price.getText().toString()));

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
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}