package com.materialdesign.oneway;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class AdActivity extends Activity {
    String theMazePackageName = "com.materialdesign.themaze";
    int timeLeft = 15;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ad);

        setTimer();
    }

    private void setTimer() {
        timeLeft = new Random().nextInt(15) + 15;
        ((TextView) findViewById(R.id.textTimer)).setText(String.valueOf(timeLeft));
        countDownTimer = new CountDownTimer(timeLeft * 1000, 1000) {
            @Override public void onTick(long l) {
                timeLeft--;
                ((TextView) findViewById(R.id.textTimer)).setText(String.valueOf(timeLeft));
            }
            @Override public void onFinish() {
                ((TextView) findViewById(R.id.textTimer)).setText(String.valueOf(timeLeft));
                StartActivity.availableHints += 2;
                finish();
            }
        }.start();
    }

    public void clickOpenTheMaze(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + theMazePackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + theMazePackageName)));
        }
    }

    @Override
    public void onBackPressed() {
        if(countDownTimer != null) countDownTimer.cancel();
        super.onBackPressed();
    }
}
