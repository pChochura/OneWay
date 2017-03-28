package com.materialdesign.oneway;

import java.util.ArrayList;

public class SectionObject {
    private String sectionName;
    private ArrayList<LevelObject> levels;

    SectionObject(String sectionName, ArrayList<LevelObject> levels) {
        this.sectionName = sectionName;
        this.levels = levels;
    }

    public ArrayList<LevelObject> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<LevelObject> levels) {
        this.levels = levels;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
