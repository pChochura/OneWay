package com.materialdesign.oneway;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class LevelsActivity extends Activity {
    final static int duration = 1000;
    static int[] sections = new int[]{15, 3};
    String[] sectionNames = new String[]{"Just beginning", "Second round!"};
    static ArrayList<Integer> endedLevels = new ArrayList<>(), endedSections = new ArrayList<>();
    SectionsAdapter sectionsAdapter;
    RecyclerView sectionRecyclerView;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_levels);

        context = getApplicationContext();
        setRecyclerView();
        findViewById(R.id.imageBackground).setTranslationX(StartActivity.backgroundTranslationX);
        findViewById(R.id.imageBackground).setTranslationY(StartActivity.backgroundTranslationY);
        setupBackgroundAnimation();
        animateIn();
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
                if(getSection(position - 1) == 0 || endedSection(getSection(position - 1) - 1) != -1) {
                    PrologueActivity.currentLevel = position;
                    animateOut();
                } else showHint(R.string.not_ready);
            }
        });
    }

    private void showHint(int hint) {
        findViewById(R.id.HintBox).setTranslationX(StartActivity.size.x / 2);
        findViewById(R.id.HintBox).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textHint)).setText(getResources().getString(hint));
        ObjectAnimator translation_1 = ObjectAnimator.ofFloat(findViewById(R.id.HintBox), "translationX", StartActivity.size.x, 0);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(translation_1);
        set.setDuration(duration);
        set.setInterpolator(new AnticipateOvershootInterpolator());
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                ObjectAnimator translation_2 = ObjectAnimator.ofFloat(findViewById(R.id.HintBox), "translationX", 0, -findViewById(R.id.HintBox).getWidth() * 2);
                translation_2.setDuration(duration);
                translation_2.setStartDelay(duration);
                translation_2.setInterpolator(new AnticipateOvershootInterpolator());
                translation_2.start();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    private ArrayList<SectionObject> getSections() {
        ArrayList<LevelObject> levels = new ArrayList<>();
        ArrayList<SectionObject> sections = new ArrayList<>();
        for (int i = 0, index = 1; i < this.sections.length; i++) {
            for (int j = 0; j < this.sections[i]; j++, index++)
                levels.add(new LevelObject(index, Levels.getLevel(index - 1).getMoves(), Levels.getLevel(index - 1).getDifficulty()));
            sections.add(new SectionObject(sectionNames[i], levels));
            levels = new ArrayList<>();
        }
        return sections;
    }

    public static int endedSection(int currentLevel) {
        int section = getSection(currentLevel);
        for(int i = 0; i < sections[section]; i++)
            if(!endedLevels.contains(i + 1)) return -1;
        return section;
    }

    public static int getSection(int currentLevel) {
        int index = -1;
        for(int j = 0; j < sections.length; j++)
            for(int i = 0; i < sections[j]; i++) {
                index++;
                if (index == currentLevel) return j;
            }
        return -1;
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
        for(int i = 1; i <= maxLevels; i++) if(!endedLevels.contains(i)) return i;
        return 0;
    }

    @Override
    public void onBackPressed() {
        animateOut();
    }
}
