package com.ambaitsystem.tapri.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ambaitsystem.vgecchat.R;

public class App_update extends Activity implements View.OnClickListener {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.app_update);

        Button btnupdate = (Button) findViewById(R.id.btnupdate);
        btnupdate.setOnClickListener(this);

        Button btnskip_tomainactivity = (Button) findViewById(R.id.btnskip_tomainactivity);
        btnskip_tomainactivity.setOnClickListener(this);

    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btnupdate:
                ///Call Play Store for Update
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                }
                catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case R.id.btnskip_tomainactivity:
                this.finish();
                Intent i=new Intent(getBaseContext(),Request_Status.class);
                i.putExtra("skip","1");
                startActivity(i);
                finish();
                break;
        }

    }
}
