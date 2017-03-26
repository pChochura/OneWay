package com.materialdesign.oneway;

public class Levels {
    static public LevelObject getLevel(int level) {
        int moves = 0, difficulty = 0;
        int[][] map = new int[StartActivity.WIDTH][StartActivity.HEIGHT];
        switch(level) {
            case 1:
                map[3][3] = 1; //Dark hole
                map[0][6] = 2; //Triangle
                map[2][4] = 3; //Empty triangle
                map[4][5] = 3;
                map[4][6] = 3;
                moves = 3;
                difficulty = 1;
                break;
            case 2:
                map[3][1] = 1; //Dark hole
                map[5][2] = 2; //Triangle
                map[4][4] = 2;
                map[2][2] = 3; //Empty triangle
                map[3][2] = 4; //Dotted triangle
                moves = 2;
                difficulty = 1;
                break;
        }
        return new LevelObject(moves, difficulty, map);
    }
}
