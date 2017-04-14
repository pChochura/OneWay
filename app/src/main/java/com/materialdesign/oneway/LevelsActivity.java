package com.materialdesign.oneway;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import java.util.ArrayList;
import java.util.Random;

public class LevelsActivity extends Activity {
    final static int duration = 1000;
    static public int[] sections = new int[]{20, 15};
    String[] sectionNames = new String[]{"Just beginning", "Second round!"};
    static ArrayList<Integer> finishedLevels = new ArrayList<>();
    SectionsAdapter sectionsAdapter;
    RecyclerView sectionRecyclerView;
    static Context context;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_levels);

        sharedPreferences = getSharedPreferences("Levels", MODE_PRIVATE);
        context = getApplicationContext();
        getFinishedLevels();
        setRecyclerView();
        findViewById(R.id.imageBackground).setTranslationX(StartActivity.backgroundTranslationX);
        findViewById(R.id.imageBackground).setTranslationY(StartActivity.backgroundTranslationY);
        setupBackgroundAnimation();
        animateIn();
    }

    private void getFinishedLevels() {
        finishedLevels.clear();
        for (int i = 0; i < sharedPreferences.getInt("countOfItems", 0); i++)
            LevelsActivity.finishedLevels.add(sharedPreferences.getInt("level_" + i, 0));
    }

    private void saveFinishedLevels() {
        sharedPreferences.edit().putInt("countOfItems", LevelsActivity.finishedLevels.size()).apply();
        for (int i = 0; i < LevelsActivity.finishedLevels.size(); i++)
            sharedPreferences.edit().putInt("level_" + i, LevelsActivity.finishedLevels.get(i)).apply();
    }

    private void animateIn() {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(sectionRecyclerView, "translationX", -StartActivity.size.x, 0);
        translationX.setDuration(duration);
        translationX.setInterpolator(new AnticipateOvershootInterpolator());
        translationX.start();
    }

    private void animateOut() {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(sectionRecyclerView, "translationX", 0, -StartActivity.size.x);
        translationX.setDuration(duration);
        translationX.setInterpolator(new AnticipateOvershootInterpolator());
        translationX.start();
        translationX.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                StartActivity.backgroundTranslationX = findViewById(R.id.imageBackground).getTranslationX();
                StartActivity.backgroundTranslationY = findViewById(R.id.imageBackground).getTranslationY();
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void setRecyclerView() {
        sectionRecyclerView = (RecyclerView) findViewById(R.id.sectionsView);
        sectionRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        sectionsAdapter = new SectionsAdapter(getSections());
        sectionRecyclerView.setAdapter(sectionsAdapter);
        SectionsAdapter.setOnItemClickListener(new SectionsAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                TutorialActivity.chosenLevel = position;
                animateOut();
            }
        });
    }

    private ArrayList<SectionObject> getSections() {
        ArrayList<LevelObject> levels = new ArrayList<>();
        ArrayList<SectionObject> sections = new ArrayList<>();
        for (int i = 0, index = 1; i < LevelsActivity.sections.length; i++) {
            for (int j = 0; j < LevelsActivity.sections[i]; j++, index++)
                levels.add(new LevelObject(index, Levels.getLevel(index - 1).getMoves(), Levels.getLevel(index - 1).getDifficulty()));
            sections.add(new SectionObject(sectionNames[i], levels));
            levels = new ArrayList<>();
        }
        return sections;
    }

    private void setupBackgroundAnimation() {
        ObjectAnimator translationY_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "translationY", findViewById(R.id.imageBackground).getTranslationY(), new Random().nextInt(200) - 100);
        ObjectAnimator translationX_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "translationX", findViewById(R.id.imageBackground).getTranslationX(), new Random().nextInt(150) - 75);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(translationX_1, translationY_1);
        set.setDuration(duration * 30);
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

    public static int getFirstUndoneLevel() {
        int maxLevels = 0;
        for(int section : sections) maxLevels += section;
        for(int i = 1; i <= maxLevels; i++) if(!finishedLevels.contains(i)) return i;
        return 0;
    }

    @Override
    public void onBackPressed() {
        saveFinishedLevels();
        animateOut();
    }

    @Override
    public void onPause() {
        saveFinishedLevels();
        super.onPause();
    }

    @Override
    protected void onStop() {
        saveFinishedLevels();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        saveFinishedLevels();
        super.onDestroy();
    }
}
