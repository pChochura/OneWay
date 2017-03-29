package com.materialdesign.oneway;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnticipateOvershootInterpolator;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

public class LevelsActivity extends Activity {
    final int duration = 1000;
    int[] sections = new int[]{10, 3};
    String[] sectionNames = new String[]{"Just beginning", "Second round!"};
    SectionsAdapter sectionsAdapter;
    RecyclerView sectionRecyclerView;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        context = getApplicationContext();
        setRecyclerView();
        findViewById(R.id.imageBackground).setTranslationX(StartActivity.backgroundTranslationX);
        findViewById(R.id.imageBackground).setTranslationY(StartActivity.backgroundTranslationY);
        setupBackgroundAnimation();
        animateIn();
    }

    private void animateIn() {
        sectionRecyclerView.setTranslationX(-StartActivity.size.x);
        ObjectAnimator translationX = ObjectAnimator.ofFloat(sectionRecyclerView, "translationX", sectionRecyclerView.getTranslationX(), 0);
        translationX.setDuration(duration);
        translationX.setInterpolator(new AnticipateOvershootInterpolator());
        translationX.start();
    }

    private void setRecyclerView() {
        sectionRecyclerView = (RecyclerView) findViewById(R.id.sectionsView);
        sectionRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        sectionsAdapter = new SectionsAdapter(getSections());
        sectionRecyclerView.setAdapter(sectionsAdapter);
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
}
