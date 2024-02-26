package com.ambaitsystem.tapri.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
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
import com.ambaitsystem.tapri.helper.DbBasic;
import com.ambaitsystem.tapri.library.JSONParser;
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

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


public class LoginActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final int PERMISSION_REQUEST_CONTACT = 123;
    public SQLiteDatabase db = null;
    private String TAG = LoginActivity.class.getSimpleName();
    private EditText inputName;
    private Spinner spinneradmissionyear, spinnerbranches, spinnercollegename;
    private TextView AllowsContacts, error_message, txtview_skip;
    private Button btnEnter, btnhelp_login;
    private LinearLayout Lin_retry, Lin_progressbar, Lin_main_layout;
    private ImageView btnImageProfile;
    private String strbranch = "", strcollege = "", stradmission_year = "", stremail = "";
    // URL to get JSON Array
    private String url = null;
    private AlphaAnimation alphaDown;
    private AlphaAnimation alphaUp;
    LinearLayout details_layout, image_layout;
    //private String Name = null;
    // JSON Node Names
    private static final String TAG_VERSION = "version";
    private static final String TAG_UPDATE = "updaterequired";
    private static final String TAG_ADDV = "adv";
    Uri mCropImageUri;

    private String PubEmailAddress = "";
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    JSONArray user = null;
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    private int PICK_IMAGE_REQUEST = 1;
    private Spinner Spinner_email;
    public DonutProgress donut_progress;
    private Bitmap bitmap;
    ImageView profile_imageview;
    TextView txtview_done;
    String imagepath;
    File sourceFile;
    int totalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (MyApplication.getInstance().getPrefManager().getUser() != null) {
            startActivity(new Intent(getBaseContext(), WelcomeActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        MyApplication.getInstance().updateAndroidSecurityProvider();
        inputName = (EditText) findViewById(R.id.input_name);

        Spinner_email = (Spinner) findViewById(R.id.spinner_email);
        Spinner_email.setOnItemSelectedListener(this);

        askForContactPermission();

        spinneradmissionyear = (Spinner) findViewById(R.id.spinnerbatch);
        spinneradmissionyear.setOnItemSelectedListener(this);

        spinnercollegename = (Spinner) findViewById(R.id.spinner_collegename);
        spinnercollegename.setOnItemSelectedListener(this);


        spinnerbranches = (Spinner) findViewById(R.id.spinnerbranch);
        spinnerbranches.setOnItemSelectedListener(this);

        AllowsContacts = (TextView) findViewById(R.id.txtviewnoemail);
        AllowsContacts.setVisibility(View.GONE);

        error_message = (TextView) findViewById(R.id.error_message);

        btnEnter = (Button) findViewById(R.id.btn_enter);
        btnEnter.setOnClickListener(this);

        btnhelp_login = (Button) findViewById(R.id.btnhelp_login);
        btnhelp_login.setOnClickListener(this);

        Lin_retry = (LinearLayout) findViewById(R.id.retry_layout);
        Lin_progressbar = (LinearLayout) findViewById(R.id.progress_layout);
        Lin_main_layout = (LinearLayout) findViewById(R.id.main_layout);

        donut_progress = (DonutProgress) findViewById(R.id.donut_progress);
        profile_imageview = (ImageView) findViewById(R.id.profile_imageview);
        txtview_skip = (TextView) findViewById(R.id.txtview_skip);
        txtview_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, WelcomeActivity.class);
                finish();
                startActivity(i);
            }
        });
        txtview_done = (TextView) findViewById(R.id.txtview_done);
        txtview_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                builder.setTitle("Have you done?");
                builder.setMessage("Profile photo is mandatory.Have you uploaded appropriate photo?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        Intent i = new Intent(LoginActivity.this, activity_main_screen.class);
                        i.putExtra("skip", "0");
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
        });

        //bind_emai_To_Spinner();
        bind_admissionyear_To_Spinner();
        bind_branch_To_Spinner();
        //Set AutoComplete to College NAme
        bind_collgename_To_Spinner();

        //Set Values to all the control

        //Create Table To Store Incomming Messages for Single User
        Create_table_incomming_message_for_Single_User();
        Create_table_for_cart();
        profile_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(LoginActivity.this);
            }
        });
        TextView txtviewChangeproductPic = (TextView) findViewById(R.id.txtviewChangeprofilePic);
        txtviewChangeproductPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(LoginActivity.this);
            }
        });


        /////
        //Animation
        ////////
        alphaDown = new AlphaAnimation(1.0f, 0f);
        alphaUp = new AlphaAnimation(0f, 1.0f);
        alphaDown.setDuration(1000);
        alphaUp.setDuration(1000);
        alphaDown.setFillAfter(true);
        alphaUp.setFillAfter(true);

        image_layout = (LinearLayout) findViewById(R.id.login_image_layout);
        details_layout = (LinearLayout) findViewById(R.id.login_detail_layout);

        //When load Hide Image layout while show Detail layout
        details_layout.startAnimation(alphaUp);
        image_layout.startAnimation(alphaDown);

        //Show & Hide
        details_layout.setVisibility(View.VISIBLE);
        image_layout.setVisibility(View.GONE);
    }

    private void Create_table_incomming_message_for_Single_User() {
        //---------------------------------------------------------------------------------------------------------
        //Create Table : GuardianEmail
        //---------------------------------------------------------------------------------------------------------
        db = (new DbBasic(this)).getWritableDatabase();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS single_users_messages (_id INTEGER AUTO_INCREMENT,user_id TEXT,name TEXT,message TEXT,created_at TEXT,email TEXT,isactive INTEGER);");
            //String Insert_Query = "INSERT INTO GuardianEmail (user_id,name,message,created_at,email,isactive) VALUES('','','','','',1);";
            //db.execSQL(Insert_Query);
            db.close();
        } catch (Exception e) {
            db.close();
        }
    }

    private void Create_table_for_cart() {
        //---------------------------------------------------------------------------------------------------------
        //Create Table : GuardianEmail
        //---------------------------------------------------------------------------------------------------------
        db = (new DbBasic(this)).getWritableDatabase();
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS cart (_id INTEGER AUTO_INCREMENT,product_id TEXT,product_name TEXT,description TEXT,creator_name TEXT,cell TEXT);");
            db.close();
        } catch (Exception e) {
            db.close();
        }
    }

    private void addAdapterToViews() {

        Account[] accounts = AccountManager.get(this).getAccounts();
        Set<String> emailSet = new HashSet<String>();
        for (Account account : accounts) {
            if (EMAIL_PATTERN.matcher(account.name).matches()) {
                emailSet.add(account.name);
            }
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, new ArrayList<String>(emailSet));

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        Spinner_email.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>(emailSet)));

    }

    public int getVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    private void Setusername_Email_inText(String Email_Address) {
        PubEmailAddress = Email_Address;
        if (Email_Address != null) {
            //Set Name from Email
            if (Email_Address.contains("@")) {
                String[] Name = Email_Address.split("@");
                if (inputName.getText().toString().equalsIgnoreCase(""))
                    inputName.setText(Name[0].toString());
            } else {
                AllowsContacts.setVisibility(View.VISIBLE);
                AllowsContacts.setText("Enter valid email address.");
            }
        } else {
            try {
                AllowsContacts.setVisibility(View.VISIBLE);
                AllowsContacts.setText("Email is not Available.Allow App to Read contacts.\n\n Goto Settings > Apps > " + getApplicationName(this));
            } catch (Exception e) {
            }
        }

    }


    public void bind_collgename_To_Spinner() {
        String[] mTestArray;

        try {
            mTestArray = getResources().getStringArray(R.array.college_name);

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, mTestArray);

            // attaching data adapter to spinner
            spinnercollegename.setAdapter(dataAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "College Name Not Bound,Please Retry!", Toast.LENGTH_LONG).show();
        }
    }

    public void bind_admissionyear_To_Spinner() {
        String[] mTestArray;

        try {
            mTestArray = getResources().getStringArray(R.array.yearofadmission);

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, mTestArray);

            // attaching data adapter to spinner
            spinneradmissionyear.setAdapter(dataAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Admission Year Not Bound,Please Retry!", Toast.LENGTH_LONG).show();
        }
    }

    public void bind_branch_To_Spinner() {
        String[] mTestArray;

        try {
            mTestArray = getResources().getStringArray(R.array.department);

            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, mTestArray);

            // attaching data adapter to spinner
            spinnerbranches.setAdapter(dataAdapter);


        } catch (Exception e) {
            Toast.makeText(this, "Branches Not Bound,Please Retry!", Toast.LENGTH_LONG).show();
        }
    }

    /*  public void bind_emai_To_Spinner() {
          String possibleEmail = "";

          try {
              EmailListing = new ArrayList<String>();
              Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");

              for (Account account : accounts) {
                  possibleEmail += account.name;
                  EmailListing.add(account.name);
              }
              // Creating adapter for spinner
              ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.spinner_row, EmailListing);

              // Drop down layout style - list view with radio button
              dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

              // attaching data adapter to spinner
              spinneremail.setAdapter(dataAdapter);


          } catch (Exception e) {
              Toast.makeText(this, "Email Address Not Bound,Please Retry!", Toast.LENGTH_LONG).show();
          }
      }
  */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {


            switch (parent.getId()) {
                case R.id.spinner_email:
                    // On selecting a spinner item
                    stremail = parent.getItemAtPosition(position).toString();
                    break;

                case R.id.spinnerbatch:
                    stradmission_year = parent.getItemAtPosition(position).toString();
                    break;
                case R.id.spinnerbranch:
                    strbranch = parent.getItemAtPosition(position).toString();
                    break;

                case R.id.spinner_collegename:
                    strcollege = parent.getItemAtPosition(position).toString();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    // Validating email
    private boolean validateEmail() {
        if (stremail.equalsIgnoreCase("") || stremail == null) {
            askForContactPermission();
            return false;
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        // onClick of button perform this simplest code.
        if (stremail.matches(emailPattern)) {
            return true;
        } else {
            AllowsContacts.setVisibility(View.VISIBLE);
            AllowsContacts.setText("Email Is not valid.If No Email available in the list,Goto Settings > App > " + getAppLable(this) + " Allow to Read Contact.");
            return false;
        }

    }

    public String getAppLable(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }

    public void askForContactPermission()

    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                addAdapterToViews();
            }
        } else {
            addAdapterToViews();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_enter:

                //Upload Image
                if (validateEmail()) {
                    Setusername_Email_inText(stremail);

                    if (stradmission_year.contains("Select") || strbranch.contains("Select") || strcollege.contains("Select") || stremail.equalsIgnoreCase("")) {
                        AllowsContacts.setVisibility(View.VISIBLE);
                        AllowsContacts.setText("Email,College Name,Department or Admission year is missing.");
                    } else {
                        //OnSignin Hide main_layout,retry_layout
                        //Show progress_layout
                        Lin_main_layout.setVisibility(View.GONE);
                        Lin_retry.setVisibility(View.GONE);
                        Lin_progressbar.setVisibility(View.VISIBLE);

                        cd = new ConnectionDetector(getApplicationContext());
                        // get Internet status
                        isInternetPresent = cd.isConnectingToInternet();

                        // check for Internet status
                        if (isInternetPresent) {

                            UploadUserInformation(strcollege, stremail);
                        } else {
                            btnEnter.setText("Retry");
                            error_message.setText("Server connection problem,Retry.");
                            Lin_main_layout.setVisibility(View.VISIBLE);
                            Lin_progressbar.setVisibility(View.GONE);

                            try {
                                btnEnter.setEnabled(true);
                            } catch (Exception e) {
                            }
                            Toast.makeText(this, "Check Connection & Retry.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Email Is not valid.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnhelp_login:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                alertDialog.setTitle("Empty Selection List?");
                alertDialog.setIcon(R.drawable.icon);
                alertDialog.setMessage("Open the Settings and tap Apps under the Device heading.\n\nSelect Engineer\'s Gossip > Permission > Allow Contact \n\nNote : We Do not access your account information.");
                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.show();
                break;
        }

    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Wait ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            try {
                // Getting JSON from URL
                JSONObject json = jParser.getJSONFromUrl(url);
                return json;
            } catch (Exception e) {
                Log.v("Error", "#$" + e.toString());
                Toast.makeText(getApplicationContext(), "No Connection to server,Please Retry![#0]", Toast.LENGTH_LONG);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            if (json != null) {

                try {

                    // Getting JSON Array
                    user = json.getJSONArray(TAG_VERSION);
                    if (user.length() > 0) {
                        JSONObject c = user.getJSONObject(0);

                        try {
                            // Storing JSON item in a Variable
                            String updaterequired = c.getString(TAG_UPDATE);
                            String advertise = c.getString(TAG_ADDV);

                            //check if Updates Available or not
                            //If Available than start Update Screen
                            //Else Start MainActivity

                            //Send This Version Code to Webservice
                            //Which will check update Required OR Not.

                            if (updaterequired.equalsIgnoreCase("true")) {
                                //GoTo Update Activity
                                startActivity(new Intent(getBaseContext(), UpdateActivity.class));
                                finish();

                            } else {
                                /**
                                 * Check for login session. It user is already logged in & No Update Required
                                 * redirect him to main activity
                                 * */

                                startActivity(new Intent(getBaseContext(), MainActivity.class));
                                finish();
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "No Connection to server,Please Retry![#1]", Toast.LENGTH_LONG);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Connection to server,Please Retry![#2]", Toast.LENGTH_LONG);
                    }
                    try {
                        pDialog.dismiss();
                    } catch (Exception ea) {
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "No Connection to server,Please Retry![#3]", Toast.LENGTH_LONG);

                }
            } else {
                Toast.makeText(getApplicationContext(), "No Connection to server,Please Retry![#4]", Toast.LENGTH_LONG);
                try {
                    pDialog.dismiss();

                } catch (Exception e) {

                }
            }

        }
    }

    private void showFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    private void UploadUserInformation(final String strcollege, final String Email_id) {

        if (!validateEmail()) {
            return;
        }

        btnEnter.setEnabled(false);

        final String email = Email_id;
        final String validationcode = "1234";
        final String name = "abc";
        //Showing the progress dialog

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                btnEnter.setEnabled(true);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in

                        JSONObject userObj = obj.getJSONObject("user");
                        User user = new User(userObj.getString("user_id"),
                                userObj.getString("name"),
                                userObj.getString("email"),
                                userObj.getString("institute"),
                                userObj.getString("batch"),
                                userObj.getString("branch")
                        );
                        // storing user in shared preferences
                        MyApplication.getInstance().getPrefManager().storeUser(user);
                        details_layout.startAnimation(alphaDown);
                        image_layout.startAnimation(alphaUp);

                        //Show & Hide
                        details_layout.setVisibility(View.GONE);
                        image_layout.setVisibility(View.VISIBLE);
                        //Show Upload Image

                    } else {

                        //If error arrived than
                        //Hide progress_layout
                        //Show main_layout
                        //Set text from Signin to REtry
                        btnEnter.setText("Retry");
                        error_message.setText("Server connection problem,Retry.");
                        Lin_main_layout.setVisibility(View.VISIBLE);
                        Lin_progressbar.setVisibility(View.GONE);

                        try {
                            btnEnter.setEnabled(true);
                        } catch (Exception e) {
                        }
                        // login error - simply toast the message
                        Toast.makeText(getApplicationContext(), "Not able to varify email,Please Retry.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    //If Exception arrived than
                    //Hide progress_layout
                    //Show main_layout
                    //Set text from Signin to REtry
                    btnEnter.setText("Retry");
                    error_message.setText("Server connection error,Retry.");
                    Lin_main_layout.setVisibility(View.VISIBLE);
                    Lin_progressbar.setVisibility(View.GONE);

                    try {

                        btnEnter.setEnabled(true);
                    } catch (Exception e1) {
                    }
                    Log.v(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Invalid Data,Please Retry.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //If error arrived than
                //Hide main_layout
                //Show retry_layout
                //SEt Signin to retry
                btnEnter.setText("Retry");
                error_message.setText("Server connection problem,Retry.");
                error.printStackTrace();
                Lin_main_layout.setVisibility(View.VISIBLE);
                Lin_progressbar.setVisibility(View.GONE);


                Toast.makeText(getApplicationContext(), "Check Connectionserver,Retry.", Toast.LENGTH_SHORT).show();
                try {
                    btnEnter.setEnabled(true);
                } catch (Exception e) {
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                //Getting Image Name
                String name = inputName.getText().toString().trim();

                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("college", strcollege);
                params.put("year", stradmission_year);
                params.put("department", strbranch);
                params.put("validationcode", validationcode);

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

    private void LoadImage(LoginActivity context, String id) {


        cd = new ConnectionDetector(context);
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {


            String URL_ForImage = "http://niruma.tv/ait/CookBookChatApp/enginnerchat/upload/" + id + ".jpg";
            ImageLoader imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.user_top)
                    .showImageOnFail(R.drawable.user_top)
                    .showImageOnLoading(R.drawable.user_loading).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    context)
                    .defaultDisplayImageOptions(options)
                    .build();

            ImageLoader.getInstance().init(config);
            //download and display image from url
            imageLoader.displayImage(URL_ForImage, profile_imageview, options);

        } else {
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
                ((ImageView) findViewById(R.id.profile_imageview)).setImageURI(result.getUri());
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                imagepath = saveOutput(bitmap);
                new LoginActivity.UploadFileToServer().execute();
                //Setting the Bitmap to ImageView
            } catch (IOException e) {
                Toast.makeText(LoginActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        }

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CONTACT) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addAdapterToViews();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                addAdapterToViews();
            } else {
                Toast.makeText(this, "No permission for contacts.Allow App to Read contacts. Goto Settings > Apps > " + getApplicationName(this), Toast.LENGTH_SHORT).show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
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
        profile_imageview.setImageBitmap(bitmap);

        return path;
    }


    private class UploadFileToServer extends AsyncTask<String, String, String> {
        String line = "12";

        @Override
        protected void onPreExecute()
        {
            // setting progress bar to zero
            donut_progress.setProgress(0);
            profile_imageview.setVisibility(View.VISIBLE); // Making the uploader area screen invisible
            donut_progress.setVisibility(View.VISIBLE); // Showing the stylish material progressbar
            sourceFile = new File(imagepath);
            totalSize = (int) sourceFile.length();
            txtview_done.setVisibility(View.VISIBLE);
            txtview_done.setText("Wait..");
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
                connection = (HttpURLConnection) new URL(EndPoints.FILE_UPLOAD_URL + "?filename=" + fileName).openConnection();
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
                profile_imageview.setVisibility(View.VISIBLE); // Making the uploader area screen invisible
                donut_progress.setVisibility(View.GONE); // Showing the stylish material progressbar
                txtview_done.setVisibility(View.VISIBLE);
                txtview_done.setText("Done");
                Toast.makeText(LoginActivity.this, "Profile picture set.", Toast.LENGTH_SHORT).show();
                //LoadImage(ViewOwnProfile.this,id);

            } else {

                donut_progress.setProgress(0);
                profile_imageview.setVisibility(View.VISIBLE); // Making the uploader area screen invisible
                txtview_done.setVisibility(View.VISIBLE);
                donut_progress.setVisibility(View.GONE); // Showing the stylish material progressbar
                txtview_done.setText("Failed.Set Profile picture again and Retry.");
                Toast.makeText(LoginActivity.this, "Check connection and Retry.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }
}
