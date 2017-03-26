package com.materialdesign.oneway;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

import tyrantgit.explosionfield.ExplosionField;

public class StartActivity extends Activity {
    static final int WIDTH = 7, HEIGHT = 7, duration = 1500;
    boolean animationRunning = false, clicked = false;
    int moves = 0;
    Pos[][] boardPoses = new Pos[WIDTH][HEIGHT];
    ImageView[][] mapImageView = new ImageView[WIDTH][HEIGHT];
    LevelObject currentLevel;
    Point firstObject;
    ExplosionField explosionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getBoard();
        getLevel();
        setBoard();
        setupBackgroundAnimation();
        explosionField = ExplosionField.attach2Window(this);
    }

    private void getBoard() {
        for(int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                mapImageView[i][j] = (ImageView) findViewById(getResources().getIdentifier("map" + i + j, "id", getPackageName()));
                final int finalI = i;
                final int finalJ = j;
                mapImageView[i][j].post(new Runnable() {
                    @Override
                    public void run() {
                        boardPoses[finalI][finalJ] = new Pos(mapImageView[finalI][finalJ].getX(), mapImageView[finalI][finalJ].getY());
                        ImageView tile = new ImageView(getApplicationContext());
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mapImageView[finalI][finalJ].getWidth(), mapImageView[finalI][finalJ].getHeight());
                        tile.setX(boardPoses[finalI][finalJ].x - mapImageView[0][0].getLeft());
                        tile.setY(boardPoses[finalI][finalJ].y - mapImageView[0][0].getTop());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            tile.setZ(-1);
                        }
                        tile.setImageResource(R.mipmap.tile);
                        tile.setLayoutParams(layoutParams);
                        ((RelativeLayout) findViewById(R.id.Board)).addView(tile);
                    }
                });
            }
        }
    }

    private void getLevel() {
        currentLevel = Levels.getLevel(MainActivity.currentLevel);
        rotateTriangles();
    }

    private void setBoard() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            setBoardByPos(i, j);
    }

    private void setBoardByPos(int x, int y) {
        if(boardPoses[x][y] != null) {
            mapImageView[x][y].setX(boardPoses[x][y].x);
            mapImageView[x][y].setY(boardPoses[x][y].y);
            mapImageView[x][y].setScaleX(0.75f);
            mapImageView[x][y].setScaleY(0.75f);
        }
        switch(currentLevel.getMap()[x][y]) {
            case 0:
                mapImageView[x][y].setImageResource(0);
                mapImageView[x][y].setRotation(0);
                break;
            case 1:
                mapImageView[x][y].setImageResource(R.mipmap.dark_hole);
                break;
            case 2:
                mapImageView[x][y].setImageResource(R.mipmap.triangle);
                break;
            case 3:
                mapImageView[x][y].setImageResource(R.mipmap.triangle_empty);
                break;
            case 4:
                mapImageView[x][y].setImageResource(R.mipmap.triangle_dot);
                break;
        }
    }

    private void setupBackgroundAnimation() {
        ObjectAnimator translationY_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "translationY", findViewById(R.id.imageBackground).getTranslationY(), new Random().nextInt(125) - 25);
        ObjectAnimator translationX_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "translationX", findViewById(R.id.imageBackground).getTranslationX(), new Random().nextInt(100) - 50);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(translationX_1, translationY_1);
        set.setDuration(duration * 40);
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

    public void clickMap(View view) {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(view.equals(mapImageView[i][j]) && currentLevel.getMap()[i][j] != 0) clickAtPos(i, j);
    }

    private void clickAtPos(int x, int y) {
        if(!animationRunning) {
            moves++;
            ObjectAnimator background = ObjectAnimator.ofObject(mapImageView[x][y], "backgroundColor", new ArgbEvaluator(), mapImageView[x][y].getSolidColor(), Color.parseColor("#6459214c"));
            background.setDuration(duration);
            background.start();
            if(!clicked && currentLevel.getMap()[x][y] != 1) {
                firstObject = new Point(x, y);
                clicked = true;
            } else {
                int typeFirst = currentLevel.getMap()[firstObject.x][firstObject.y];
                int typeSecond = currentLevel.getMap()[x][y];
                if((typeFirst == 2 && (typeSecond == 3 || typeSecond == 4)) || (typeSecond == 2 && (typeFirst == 3 || typeFirst == 4))) {
                    if (x == firstObject.x && Math.abs(y - firstObject.y) % 2 == 0) {
                        Point midPoint = new Point(x, (y + firstObject.y) / 2);
                        rotateTriangles(firstObject, new Point(x, y));
                        collapseTriangles(firstObject, new Point(x, y), midPoint);
                    } else if (y == firstObject.y && Math.abs(x - firstObject.x) % 2 == 0) {
                        Point midPoint = new Point((x + firstObject.x) / 2, y);
                        rotateTriangles(firstObject, new Point(x, y));
                        collapseTriangles(firstObject, new Point(x, y), midPoint);
                    }
                } else {
                    ObjectAnimator background_1 = ObjectAnimator.ofObject(mapImageView[firstObject.x][firstObject.y], "backgroundColor", new ArgbEvaluator(), mapImageView[x][y].getSolidColor(), Color.parseColor("#0059214c"));
                    ObjectAnimator background_2 = ObjectAnimator.ofObject(mapImageView[x][y], "backgroundColor", new ArgbEvaluator(), mapImageView[x][y].getSolidColor(), Color.parseColor("#0059214c"));
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(background_1, background_2);
                    set.setDuration(duration);
                    set.start();
                }
                clicked = false;
            }
        }
    }

    private void collapseTriangles(final Point first, final Point second, final Point midPoint) {
        ObjectAnimator xy_1, xy_2;
        if(first.x == second.x) {
            xy_1 = ObjectAnimator.ofFloat(mapImageView[first.x][first.y], "y", mapImageView[first.x][first.y].getY(), mapImageView[midPoint.x][midPoint.y].getY());
            xy_2 = ObjectAnimator.ofFloat(mapImageView[second.x][second.y], "y", mapImageView[second.x][second.y].getY(), mapImageView[midPoint.x][midPoint.y].getY());
        } else {
            xy_1 = ObjectAnimator.ofFloat(mapImageView[first.x][first.y], "x", mapImageView[first.x][first.y].getX(), mapImageView[midPoint.x][midPoint.y].getX());
            xy_2 = ObjectAnimator.ofFloat(mapImageView[second.x][second.y], "x", mapImageView[second.x][second.y].getX(), mapImageView[midPoint.x][midPoint.y].getX());
        }
        ObjectAnimator rotate_1 = ObjectAnimator.ofFloat(mapImageView[first.x][first.y], "rotation", mapImageView[first.x][first.y].getRotation(), getAngle(midPoint.x, midPoint.y));
        ObjectAnimator rotate_2 = ObjectAnimator.ofFloat(mapImageView[second.x][second.y], "rotation", mapImageView[second.x][second.y].getRotation(), getAngle(midPoint.x, midPoint.y));
        ObjectAnimator rotate_3 = ObjectAnimator.ofFloat(mapImageView[midPoint.x][midPoint.y], "rotation", mapImageView[midPoint.x][midPoint.y].getRotation(), getAngle(midPoint.x, midPoint.y));
        AnimatorSet set = new AnimatorSet();
        set.playTogether(xy_1, xy_2, rotate_1, rotate_2, rotate_3);
        set.setDuration(duration);
        set.setInterpolator(new AccelerateInterpolator(2f));
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                if(currentLevel.getMap()[first.x][first.y] != 4 && currentLevel.getMap()[second.x][second.y] != 4 && currentLevel.getMap()[midPoint.x][midPoint.y] == 0) {
                    currentLevel.getMap()[midPoint.x][midPoint.y] = 2;
                }
                currentLevel.getMap()[first.x][first.y] = 0;
                currentLevel.getMap()[second.x][second.y] = 0;
                setBoardByPos(midPoint.x, midPoint.y);
                setBoardByPos(first.x, first.y);
                setBoardByPos(second.x, second.y);
                makeFireworks(midPoint);
                if(lastTriangle())
                    collapseDarkHole();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void makeFireworks(Point point) { //TODO fireworks
    }

    private void collapseDarkHole() {
        ImageView lastTriangle = getLastTriangle();
        if(getLastTrianglePos().x == getDarkHolePos().x || getLastTrianglePos().y == getDarkHolePos().y) {
            ObjectAnimator x = ObjectAnimator.ofFloat(lastTriangle, "x", lastTriangle.getX(), getDarkHole().getX());
            ObjectAnimator y = ObjectAnimator.ofFloat(lastTriangle, "y", lastTriangle.getY(), getDarkHole().getY());
            ObjectAnimator alpha = ObjectAnimator.ofFloat(lastTriangle, "alpha", lastTriangle.getAlpha(), 0);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(x, y, alpha);
            set.setDuration(duration);
            set.setInterpolator(new AccelerateInterpolator(2f));
            set.start();
            set.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animator) {}
                @Override public void onAnimationEnd(Animator animator) {
                    Point lastTrianglePos = getLastTrianglePos();
                    currentLevel.getMap()[lastTrianglePos.x][lastTrianglePos.y] = 0;
                    setBoardByPos(lastTrianglePos.x, lastTrianglePos.y);
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(getDarkHole(), "scaleX", getDarkHole().getScaleX(), 4);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(getDarkHole(), "scaleY", getDarkHole().getScaleY(), 4);
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(scaleX, scaleY);
                    set.setDuration(duration * 5);
                    set.setInterpolator(new DecelerateInterpolator(4f));
                    set.start();
                    set.addListener(new Animator.AnimatorListener() {
                        @Override public void onAnimationStart(Animator animator) {}
                        @Override public void onAnimationEnd(Animator animator) {
                            ObjectAnimator scaleX = ObjectAnimator.ofFloat(getDarkHole(), "scaleX", getDarkHole().getScaleX(), 0);
                            ObjectAnimator scaleY = ObjectAnimator.ofFloat(getDarkHole(), "scaleY", getDarkHole().getScaleY(), 0);
                            AnimatorSet set = new AnimatorSet();
                            set.playTogether(scaleX, scaleY);
                            set.setDuration(duration * 5);
                            set.setInterpolator(new AccelerateInterpolator(4f));
                            set.start();
                            set.addListener(new Animator.AnimatorListener() {
                                @Override public void onAnimationStart(Animator animator) {}
                                @Override public void onAnimationEnd(Animator animator) {
                                    MainActivity.currentLevel++;
                                    getLevel();
                                    setBoard();
                                }
                                @Override public void onAnimationCancel(Animator animator) {}
                                @Override public void onAnimationRepeat(Animator animator) {}
                            });
                        }
                        @Override public void onAnimationCancel(Animator animator) {}
                        @Override public void onAnimationRepeat(Animator animator) {}
                    });
                }
                @Override public void onAnimationCancel(Animator animator) {}
                @Override public void onAnimationRepeat(Animator animator) {}
            });
        }
    }

    private ImageView getLastTriangle() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if (currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3) return mapImageView[i][j];
        return null;
    }

    private Point getLastTrianglePos() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if (currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3) return new Point(i, j);
        return null;
    }

    private ImageView getDarkHole() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 1) return mapImageView[i][j];
        return null;
    }

    private Point getDarkHolePos() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 1) return new Point(i, j);
        return null;
    }

    private boolean lastTriangle() {
        for(int i = 0, count = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++) {
            if (currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3) count++;
            if (count > 1) return false;
        }
        return true;
    }

    private void rotateTriangles(Point first, Point second) {
        if(first.x == second.x) {
            ImageView toUpTriangle = first.y > second.y ? mapImageView[first.x][first.y] : mapImageView[second.x][second.y];
            ImageView toDownTriangle = first.y > second.y ? mapImageView[second.x][second.y] : mapImageView[first.x][first.y];
            ObjectAnimator rotateDown = ObjectAnimator.ofFloat(toDownTriangle, "rotation", toDownTriangle.getRotation(), 180);
            ObjectAnimator rotateUp = ObjectAnimator.ofFloat(toUpTriangle, "rotation", toUpTriangle.getRotation(), 0);
            ObjectAnimator background_1 = ObjectAnimator.ofObject(mapImageView[first.x][first.y], "backgroundColor", new ArgbEvaluator(), mapImageView[first.x][first.y].getSolidColor(), Color.parseColor("#0059214c"));
            ObjectAnimator background_2 = ObjectAnimator.ofObject(mapImageView[second.x][second.y], "backgroundColor", new ArgbEvaluator(), mapImageView[first.x][first.y].getSolidColor(), Color.parseColor("#0059214c"));
            AnimatorSet set = new AnimatorSet();
            set.playTogether(rotateDown, rotateUp, background_1, background_2);
            set.setDuration(duration);
            set.setInterpolator(new DecelerateInterpolator(2f));
            set.start();
        } else {
            ImageView toRightTriangle = first.x < second.x ? mapImageView[first.x][first.y] : mapImageView[second.x][second.y];
            ImageView toLeftTriangle = first.x < second.x ? mapImageView[second.x][second.y] : mapImageView[first.x][first.y];
            ObjectAnimator rotateRight = ObjectAnimator.ofFloat(toRightTriangle, "rotation", toRightTriangle.getRotation(), 90);
            ObjectAnimator rotateLeft = ObjectAnimator.ofFloat(toLeftTriangle, "rotation", toLeftTriangle.getRotation(), -90);
            ObjectAnimator background_1 = ObjectAnimator.ofObject(mapImageView[first.x][first.y], "backgroundColor", new ArgbEvaluator(), mapImageView[first.x][first.y].getSolidColor(), Color.parseColor("#0059214c"));
            ObjectAnimator background_2 = ObjectAnimator.ofObject(mapImageView[second.x][second.y], "backgroundColor", new ArgbEvaluator(), mapImageView[first.x][first.y].getSolidColor(), Color.parseColor("#0059214c"));
            AnimatorSet set = new AnimatorSet();
            set.playTogether(rotateRight, rotateLeft, background_1, background_2);
            set.setDuration(duration);
            set.setInterpolator(new DecelerateInterpolator(2f));
            set.start();
        }
    }

    private void rotateTriangles() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3) {
                final int finalI = i;
                final int finalJ = j;
                mapImageView[i][j].post(new Runnable() {
                    @Override
                    public void run() {
                        float angle = getAngle(finalI, finalJ);
                        ObjectAnimator rotate = ObjectAnimator.ofFloat(mapImageView[finalI][finalJ], "rotation", mapImageView[finalI][finalJ].getRotation(), angle);
                        rotate.setDuration(duration * 10);
                        rotate.setInterpolator(new DecelerateInterpolator(2f));
                        rotate.start();
                    }
                });
            }
    }

    private float getAngle(int x, int y) {
        float angle = 0;
        float dx = getDarkHole().getX() - mapImageView[x][y].getX();
        float dy = getDarkHole().getY() - mapImageView[x][y].getY();
        try {
            if (mapImageView[x][y].getX() < getDarkHole().getX() && mapImageView[x][y].getY() < getDarkHole().getY())
                angle = (float) (Math.atan(dx / dy) * 180 / Math.PI) + 90;
            else if (mapImageView[x][y].getX() >= getDarkHole().getX() && mapImageView[x][y].getY() < getDarkHole().getY())
                angle = -(float) (Math.atan(dx / dy) * 180 / Math.PI) + 180;
            else angle = -(float) (Math.atan(dx / dy) * 180 / Math.PI);
        } catch (Exception ignored) {}
        return angle;
    }
}

















