package com.materialdesign.oneway;

public class LevelObject {
    private int moves, difficulty;
    private int[][] map;

    LevelObject() {}

    LevelObject(int moves, int difficulty, int[][] map) {
        this.moves = moves;
        this.difficulty = difficulty;
        this.map = map;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }
}
