package com.materialdesign.oneway;

public class Levels {
    static public LevelObject getLevel(int level) {
        int moves = 0, difficulty = 0;
        int[][] map = new int[StartActivity.WIDTH][StartActivity.HEIGHT];
        switch(level) {
            case 0:
                map[2][1] = 1; //Dark hole
                map[0][2] = 2; //Triangle
                map[0][0] = 3; //Empty triangle
                moves = 1;
                difficulty = 0;
                break;
            case -1:
                map[4][1] = 1; //Dark hole
                map[0][2] = 2; //Triangle
                map[0][1] = 3; //Empty triangle
                map[4][0] = 3;
                moves = 2;
                difficulty = 0;
                break;
            case -2:
                map[4][2] = 1; //Dark hole
                map[0][1] = 2; //Triangle
                map[0][2] = 2;
                map[2][1] = 3; //Empty triangle
                map[3][1] = 4; //Dotted triangle
                moves = 2;
                difficulty = 0;
                break;
            case -3:
                map[4][1] = 1; //Dark hole
                map[3][0] = 2; //Triangle
                map[0][0] = 3; //Empty triangle
                map[1][0] = 3;
                moves = 2;
                difficulty = 0;
                break;
            case -4:
                map[4][1] = 1; //Dark hole
                map[0][1] = 3; //Empty triangle
                map[2][1] = 5; //Bright hole
                moves = 1;
                difficulty = 0;
                break;
            case -5:
                map[4][1] = 1; //Dark hole
                map[4][0] = 2; //Triangle
                map[0][1] = 4; //Dotted triangle
                map[0][2] = 5; //Bright hole
                moves = 2;
                difficulty = 0;
                break;
            case -6:
                map[4][1] = 1; //Dark hole
                map[2][1] = 2; //Triangle
                map[4][2] = 2;
                map[0][2] = 5; //Bright hole
                moves = 1;
                difficulty = 0;
                break;
            case 1:
                map[3][1] = 1; //Dark hole
                map[5][2] = 2; //Triangle
                map[4][4] = 2;
                map[2][2] = 3; //Empty triangle
                map[3][2] = 4; //Dotted triangle
                moves = 2;
                difficulty = 1;
                break;
            case 2:
                map[3][3] = 1; //Dark hole
                map[0][6] = 2; //Triangle
                map[2][4] = 3; //Empty triangle
                map[4][5] = 3;
                map[4][6] = 3;
                moves = 3;
                difficulty = 1;
                break;
            case 3:
                map[3][4] = 1; //Dark hole
                map[5][4] = 2; //Triangle
                map[2][6] = 2;
                map[1][2] = 3; //Empty triangle
                map[4][6] = 3;
                map[5][2] = 4; //Dotted triangle
                moves = 3;
                difficulty = 2;
                break;
            case 4:
                map[0][0] = 1; //Dark hole
                map[4][0] = 2; //Triangle
                map[0][6] = 2;
                map[0][4] = 3; //Empty triangle
                map[5][5] = 3;
                map[6][0] = 3;
                map[4][6] = 4; //Dotted triangle
                moves = 4;
                difficulty = 2;
                break;
            case 5:
                map[3][0] = 1; //Dark hole
                map[6][0] = 2; //Triangle
                map[1][2] = 2;
                map[2][0] = 3; //Empty triangle
                map[5][0] = 3;
                map[4][2] = 3;
                map[6][3] = 3;
                map[1][5] = 4; //Dotted triangle
                moves = 5;
                difficulty = 2;
                break;
            case 6:
                map[0][6] = 1; //Dark hole
                map[4][2] = 2; //Triangle
                map[0][4] = 2;
                map[1][5] = 2;
                map[2][6] = 2;
                map[2][0] = 3; //Empty triangle
                map[2][4] = 3;
                map[6][4] = 3;
                map[3][0] = 4; //Dotted triangle
                map[5][1] = 4;
                map[6][3] = 4;
                moves = 6;
                difficulty = 2;
                break;
            case 7:
                map[3][1] = 1; //Dark hole
                map[1][1] = 2; //Triangle
                map[5][1] = 2;
                map[3][3] = 3; //Empty triangle
                map[5][3] = 3;
                map[3][6] = 3;
                map[4][0] = 4; //Dotted triangle
                moves = 4;
                difficulty = 3;
                break;
            case 8:
                map[0][3] = 1; //Dark hole
                map[2][3] = 2; //Triangle
                map[4][3] = 2;
                map[6][3] = 2;
                map[3][0] = 3; //Empty triangle
                map[0][1] = 3;
                map[4][1] = 3;
                map[1][4] = 3;
                map[2][5] = 3;
                map[5][6] = 3;
                map[6][1] = 4; //Dotted triangle
                map[6][5] = 4;
                moves = 8;
                difficulty = 3;
                break;
            case 9:
                map[3][6] = 1; //Dark hole
                map[0][0] = 2; //Triangle
                map[6][0] = 2;
                map[1][2] = 3; //Empty triangle
                map[2][2] = 3;
                map[4][2] = 3;
                map[2][3] = 3;
                map[4][3] = 3;
                map[6][6] = 3;
                map[2][6] = 4; //Dotted triangle
                moves = 7;
                difficulty = 3;
                break;
            case 10:
                map[4][4] = 1; //Dark hole
                map[0][0] = 2; //Triangle
                map[3][3] = 2;
                map[3][1] = 3; //Empty triangle
                map[1][2] = 3;
                map[4][2] = 3;
                map[1][5] = 3;
                map[2][6] = 3;
                map[6][6] = 4; //Dotted triangle
                moves = 6;
                difficulty = 3;
                break;
            case 11:
                map[3][5] = 1; //Dark hole
                map[2][0] = 2; //Triangle
                map[3][4] = 2;
                map[0][6] = 2;
                map[3][0] = 3; //Empty triangle
                map[1][2] = 3;
                map[5][2] = 3;
                map[6][3] = 3;
                map[6][5] = 3;
                map[2][6] = 3;
                map[0][0] = 4; //Dotted triangle
                map[4][6] = 4;
                moves = 8;
                difficulty = 3;
                break;
            case 12:
                map[0][6] = 1; //Dark hole
                map[6][0] = 2; //Triangle
                map[2][0] = 3; //Empty triangle
                map[0][1] = 3;
                map[1][2] = 3;
                map[6][4] = 3;
                map[2][5] = 3;
                map[3][6] = 3;
                map[5][6] = 3;
                moves = 7;
                difficulty = 3;
                break;
            case 13:
                map[3][0] = 1; //Dark hole
                map[0][0] = 2; //Triangle
                map[3][5] = 2;
                map[3][6] = 2;
                map[4][0] = 3; //Empty triangle
                map[2][1] = 3;
                map[3][2] = 3;
                map[4][2] = 3;
                map[1][3] = 3;
                map[2][3] = 3;
                map[5][4] = 3;
                map[0][5] = 3;
                map[2][6] = 3;
                map[4][6] = 3;
                map[0][2] = 4; //Dotted triangle
                map[6][4] = 4;
                moves = 12;
                difficulty = 4;
                break;
            case 14:
                map[6][3] = 1; //Dark hole
                map[2][0] = 2; //Triangle
                map[3][3] = 2;
                map[4][0] = 3; //Empty triangle
                map[1][1] = 3;
                map[2][1] = 3;
                map[5][1] = 3;
                map[0][2] = 3;
                map[4][2] = 3;
                map[0][3] = 3;
                map[1][4] = 3;
                map[5][3] = 3;
                map[4][4] = 3;
                map[5][5] = 3;
                map[2][6] = 3;
                map[3][6] = 3;
                map[6][1] = 3;
                map[5][0] = 3;
                map[6][6] = 3;
                map[1][5] = 4; //Dotted triangle
                moves = 17;
                difficulty = 5;
                break;
            case 15:
                map[5][1] = 1; //Dark hole
                map[0][0] = 2; //Triangle
                map[6][0] = 2;
                map[6][6] = 2;
                map[1][0] = 3; //Empty triangle
                map[4][1] = 3;
                map[2][2] = 3;
                map[5][2] = 3;
                map[2][3] = 3;
                map[5][3] = 3;
                map[6][3] = 3;
                map[0][4] = 3;
                map[2][4] = 3;
                map[4][4] = 3;
                map[2][5] = 3;
                map[3][5] = 3;
                map[0][6] = 3;
                map[2][6] = 3;
                map[3][0] = 4; //Dotted triangle
                map[1][3] = 4;
                moves = 16;
                difficulty = 5;
                break;
            case 16:
                map[1][6] = 1; //Dark hole
                map[0][6] = 2; //Triangle
                map[2][6] = 2;
                map[5][6] = 2;
                map[6][6] = 2;
                map[0][0] = 3; //Empty triangle
                map[2][0] = 3;
                map[4][0] = 3;
                map[1][1] = 3;
                map[5][1] = 3;
                map[6][1] = 3;
                map[0][2] = 3;
                map[1][2] = 3;
                map[3][4] = 3;
                map[4][4] = 3;
                map[3][5] = 3;
                map[6][5] = 3;
                map[4][6] = 3;
                map[3][1] = 4; //Dotted triangle
                map[1][3] = 4;
                map[5][4] = 4;
                moves = 16;
                difficulty = 5;
                break;
            case 17:
                map[5][3] = 1; //Dark hole
                map[6][2] = 2; //Triangle
                map[2][3] = 2;
                map[6][4] = 2;
                map[1][0] = 3; //Empty triangle
                map[6][0] = 3;
                map[0][1] = 3;
                map[5][1] = 3;
                map[6][1] = 3;
                map[4][2] = 3;
                map[0][3] = 3;
                map[6][3] = 3;
                map[1][4] = 3;
                map[5][4] = 3;
                map[0][5] = 3;
                map[4][5] = 3;
                map[6][6] = 3;
                map[1][1] = 4; //Dotted triangle
                map[2][1] = 4;
                moves = 15;
                difficulty = 5;
                break;
            case 18:
                map[0][0] = 1; //Dark hole
                map[1][0] = 2; //Triangle
                map[0][2] = 2;
                map[6][6] = 2;
                map[5][0] = 3; //Empty triangle
                map[6][0] = 3;
                map[1][1] = 3;
                map[3][1] = 3;
                map[6][1] = 3;
                map[5][2] = 3;
                map[0][3] = 3;
                map[1][3] = 3;
                map[0][4] = 3;
                map[0][6] = 3;
                map[5][4] = 3;
                map[0][5] = 3;
                map[1][5] = 3;
                map[1][2] = 3;
                map[5][5] = 3;
                map[3][6] = 3;
                map[6][3] = 4; //Dotted triangle
                map[1][6] = 4;
                moves = 18;
                difficulty = 4;
                break;
            case 19:
                map[0][3] = 1; //Dark hole
                map[0][0] = 2; //Triangle
                map[6][0] = 2;
                map[6][6] = 2;
                map[2][0] = 3; //Empty triangle
                map[5][0] = 3;
                map[0][1] = 3;
                map[3][1] = 3;
                map[6][1] = 3;
                map[2][2] = 3;
                map[6][2] = 3;
                map[5][3] = 3;
                map[0][5] = 3;
                map[1][5] = 3;
                map[3][5] = 3;
                map[6][5] = 3;
                map[1][6] = 3;
                map[5][1] = 4; //Dotted triangle
                map[2][3] = 4;
                moves = 15;
                difficulty = 4;
                break;
            case 20:
                map[1][1] = 1; //Dark hole
                map[0][0] = 2; //Triangle
                map[2][0] = 2;
                map[6][6] = 2;
                map[3][0] = 3; //Empty triangle
                map[6][0] = 3;
                map[0][1] = 3;
                map[2][1] = 3;
                map[5][1] = 3;
                map[6][1] = 3;
                map[4][2] = 3;
                map[1][3] = 3;
                map[0][4] = 3;
                map[4][4] = 3;
                map[6][4] = 3;
                map[1][5] = 3;
                map[1][6] = 3;
                map[3][4] = 4; //Dotted triangle
                map[2][6] = 4;
                moves = 15;
                difficulty = 4;
                break;
            case 21:
                map[1][1] = 1; //Dark hole
                map[4][2] = 2; //Triangle
                map[3][1] = 3; //Empty triangle
                map[6][4] = 3;
                map[0][6] = 4; //Dotted triangle
                map[2][4] = 5; //Bright hole
                moves = 4;
                difficulty = 2;
                break;
            case 22:
                map[3][0] = 1; //Dark hole
                map[0][0] = 2; //Triangle
                map[6][0] = 2;
                map[2][0] = 3; //Empty triangle
                map[4][0] = 3;
                map[1][1] = 3;
                map[2][1] = 3;
                map[6][1] = 3;
                map[1][2] = 3;
                map[4][2] = 3;
                map[0][4] = 3;
                map[3][5] = 3;
                map[5][5] = 3;
                map[6][5] = 3;
                map[0][6] = 3;
                map[3][6] = 3;
                map[5][6] = 3;
                map[1][3] = 4; //Dotted triangle
                map[2][4] = 4;
                map[3][1] = 5; //Bright hole
                moves = 16;
                difficulty = 4;
                break;
            case 23:
                map[5][1] = 1; //Dark hole
                map[0][0] = 2; //Triangle
                map[6][0] = 2;
                map[0][1] = 3; //Empty triangle
                map[0][3] = 3;
                map[0][5] = 3;
                map[1][6] = 3;
                map[2][0] = 3;
                map[2][2] = 3;
                map[2][3] = 3;
                map[3][1] = 3;
                map[3][5] = 3;
                map[4][0] = 3;
                map[4][6] = 3;
                map[5][0] = 3;
                map[5][5] = 3;
                map[3][6] = 4; //Dotted triangle
                map[5][3] = 5; //Bright hole
                moves = 15;
                difficulty = 4;
                break;
            case 24:
                map[1][3] = 1; //Dark hole
                map[6][2] = 2; //Triangle
                map[6][3] = 2;
                map[0][4] = 3; //Empty triangle
                map[0][5] = 3;
                map[1][1] = 3;
                map[1][6] = 3;
                map[2][0] = 3;
                map[2][1] = 3;
                map[3][4] = 3;
                map[4][3] = 3;
                map[5][1] = 3;
                map[6][0] = 3;
                map[6][4] = 3;
                map[6][5] = 3;
                map[2][6] = 3;
                map[3][1] = 4; //Dotted triangle
                map[3][5] = 4;
                map[0][0] = 5; //Bright hole
                moves = 15;
                difficulty = 4;
                break;
            case 25:
                map[3][0] = 1; //Dark hole
                map[0][6] = 2; //Triangle
                map[6][6] = 2;
                map[0][0] = 3; //Empty triangle
                map[0][5] = 3;
                map[1][1] = 3;
                map[2][1] = 3;
                map[3][1] = 3;
                map[4][0] = 3;
                map[4][5] = 3;
                map[5][2] = 3;
                map[5][3] = 3;
                map[5][6] = 3;
                map[6][0] = 3;
                map[6][2] = 3;
                map[6][4] = 3;
                map[0][1] = 4; //Dotted triangle
                map[2][0] = 4;
                map[2][2] = 4;
                map[3][3] = 5; //Bright hole
                map[3][4] = 5;
                moves = 17;
                difficulty = 4;
                break;
            case 26:
                map[3][6] = 1; //Dark hole
                map[3][0] = 2; //Triangle
                map[6][3] = 2;
                map[0][0] = 3; //Empty triangle
                map[1][2] = 3;
                map[2][2] = 3;
                map[2][4] = 3;
                map[3][2] = 3;
                map[3][3] = 3;
                map[3][4] = 3;
                map[3][5] = 3;
                map[4][5] = 3;
                map[5][0] = 3;
                map[5][5] = 3;
                map[6][1] = 3;
                map[6][5] = 3;
                map[6][6] = 3;
                map[2][6] = 4; //Dotted triangle
                map[5][6] = 4;
                map[0][6] = 5; //Bright hole
                moves = 16;
                difficulty = 4;
                break;
            case 27:
                map[3][6] = 1; //Dark hole
                map[0][6] = 2; //Triangle
                map[6][0] = 2;
                map[0][0] = 3; //Empty triangle
                map[0][1] = 3;
                map[0][5] = 3;
                map[2][0] = 3;
                map[2][2] = 3;
                map[2][3] = 3;
                map[2][6] = 3;
                map[3][0] = 3;
                map[5][0] = 3;
                map[5][1] = 3;
                map[5][2] = 3;
                map[5][6] = 3;
                map[6][5] = 3;
                map[6][2] = 4; //Dotted triangle
                map[3][3] = 5; //Bright hole
                map[6][6] = 5;
                moves = 15;
                difficulty = 4;
                break;
            case 28:
                map[3][1] = 1; //Dark hole
                map[2][2] = 2; //Triangle
                map[1][2] = 3; //Empty triangle
                map[1][5] = 3;
                map[1][6] = 3;
                map[3][0] = 3;
                map[4][2] = 3;
                map[4][6] = 3;
                map[5][6] = 3;
                map[6][0] = 3;
                map[6][1] = 3;
                map[6][3] = 3;
                map[1][1] = 4; //Dotted triangle
                map[5][3] = 4;
                map[0][0] = 5; //Bright hole
                map[3][6] = 5;
                moves = 13;
                difficulty = 4;
                break;
            case 29:
                map[3][3] = 1; //Dark hole
                map[0][3] = 2; //Triangle
                map[6][0] = 2;
                map[0][6] = 3; //Empty triangle
                map[1][2] = 3;
                map[2][0] = 3;
                map[2][5] = 3;
                map[3][1] = 3;
                map[3][5] = 3;
                map[4][0] = 3;
                map[4][6] = 3;
                map[5][2] = 3;
                map[5][3] = 3;
                map[5][6] = 3;
                map[6][4] = 3;
                map[6][1] = 4; //Dotted triangle
                map[0][0] = 5; //Bright hole
                map[3][6] = 5;
                moves = 14;
                difficulty = 4;
                break;
            case 30:
                map[3][6] = 1; //Dark hole
                map[5][4] = 2; //Triangle
                map[6][6] = 2;
                map[0][0] = 3; //Empty triangle
                map[0][1] = 3;
                map[1][0] = 3;
                map[1][2] = 3;
                map[1][6] = 3;
                map[2][1] = 3;
                map[2][4] = 3;
                map[3][5] = 3;
                map[4][2] = 3;
                map[4][6] = 3;
                map[5][0] = 3;
                map[5][1] = 3;
                map[5][2] = 3;
                map[6][1] = 3;
                map[2][5] = 4; //Dotted triangle
                map[6][0] = 5; //Bright hole
                map[6][4] = 5;
                moves = 16;
                difficulty = 4;
                break;
        }
        return new LevelObject(level, moves, difficulty, map);
    }
}
