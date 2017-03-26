package com.materialdesign.oneway;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.Random;

public class MainActivity extends Activity {
    final int duration = 1500;
    static int currentLevel = 1;
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PACKAGE_NAME = getPackageName();
        setupAnimations();
    }

    private void setupAnimations() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(findViewById(R.id.StartContainer), "rotation", 0, 360);
        rotation.setRepeatMode(ValueAnimator.RESTART);
        rotation.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator rotation_2 = ObjectAnimator.ofFloat(findViewById(R.id.imageDarkHole), "rotation", 0, -360);
        rotation_2.setRepeatMode(ValueAnimator.RESTART);
        rotation_2.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(findViewById(R.id.imageDarkHole), "scaleX", 1, 1.1f, 1);
        scaleX.setRepeatMode(ValueAnimator.RESTART);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(findViewById(R.id.imageDarkHole), "scaleY", 1, 1.1f, 1);
        scaleY.setRepeatMode(ValueAnimator.RESTART);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(rotation, rotation_2, scaleX, scaleY);
        set.setDuration(duration * 15);
        set.start();
        setupBackgroundAnimation();
    }

    private void setupBackgroundAnimation() {
        ObjectAnimator translationY_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "translationY", findViewById(R.id.imageBackground).getTranslationY(), new Random().nextInt(125) - 25);
        ObjectAnimator translationX_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "translationX", findViewById(R.id.imageBackground).getTranslationX(), new Random().nextInt(100) - 50);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(translationX_1, translationY_1);
        set.setDuration(duration * 40);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                setupBackgroundAnimation();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    public void clickStart(View view) {
        ObjectAnimator x_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageTriangle1), "x", findViewById(R.id.imageTriangle1).getX(), findViewById(R.id.imageDarkHole).getX());
        ObjectAnimator x_2 = ObjectAnimator.ofFloat(findViewById(R.id.imageTriangle2), "x", findViewById(R.id.imageTriangle2).getX(), findViewById(R.id.imageDarkHole).getX());
        ObjectAnimator alpha_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageTriangle1), "alpha", findViewById(R.id.imageTriangle1).getAlpha(), 0);
        ObjectAnimator alpha_2 = ObjectAnimator.ofFloat(findViewById(R.id.imageTriangle2), "alpha", findViewById(R.id.imageTriangle2).getAlpha(), 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alpha_1, x_1, alpha_2, x_2);
        set.setDuration(duration);
        set.setInterpolator(new AccelerateInterpolator(4f));
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                finish();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }
}
