package com.ambaitsystem.tapri.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ambaitsystem.tapri.app.EndPoints;
import com.ambaitsystem.tapri.helper.ConnectionDetector;
import com.ambaitsystem.vgecchat.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {

    private static final String REGISTER_URL = "http://niruma.tv/ait/tnews/report_quiz_question.php?id=";
    private TextView quizQuestion;
    public static final String KEY_ID = "id";

    private RadioGroup radioGroup;
    private RadioButton optionOne;
    private RadioButton optionTwo;
    private RadioButton optionThree;
    private RadioButton optionFour;

    private int currentQuizQuestion = 0;
    private int quizCount;
    private int radiocheckid;
    private QuizWrapper firstQuestion;
    private String Number_OF_Quiz = "0";

    private int id_question;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    sound ObjSound;

    private List<QuizWrapper> parsedObject;
    Context context;
    private static int count_correctanswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_quiz);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<b><font  color=\"#FFFFFF\">Quiz</font></b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the shared Tracker instance.

        Number_OF_Quiz = getIntent().getExtras().getString("Subject_Of_Quiz");

        context = this;

        ///////////////////////Report Quiz Question
        TextView imgbtnquestionreport = (TextView) findViewById(R.id.imgbtnquestionreport);
        imgbtnquestionreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report_Question(context, id_question);
            }


        });


        ///////////////////

        quizQuestion = (TextView) findViewById(R.id.quiz_question);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        optionOne = (RadioButton) findViewById(R.id.radio0);
        optionTwo = (RadioButton) findViewById(R.id.radio1);
        optionThree = (RadioButton) findViewById(R.id.radio2);
        optionFour = (RadioButton) findViewById(R.id.radio3);

        //SET Type face
            Typeface typefacename = Typeface.createFromAsset(context.getAssets(), "fonts/shruti.ttf");

        quizQuestion.setTypeface(typefacename);

        optionOne.setTypeface(typefacename);
        optionTwo.setTypeface(typefacename);
        optionThree.setTypeface(typefacename);
        optionFour.setTypeface(typefacename);
        //////


        optionOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reset_RadioButton();
                    optionOne.setTextColor(Color.BLUE);
                    optionTwo.setTextColor(Color.BLACK);
                    optionThree.setTextColor(Color.BLACK);
                    optionFour.setTextColor(Color.BLACK);
                    radiocheckid = 1;
                    Check_Question();
                }
            }
        });

        optionTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reset_RadioButton();
                    optionOne.setTextColor(Color.BLACK);
                    optionTwo.setTextColor(Color.BLUE);
                    optionThree.setTextColor(Color.BLACK);
                    optionFour.setTextColor(Color.BLACK);
                    radiocheckid = 2;
                    Check_Question();
                }
            }
        });

        optionThree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reset_RadioButton();
                    optionOne.setTextColor(Color.BLACK);
                    optionTwo.setTextColor(Color.BLACK);
                    optionThree.setTextColor(Color.BLUE);
                    optionFour.setTextColor(Color.BLACK);
                    radiocheckid = 3;
                    Check_Question();
                }
            }
        });

        optionFour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reset_RadioButton();
                    optionOne.setTextColor(Color.BLACK);
                    optionTwo.setTextColor(Color.BLACK);
                    optionThree.setTextColor(Color.BLACK);
                    optionFour.setTextColor(Color.BLUE);
                    radiocheckid = 4;
                    Check_Question();
                }
            }
        });

        final Button previousButton = (Button) findViewById(R.id.previousquiz);
        final Button nextButton = (Button) findViewById(R.id.nextquiz);
        Button Retry = (Button) findViewById(R.id.retry);

        previousButton.setEnabled(false);

        //Check for Network Availablity
        cd = new ConnectionDetector(getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            showAlertDialog(QuizActivity.this, "No Internet Connection", "No Connectivity.Please check your connection & Retry.", false);
        } else {
            AsyncJsonObject asyncObject = new AsyncJsonObject();
            asyncObject.execute("");
        }

        final TextView imgscore = (TextView) findViewById(R.id.TextViewscore);
        imgscore.setText(count_correctanswer + "Out Of" + quizCount + "Correct.");

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                previousButton.setEnabled(true);
                uncheckedRadioButton();
                Show_Next_Question();

                return;
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButton.setVisibility(View.VISIBLE);

                currentQuizQuestion--;

                if (currentQuizQuestion < 0) {
                    Toast.makeText(QuizActivity.this, "You are on the first Question.", Toast.LENGTH_LONG).show();
                    previousButton.setEnabled(false);
                    return;
                }
                uncheckedRadioButton();

                try {
                    firstQuestion = parsedObject.get(currentQuizQuestion);
                    id_question = firstQuestion.getId();
                    quizQuestion.setText(firstQuestion.getQuestion());

                    String[] possibleAnswers = firstQuestion.getAnswers().split("#");
                    optionOne.setText(possibleAnswers[0]);
                    optionTwo.setText(possibleAnswers[1]);
                    optionThree.setText(possibleAnswers[2]);
                    optionFour.setText(possibleAnswers[3]);
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Something went wrong:" + e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getBaseContext(), "Something went wrong:" + e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        });

        Retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncJsonObject asyncObject = new AsyncJsonObject();
                asyncObject.execute("");
            }
        });
    }



    private void Report_Question(final Context context, final int id_question_toReport) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Report Question");
        builder.setMessage("Do you think that question is wrong?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog pd = new ProgressDialog(QuizActivity.this);
                pd.setMessage("Reporting question.");
                pd.show();
                //Call Volley to report question
                StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL + id_question,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "Question Reported", Toast.LENGTH_LONG).show();

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Log.v("Error", "#" + error.toString());
                                Toast.makeText(getApplicationContext(), "Check Internet connection and retry.", Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(KEY_ID, String.valueOf(id_question_toReport));

                        return params;
                    }

                };

                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void Check_Question() {
        if (optionOne.isChecked() || optionTwo.isChecked() || optionThree.isChecked() || optionFour.isChecked()) {
            //Nothing to do
        } else {
            Toast.makeText(getBaseContext(), "Select atleast one answer.", Toast.LENGTH_LONG).show();
            return;
        }
//        Toast.makeText(getBaseContext(), "Radio Selected"+radiocheckid, Toast.LENGTH_LONG).show();

        int userSelection = radiocheckid;//getSelectedAnswer(radioSelected);

        if (firstQuestion != null) {

            int correctAnswerForQuestion = firstQuestion.getCorrectAnswer();

            if (userSelection == correctAnswerForQuestion) {       // Toast.makeText(this,"Answer is "+correctAnswerForQuestion,Toast.LENGTH_LONG).show();

                // correct answer
                //Toast.makeText(QuizActivity.this, "You got the answer correct", Toast.LENGTH_LONG).show();
                //Show_Next_Question();
                count_correctanswer++;
                userSelection_backgroudsetter(userSelection, true);

            } else {
                // failed question
                //True_Answer_dialog_display(firstQuestion.getCorrectAnswer());
                userSelection_backgroudsetter(userSelection, false);
                correctanswer_backgroundsetter(correctAnswerForQuestion);

            }
        }
    }

    public void userSelection_backgroudsetter(int userSelection, Boolean status) {
        //  Toast.makeText(this,"user selected"+userSelection,Toast.LENGTH_LONG).show();
        switch (userSelection) {
            case 1:
                if (status == true) {
                    optionOne.setBackgroundResource(R.drawable.greenstrip);
                    ObjSound = new sound(getApplicationContext());
                   // ObjSound.win(0.0);
                } else {
                    optionOne.setBackgroundResource(R.drawable.redstrip);
                    ObjSound = new sound(getApplicationContext());
                   // ObjSound.fail(0.0);
                }
                break;
            case 2:
                if (status == true) {
                    optionTwo.setBackgroundResource(R.drawable.greenstrip);
                    ObjSound = new sound(getApplicationContext());
                  //  ObjSound.win(0.0);
                } else {
                    optionTwo.setBackgroundResource(R.drawable.redstrip);
                    ObjSound = new sound(getApplicationContext());
                  //  ObjSound.fail(0.0);
                }
                break;
            case 3:

                if (status == true) {
                    optionThree.setBackgroundResource(R.drawable.greenstrip);
                    ObjSound = new sound(getApplicationContext());
                    //ObjSound.win(0.0);
                } else {
                    optionThree.setBackgroundResource(R.drawable.redstrip);
                    ObjSound = new sound(getApplicationContext());
                   // ObjSound.fail(0.0);
                }
                break;
            case 4:
                if (status == true) {
                    optionFour.setBackgroundResource(R.drawable.greenstrip);
                    ObjSound = new sound(getApplicationContext());
                   // ObjSound.win(0.0);
                } else {
                    optionFour.setBackgroundResource(R.drawable.redstrip);
                    ObjSound = new sound(getApplicationContext());
                   // ObjSound.fail(0.0);
                }

                break;
        }
    }

    public void correctanswer_backgroundsetter(int correctanswers) {
        switch (correctanswers) {
            case 1:
                optionOne.setBackgroundResource(R.drawable.greenstrip);
                break;
            case 2:
                optionTwo.setBackgroundResource(R.drawable.greenstrip);
                break;
            case 3:
                optionThree.setBackgroundResource(R.drawable.greenstrip);
                break;
            case 4:
                optionFour.setBackgroundResource(R.drawable.greenstrip);
                break;
        }
    }

    public void True_Answer_dialog_display(int CorrectAnswers) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.custom_dialog);

