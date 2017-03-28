package com.materialdesign.oneway;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.logging.Level;

public class LevelsActivity extends Activity {
    int[] sections = new int[]{5, 15};
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
}
