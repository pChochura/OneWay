package com.materialdesign.oneway;

import android.graphics.Point;

import java.util.ArrayList;

public class Hints {
    static public ArrayList<Point> getHint(int level) {
        ArrayList<Point> clicks = new ArrayList<>();
        switch(level) {
            case 1:
                clicks.add(new Point(3, 2));
                clicks.add(new Point(5, 2));
                clicks.add(new Point(2, 2));
                clicks.add(new Point(4, 4));
                break;
            case 2:
                clicks.add(new Point(0, 6));
                clicks.add(new Point(4, 6));
                clicks.add(new Point(2, 6));
                clicks.add(new Point(2, 4));
                clicks.add(new Point(2, 5));
                clicks.add(new Point(4, 5));
                break;
            case 3:
                clicks.add(new Point(2, 6));
                clicks.add(new Point(4, 6));
                clicks.add(new Point(5, 2));
                clicks.add(new Point(5, 4));
                clicks.add(new Point(1, 2));
                clicks.add(new Point(3, 6));
                break;
            case 4:
                clicks.add(new Point(0, 6));
                clicks.add(new Point(6, 0));
                clicks.add(new Point(0, 4));
                clicks.add(new Point(4, 0));
                clicks.add(new Point(3, 3));
                clicks.add(new Point(5, 5));
                clicks.add(new Point(4, 4));
                clicks.add(new Point(4, 6));
                break;
            case 5:
                clicks.add(new Point(0, 6));
                clicks.add(new Point(2, 4));
                clicks.add(new Point(4, 2));
                clicks.add(new Point(6, 4));
                clicks.add(new Point(3, 1));
                clicks.add(new Point(5, 3));
                clicks.add(new Point(4, 2));
                clicks.add(new Point(2, 4));
                break;
        }
        return clicks;
    }
}