//Find Correct answer
        String[] possibleAnswers = firstQuestion.getAnswers().split("#");

        TextView txtview = (TextView) dialog.findViewById(R.id.text1);
        txtview.setText(possibleAnswers[CorrectAnswers - 1].toString());

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_Next_Question();
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();
        sendScreenImageName();
    }

    private void sendScreenImageName() {

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public void Show_Next_Question() {
        currentQuizQuestion++;
        if (currentQuizQuestion >= quizCount) {
            Button nextButton = (Button) findViewById(R.id.nextquiz);
            nextButton.setVisibility(View.GONE);
            Toast.makeText(QuizActivity.this, "End of the Quiz Questions", Toast.LENGTH_LONG).show();

            return;
        } else {
            //Set Score
            final TextView imgscore = (TextView) findViewById(R.id.TextViewscore);
            imgscore.setText(quizCount + " में से  " + count_correctanswer + " सही.");
            try {
                firstQuestion = parsedObject.get(currentQuizQuestion);
                id_question = firstQuestion.getId();
                quizQuestion.setText(firstQuestion.getQuestion());

                String[] possibleAnswers = firstQuestion.getAnswers().split("#");
                uncheckedRadioButton();
                optionOne.setText(possibleAnswers[0]);
                optionTwo.setText(possibleAnswers[1]);
                optionThree.setText(possibleAnswers[2]);
                optionFour.setText(possibleAnswers[3]);
            } catch (Exception e) {
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private class AsyncJsonObject extends AsyncTask<String, Void, String> {

        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            HttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httpPost = new HttpPost(EndPoints.APP_BASE_FOR_RSS_SPLASH + "/tnews/Android_Quiz/index.php?department=" + Number_OF_Quiz);
            String jsonResult = "";

            try {
                HttpResponse response = httpClient.execute(httpPost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return jsonResult;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = ProgressDialog.show(QuizActivity.this, "Downloading Quiz", "Wait....", true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            parsedObject = returnParsedJsonObject(result);
            if (parsedObject == null) {

                TextView txtview = (TextView) findViewById(R.id.quiz_question);
                txtview.setText("Quiz not loaded.Please check connections.");
                //Hide Linear Layout
                LinearLayout lnlayout = (LinearLayout) findViewById(R.id.linlayoutbtn);
                LinearLayout lnlayoutretry = (LinearLayout) findViewById(R.id.linlayoutbtnretry);
                lnlayout.setVisibility(View.GONE);
                lnlayoutretry.setVisibility(View.VISIBLE);
                return;
            } else {
                LinearLayout lnlayout = (LinearLayout) findViewById(R.id.linlayoutbtn);
                LinearLayout lnlayoutretry = (LinearLayout) findViewById(R.id.linlayoutbtnretry);
                lnlayout.setVisibility(View.VISIBLE);
                lnlayoutretry.setVisibility(View.GONE);
            }
            quizCount = parsedObject.size();
            firstQuestion = parsedObject.get(0);
            id_question = firstQuestion.getId();
            quizQuestion.setText(firstQuestion.getQuestion());
            String[] possibleAnswers = firstQuestion.getAnswers().split("#");
            optionOne.setText(possibleAnswers[0]);
            optionTwo.setText(possibleAnswers[1]);
            optionThree.setText(possibleAnswers[2]);
            optionFour.setText(possibleAnswers[3]);

            final TextView imgscore = (TextView) findViewById(R.id.TextViewscore);
            imgscore.setText("Total " + quizCount);
        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = br.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return answer;
        }
    }

    private List<QuizWrapper> returnParsedJsonObject(String result) {

        List<QuizWrapper> jsonObject = new ArrayList<QuizWrapper>();
        JSONObject resultObject = null;
        JSONArray jsonArray = null;
        QuizWrapper newItemObject = null;

        try {
            resultObject = new JSONObject(result);
            jsonArray = resultObject.optJSONArray("quiz_questions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonChildNode = null;
                try {
                    jsonChildNode = jsonArray.getJSONObject(i);
                    int id = jsonChildNode.getInt("id");
                    String question = jsonChildNode.getString("question");
                    String answerOptions = jsonChildNode.getString("possible_answers");
                    int correctAnswer = jsonChildNode.getInt("correct_answer");
                    newItemObject = new QuizWrapper(id, question, answerOptions, correctAnswer);
                    jsonObject.add(newItemObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonObject;
        }
        return null;
    }

    private int getSelectedAnswer(int radioSelected) {

        int answerSelected = 0;
        if (radioSelected == R.id.radio0) {
            answerSelected = 1;
        }
        if (radioSelected == R.id.radio1) {
            answerSelected = 2;
        }
        if (radioSelected == R.id.radio2) {
            answerSelected = 3;
        }
        if (radioSelected == R.id.radio3) {
            answerSelected = 4;
        }
        return answerSelected;
    }

    private void uncheckedRadioButton() {
        optionOne.setChecked(false);
        optionTwo.setChecked(false);
        optionThree.setChecked(false);
        optionFour.setChecked(false);


        optionOne.setTextColor(Color.BLACK);
        optionTwo.setTextColor(Color.BLACK);
        optionThree.setTextColor(Color.BLACK);
        optionFour.setTextColor(Color.BLACK);

        optionOne.setBackgroundColor(Color.TRANSPARENT);
        optionTwo.setBackgroundColor(Color.TRANSPARENT);
        optionThree.setBackgroundColor(Color.TRANSPARENT);
        optionFour.setBackgroundColor(Color.TRANSPARENT);


    }

    private void reset_RadioButton() {
        optionOne.setTextColor(Color.BLACK);
        optionTwo.setTextColor(Color.BLACK);
        optionThree.setTextColor(Color.BLACK);
        optionFour.setTextColor(Color.BLACK);

        optionOne.setBackgroundColor(Color.TRANSPARENT);
        optionTwo.setBackgroundColor(Color.TRANSPARENT);
        optionThree.setBackgroundColor(Color.TRANSPARENT);
        optionFour.setBackgroundColor(Color.TRANSPARENT);

    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);    }
}
