package com.materialdesign.oneway;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class PrologueActivity extends Activity {
    final int duration = 1000, numberOfHints = 11;
    static int currentLevel = 1;
    public static String PACKAGE_NAME;
    int hint = 10;
    double hintDelay = 5;
    boolean imageAppear = false, imageDisappear = true;
    Typeface arconFont, timeBurnerFont, timeBurnerBoldFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        PACKAGE_NAME = getPackageName();
        arconFont = Typeface.createFromAsset(getAssets(), "fonts/Arcon.ttf");
        timeBurnerFont = Typeface.createFromAsset(getAssets(), "fonts/TimeBurner.ttf");
        timeBurnerBoldFont = Typeface.createFromAsset(getAssets(), "fonts/TimeBurnerBold.ttf");

        startActivity(new Intent(getApplicationContext(), StartActivity.class));
        finish();

        setupBackgroundAnimation();
        showHint();
    }

    private void showHint() {
        if(hint < numberOfHints)
            new CountDownTimer((long) (hintDelay * 1000), (long) (hintDelay * 1000)) {
                @Override public void onTick(long l) {}
                @Override public void onFinish() {
                    showHint();
                }
            }.start();
        hideHint(new Runnable() {
            @Override
            public void run() {
                changeHint();
                findViewById(R.id.textHint).setVisibility(View.VISIBLE);
                ObjectAnimator a_1 = ObjectAnimator.ofFloat(findViewById(R.id.textHint), "alpha", 0, 1);
                ObjectAnimator a_2 = ObjectAnimator.ofFloat(findViewById(R.id.imageHint), "alpha", imageAppear ? findViewById(R.id.imageHint).getAlpha() : 0, imageAppear ? 1 : 0);
                ObjectAnimator y_1 = ObjectAnimator.ofFloat(findViewById(R.id.textHint), "translationY", 50, 0);
                ObjectAnimator sX_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageHint), "scaleX", imageAppear ? Math.max(0.5f, findViewById(R.id.imageHint).getScaleX()) : 0, imageAppear ? 1 : 0);
                ObjectAnimator sY_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageHint), "scaleY", imageAppear ? Math.max(0.5f, findViewById(R.id.imageHint).getScaleY()) : 0, imageAppear ? 1 : 0);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(a_1, a_2, y_1, sX_1, sY_1);
                set.setDuration(duration);
                set.start();
            }
        });
    }

    private void hideHint(final Runnable runnable) {
        ObjectAnimator a_1 = ObjectAnimator.ofFloat(findViewById(R.id.textHint), "alpha", findViewById(R.id.textHint).getAlpha(), 0);
        ObjectAnimator a_2 = ObjectAnimator.ofFloat(findViewById(R.id.imageHint), "alpha", findViewById(R.id.imageHint).getAlpha(), imageDisappear ? 0 : 1);
        ObjectAnimator y_1 = ObjectAnimator.ofFloat(findViewById(R.id.textHint), "translationY", findViewById(R.id.textHint).getTranslationY(), -50);
        ObjectAnimator sX_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageHint), "scaleX", findViewById(R.id.imageHint).getScaleX(), imageDisappear ? 0.5f : 1);
        ObjectAnimator sY_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageHint), "scaleY", findViewById(R.id.imageHint).getScaleY(), imageDisappear ? 0.5f : 1);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(a_1, a_2, y_1, sX_1, sY_1);
        set.setDuration(duration);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                findViewById(R.id.textHint).setVisibility(View.INVISIBLE);
                runnable.run();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void changeHint() {
        switch(hint) {
            case 1:
                ((TextView)findViewById(R.id.textHint)).setTypeface(timeBurnerBoldFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                hintDelay = 1;
                break;
            case 2:
                ((TextView)findViewById(R.id.textHint)).setTypeface(arconFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                hintDelay = 3;
                break;
            case 3:
                ((TextView)findViewById(R.id.textHint)).setTypeface(arconFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
                hintDelay = 3;
                break;
            case 4:
                ((TextView)findViewById(R.id.textHint)).setTypeface(arconFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                hintDelay = 5;
                break;
            case 5:
                ((TextView)findViewById(R.id.textHint)).setTypeface(arconFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                imageAppear = true;
                imageDisappear = false;
                findViewById(R.id.imageHint).setBackground(getResources().getDrawable(R.drawable.section_box));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) findViewById(R.id.imageHint).getLayoutParams();
                layoutParams.width = 800;
                layoutParams.height = 400;
                findViewById(R.id.imageHint).setLayoutParams(layoutParams);
                hintDelay = 5;
                break;
            case 6:
                ((TextView)findViewById(R.id.textHint)).setTypeface(arconFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                imageAppear = true;
                makeEnergy();
                hintDelay = 4;
                break;
            case 7:
                ((TextView)findViewById(R.id.textHint)).setTypeface(timeBurnerBoldFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                imageAppear = true;
                imageDisappear = true;
                scaleImage();
                hintDelay = 5;
                break;
            case 8:
                ((TextView)findViewById(R.id.textHint)).setTypeface(timeBurnerBoldFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                imageAppear = false;
                show(R.mipmap.triangle);
                hintDelay = 7;
                break;
            case 9:
                ((TextView)findViewById(R.id.textHint)).setTypeface(timeBurnerBoldFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                imageAppear = false;
                show(R.mipmap.triangle_empty);
                hintDelay = 7;
                break;
            case 10:
                ((TextView)findViewById(R.id.textHint)).setTypeface(timeBurnerBoldFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                imageAppear = false;
                show(R.mipmap.triangle_dot);
                hintDelay = 5;
                break;
            case 11:
                ((TextView)findViewById(R.id.textHint)).setTypeface(timeBurnerBoldFont);
                ((TextView)findViewById(R.id.textHint)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                imageAppear = false;
                show(0);
                hintDelay = 3;
                new CountDownTimer(3000, 3000) {
                    @Override public void onTick(long l) {}
                    @Override public void onFinish() {
                        startGame();
                    }
                }.start();
                break;
        }
        int id = getResources().getIdentifier("hint_" + hint, "string", PACKAGE_NAME);
        ((TextView)findViewById(R.id.textHint)).setText(getResources().getString(id));
        if(hint < numberOfHints) hint++;
    }

    private void startGame() {
        hideHint(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                finish();
            }
        });
    }

    private void show(final int id) {
        ObjectAnimator a_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "alpha", findViewById(R.id.imageLogo).getAlpha(), 0);
        ObjectAnimator tY_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "translationY", findViewById(R.id.imageLogo).getTranslationY(), -50);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(a_1, tY_1);
        set.setDuration(duration);
        set.setInterpolator(new AccelerateInterpolator());
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                ((ImageView) findViewById(R.id.imageLogo)).setImageResource(id);
                ObjectAnimator a_2 = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "alpha", 0, 1);
                ObjectAnimator tY_2 = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "translationY", 50, 0);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(a_2, tY_2);
                set.setDuration(duration);
                set.setInterpolator(new DecelerateInterpolator());
                set.start();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void scaleImage() {
        findViewById(R.id.Image).post(new Runnable() {
            @Override
            public void run() {
                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);
                ObjectAnimator sX_1 = ObjectAnimator.ofFloat(findViewById(R.id.Image), "scaleX", findViewById(R.id.Image).getScaleX(), (size.y / findViewById(R.id.Image).getHeight()) + 2);
                ObjectAnimator sY_1 = ObjectAnimator.ofFloat(findViewById(R.id.Image), "scaleY", findViewById(R.id.Image).getScaleY(), (size.y / findViewById(R.id.Image).getHeight()) + 2);
                ObjectAnimator y_1 = ObjectAnimator.ofFloat(findViewById(R.id.Image), "y", findViewById(R.id.Image).getY(), (size.y - findViewById(R.id.Image).getHeight()) / 2);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(sX_1, sY_1, y_1);
                set.setDuration(duration * 3);
                set.setInterpolator(new DecelerateInterpolator(4f));
                set.start();
            }
        });
    }

    private void makeEnergy() {
        findViewById(R.id.textHint).post(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 50; i++) {
                    int x1 = new Random().nextInt(findViewById(R.id.Image).getWidth());
                    int y1 = new Random().nextInt(findViewById(R.id.Image).getHeight());
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(10, 10), layoutParams2 = new RelativeLayout.LayoutParams(10, 10);
                    final ImageView particle1, particle2;
                    particle1 = new ImageView(getApplicationContext());
                    particle1.setBackground(getResources().getDrawable(R.drawable.particle));
                    particle1.setLayoutParams(layoutParams);
                    particle1.setX(x1);
                    particle1.setY(y1);
                    particle1.setAlpha(0f);
                    int x2 = new Random().nextInt(findViewById(R.id.Image).getWidth());
                    int y2 = new Random().nextInt(findViewById(R.id.Image).getHeight());
                    particle2 = new ImageView(getApplicationContext());
                    particle2.setBackground(getResources().getDrawable(R.drawable.anti_particle));
                    particle2.setLayoutParams(layoutParams2);
                    particle2.setX(x2);
                    particle2.setY(y2);
                    particle2.setAlpha(0f);
                    ((RelativeLayout) findViewById(R.id.Image)).addView(particle2);
                    ((RelativeLayout) findViewById(R.id.Image)).addView(particle1);
                    ObjectAnimator a_1 = ObjectAnimator.ofFloat(particle1, "alpha", 0, 1);
                    ObjectAnimator a_2 = ObjectAnimator.ofFloat(particle2, "alpha", 0, 1);
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(a_1, a_2);
                    set.setDuration(duration);
                    set.setStartDelay(i * duration);
                    set.start();
                    int centerX = (x1 + x2) / 2, centerY = (y1 + y2) / 2;
                    ObjectAnimator x_1 = ObjectAnimator.ofFloat(particle1, "x", x1, centerX);
                    ObjectAnimator x_2 = ObjectAnimator.ofFloat(particle2, "x", x2, centerX);
                    ObjectAnimator y_1 = ObjectAnimator.ofFloat(particle1, "y", y1, centerY);
                    ObjectAnimator y_2 = ObjectAnimator.ofFloat(particle2, "y", y2, centerY);
                    set = new AnimatorSet();
                    set.playTogether(x_1, x_2, y_1, y_2);
                    set.setDuration(duration);
                    set.setStartDelay(i * duration);
                    set.setInterpolator(new AccelerateInterpolator(4f));
                    set.start();
                    set.addListener(new Animator.AnimatorListener() {
                        @Override public void onAnimationStart(Animator animator) {}
                        @Override public void onAnimationEnd(Animator animator) {
                            ((RelativeLayout) findViewById(R.id.Image)).removeView(particle2);
                            ((RelativeLayout) findViewById(R.id.Image)).removeView(particle1);
                        }
                        @Override public void onAnimationCancel(Animator animator) {}
                        @Override public void onAnimationRepeat(Animator animator) {}
                    });
                }
            }
        });
    }

    private void setupBackgroundAnimation() {
        ObjectAnimator translationY_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "translationY", findViewById(R.id.imageBackground).getTranslationY(), new Random().nextInt(200) - 100);
        ObjectAnimator translationX_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "translationX", findViewById(R.id.imageBackground).getTranslationX(), new Random().nextInt(150) - 75);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(translationX_1, translationY_1);
        set.setDuration(duration * 20);
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
}
