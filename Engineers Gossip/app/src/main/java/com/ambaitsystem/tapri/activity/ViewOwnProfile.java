package com.ambaitsystem.tapri.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.app.MyApplication;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.tapri.model.User;
import com.ambaitsystem.vgecchat.R;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

public class ViewOwnProfile extends AppCompatActivity
{
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    private ProgressDialog progress;
    Context context;

    public DonutProgress donut_progress;
    String imagepath;
    File sourceFile;
    int totalSize = 0;
    private Bitmap bitmap;
    ImageView profile_imageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewownprofile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        context = this;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        getWindow().setLayout(screenWidth, ActionBar.LayoutParams.WRAP_CONTENT);

        User user = MyApplication.getInstance().getPrefManager().getUser();
        // get data via the key
        final String id = user.getId();
        final String name = user.getName();
        String college = user.getcollegename();
        String batch = user.getadmission_index();
        String timesview = "0";
        String department = user.getdepartment_index();
       //Increment the View COunt

        donut_progress = (DonutProgress) findViewById(R.id.donut_progress);
        profile_imageview = (ImageView) findViewById(R.id.profile_imageview);
        TextView txtname = (TextView)findViewById(R.id.txtName);
        txtname.setText(name);

        TextView txtcollegeName = (TextView)findViewById(R.id.txtCollege);
        txtcollegeName.setText(college);

        TextView txtBatch = (TextView)findViewById(R.id.txtBatch);
        txtBatch.setText(batch );

        TextView txtDepartment = (TextView)findViewById(R.id.txtDepartment);
        txtDepartment.setText(department+ " Batch");

        TextView txtviewChangeProfilePic = (TextView)findViewById(R.id.txtviewChangeProfilePic);
        txtviewChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*"); // intent.setType("video/*"); to select videos to upload
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        //Load Image Using ID
        LoadImage(this,id);

    }

    private void LoadImage(ViewOwnProfile context, String id)
    {


        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent)
        {


            String URL_ForImage ="http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/" +id +".jpg";
            ImageLoader imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                    .cacheOnDisk(false).cacheInMemory(false)
                    .showImageForEmptyUri(R.drawable.user_top)
                    .showImageOnFail(R.drawable.user_top)
                    .showImageOnLoading(R.drawable.user_loading).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    context)
                    .defaultDisplayImageOptions(options)
                    .build()  ;

            ImageLoader.getInstance().init(config);
            //download and display image from url
            imageLoader.displayImage(URL_ForImage, profile_imageview, options);

        }
        else
        {
            profile_imageview.setImageResource(R.drawable.user_loading);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //Getting the Bitmap from Gallery
                try {
                    ((ImageView) findViewById(R.id.profile_imageview)).setImageURI(result.getUri());
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    imagepath = saveOutput(bitmap);
                    new ViewOwnProfile.UploadFileToServer().execute();
                    //Setting the Bitmap to ImageView
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Toast.makeText(this, "Cropping failed.Change Image,Retry", Toast.LENGTH_LONG).show();
            }
        }


        if (requestCode == 1 && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            startCropImageActivity(selectedImageUri);


        }
    }


    private String saveOutput(Bitmap croppedImage)
    {
        Uri saveUri = null;
        String path = null;
        // Saves the image in cache, you may want to modify this to save it to Gallery
        File file = new File(getCacheDir(), "cropped.jpg");
        OutputStream outputStream = null;
        try
        {
            file.getParentFile().mkdirs();
            saveUri = Uri.fromFile(file);
            path = file.getAbsolutePath();

            outputStream = getContentResolver().openOutputStream(saveUri);
            if (outputStream != null)
            {
                croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            }
        } catch (IOException e)
        {
            // log the error
        }
        profile_imageview.setImageBitmap(bitmap);

        return path;
    }


    private class UploadFileToServer extends AsyncTask<String, String, String>
    {
        String line="12";
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            donut_progress.setProgress(0);
            profile_imageview.setVisibility(View.VISIBLE); // Making the uploader area screen invisible
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
            String fileName = MyApplication.getInstance().getPrefManager().getUser().getId() + ".jpg";

            try {
                connection = (HttpURLConnection) new URL(EndPoints.FILE_UPLOAD_URL + "?filename="+fileName).openConnection();
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
        protected void onPostExecute(String result)
        {
            //1 : for Scuucess
            if(result.equalsIgnoreCase("1"))
            {
                User user = MyApplication.getInstance().getPrefManager().getUser();
                // get data via the key
                final String id = user.getId();
                profile_imageview.setVisibility(View.VISIBLE); // Making the uploader area screen invisible
                donut_progress.setVisibility(View.GONE); // Showing the stylish material progressbar
                Toast.makeText(ViewOwnProfile.this, "Profile Picture set.", Toast.LENGTH_SHORT).show();
                //LoadImage(ViewOwnProfile.this,id);

            }
            else {

                donut_progress.setProgress(0);
                profile_imageview.setVisibility(View.VISIBLE); // Making the uploader area screen invisible
                donut_progress.setVisibility(View.GONE); // Showing the stylish material progressbar

                Toast.makeText(ViewOwnProfile.this, "Check connection and Retry.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }
}