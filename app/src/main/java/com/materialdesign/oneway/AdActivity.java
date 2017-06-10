package com.materialdesign.oneway;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class AdActivity extends Activity {
    String[] packageNames = new String[]{"com.materialdesign.hexagon", "com.materialdesign.themaze"};
    int timeLeft = 15, adType = 0, adIndex = 0;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ad);

        adType = new Random().nextInt(2);
        setBackground();
        setTimer();
    }

    private void setBackground() {
        if(adIndex < 3) adIndex++;
        ((ImageView) findViewById(R.id.imageBackground)).setImageResource(getResources().getIdentifier((adType == 0 ? "ad_hexagon_" : "ad_the_maze_") + adIndex, "mipmap", getPackageName()));
    }

    private void setTimer() {
        ((TextView) findViewById(R.id.textTimer)).setText(String.valueOf(timeLeft));
        countDownTimer = new CountDownTimer(timeLeft * 1000, 1000) {
            @Override public void onTick(long l) {
                timeLeft--;
                if(timeLeft % 5 == 0)
                    setBackground();
                ((TextView) findViewById(R.id.textTimer)).setText(String.valueOf(timeLeft));
            }
            @Override public void onFinish() {
                ((TextView) findViewById(R.id.textTimer)).setText("X");
            }
        }.start();
    }

    public void clickOpenApp(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageNames[adType])));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageNames[adType])));
        }
    }

    public void clickClose(View view) {
        if(timeLeft == 1) {
            StartActivity.availableHints += 2;
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(countDownTimer != null) countDownTimer.cancel();
        super.onBackPressed();
    }
}
