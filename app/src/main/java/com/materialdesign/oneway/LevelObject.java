package com.materialdesign.oneway;

public class LevelObject {
    private int index, moves, difficulty;
    private int[][] map;

    LevelObject(int index, int moves, int difficulty) {
        this.index = index;
        this.moves = moves;
        this.difficulty = difficulty;
    }

    LevelObject(int index, int moves, int difficulty, int[][] map) {
        this.index = index;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
