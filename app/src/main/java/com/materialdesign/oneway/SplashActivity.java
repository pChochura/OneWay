package com.materialdesign.oneway;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(getSharedPreferences("Tutorial", MODE_PRIVATE).getBoolean("tutorialEnded", false)) {
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        } else {
            startActivity(new Intent(getApplicationContext(), TutorialActivity.class));
            finish();
        }
    }
}
