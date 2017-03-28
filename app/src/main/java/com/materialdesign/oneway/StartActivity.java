package com.materialdesign.oneway;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class StartActivity extends Activity {
    static final int WIDTH = 7, HEIGHT = 7, duration = 1000;
    boolean animationRunning = false, clicked = false;
    int moves = 0;
    ImageView[][] mapImageView = new ImageView[WIDTH][HEIGHT];
    ImageView[][] tilesImageView = new ImageView[WIDTH][HEIGHT];
    LevelObject currentLevel;
    Point firstObject, size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        getBoard();
        getLevel();
        setBoard();
        setupBackgroundAnimation();
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
                        ImageView tile = new ImageView(getApplicationContext());
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mapImageView[finalI][finalJ].getWidth(), mapImageView[finalI][finalJ].getHeight());
                        tile.setX(mapImageView[finalI][finalJ].getX() - mapImageView[0][0].getLeft());
                        tile.setY(mapImageView[finalI][finalJ].getY() - mapImageView[0][0].getTop());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            tile.setTranslationZ(-1);
                            tile.setBackground(getResources().getDrawable(R.drawable.tile));
                        }
                        tile.setLayoutParams(layoutParams);
                        ((RelativeLayout) findViewById(R.id.Board)).addView(tile);
                        tilesImageView[finalI][finalJ] = tile;
                    }
                });
            }
        }
    }

    private void getLevel() {
        moves = 0;
        currentLevel = Levels.getLevel(MainActivity.currentLevel);
        setMoves();
        setLevel();
        rotateTriangles();
    }

    private void setBoard() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            setBoardByPos(i, j, true);
    }

    private void setBoardByPos(int x, int y, boolean hide) {
        if(tilesImageView[x][y] != null) {
            mapImageView[x][y].setX(tilesImageView[x][y].getX());
            mapImageView[x][y].setY(tilesImageView[x][y].getY());
            mapImageView[x][y].setScaleX(hide ? 0 : 0.6f);
            mapImageView[x][y].setScaleY(hide ? 0 : 0.6f);
            tilesImageView[x][y].setRotation(0);
            mapImageView[x][y].setAlpha(1f);
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
            case 5:
                mapImageView[x][y].setImageResource(R.mipmap.bright_hole);
                break;
        }
    }

    private void showBoard() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            showBoardByPos(i, j);
    }

    private void showBoardByPos(int x, int y) {
        ObjectAnimator animation_1, animation_2, animation_3;
        animation_1 = ObjectAnimator.ofFloat(mapImageView[x][y], "scaleX", 0, 0.6f);
        animation_2 = ObjectAnimator.ofFloat(mapImageView[x][y], "scaleY", 0, 0.6f);
        animation_3 = ObjectAnimator.ofFloat(tilesImageView[x][y], "alpha", tilesImageView[x][y].getAlpha(), 1f);
        animation_3.setDuration(duration);
        animation_3.start();
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animation_1, animation_2);
        set.setDuration(duration);
        set.setStartDelay(currentLevel.getMap()[x][y] != 1 ? duration : 0);
        set.setInterpolator(new AnticipateOvershootInterpolator());
        set.start();
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

    public void clickMap(View view) {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(view.equals(mapImageView[i][j]) && currentLevel.getMap()[i][j] != 0) clickAtPos(i, j);
    }

    public void clickBack(View view) {
        ObjectAnimator x_1 = ObjectAnimator.ofFloat(findViewById(R.id.Board), "x", findViewById(R.id.Board).getX(), size.x);
        ObjectAnimator x_2 = ObjectAnimator.ofFloat(findViewById(R.id.InformationContainer), "x", findViewById(R.id.InformationContainer).getX(), size.x);
        ObjectAnimator x_3 = ObjectAnimator.ofFloat(findViewById(R.id.BottomBar), "x", findViewById(R.id.BottomBar).getX(), size.x);
        x_1.setStartDelay(new Random().nextInt(duration));
        x_2.setStartDelay(new Random().nextInt(duration));
        x_3.setStartDelay(new Random().nextInt(duration));
        AnimatorSet set = new AnimatorSet();
        set.playTogether(x_1, x_2, x_3);
        set.setDuration(duration);
        set.setInterpolator(new AnticipateOvershootInterpolator());
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                showAllLevels();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void showAllLevels() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void clickRestart(View view) {
        if(!animationRunning) {
            animationRunning = true;
            ObjectAnimator rotation = ObjectAnimator.ofFloat(findViewById(R.id.imageRestart), "rotation", 0, 360);
            rotation.setDuration(duration);
            rotation.setInterpolator(new AccelerateDecelerateInterpolator());
            rotation.start();
            collapseAllTriangles(new Runnable() {
                @Override
                public void run() {
                    getLevel();
                    setBoard();
                    showBoard();
                    setMoves();
                    animationRunning = false;
                }
            });
        }
    }

    private void setMoves() {
        ((TextView) findViewById(R.id.textMoves)).setText(getResources().getString(R.string.moves) + " " + (currentLevel.getMoves() - moves));
    }

    private void setLevel() {
        ((TextView) findViewById(R.id.textLevel)).setText(getResources().getString(R.string.level) + " " + MainActivity.currentLevel);
    }

    private void clickAtPos(final int x, final int y) {
        if(!animationRunning) {
            GradientDrawable shape = (GradientDrawable) ((LayerDrawable) tilesImageView[x][y].getBackground()).findDrawableByLayerId(R.id.card);
            animateColorShape(shape, getResources().getColor(R.color.colorAccentBright), getResources().getColor(R.color.colorAccent));
            if(!clicked && currentLevel.getMap()[x][y] != 1) {
                firstObject = new Point(x, y);
                clicked = true;
            } else {
                int typeFirst = currentLevel.getMap()[firstObject.x][firstObject.y];
                int typeSecond = currentLevel.getMap()[x][y];
                if((typeFirst == 2 && (typeSecond == 3 || typeSecond == 4)) || (typeSecond == 2 && (typeFirst == 3 || typeFirst == 4))) {
                    Point midPoint = null;
                    if (x == firstObject.x && Math.abs(y - firstObject.y) % 2 == 0)
                        midPoint = new Point(x, (y + firstObject.y) / 2);
                    else if (y == firstObject.y && Math.abs(x - firstObject.x) % 2 == 0)
                        midPoint = new Point((x + firstObject.x) / 2, y);
                    else if(Math.abs(firstObject.x - x) == Math.abs(firstObject.y - y) && Math.abs(firstObject.y - y) % 2 == 0)
                        midPoint = new Point((x + firstObject.x) / 2, (y + firstObject.y) / 2);
                    else if(Math.abs(firstObject.x - x) % 2 == 0 && Math.abs(firstObject.y - y) % 2 == 0)
                        midPoint = new Point((x + firstObject.x) / 2, (y + firstObject.y) / 2);
                    else showHint(R.string.hint);

                    if(midPoint != null && currentLevel.getMap()[midPoint.x][midPoint.y] == 0) {
                        rotateTriangles(firstObject, new Point(x, y));
                        collapseTriangles(firstObject, new Point(x, y), midPoint);
                    } else showHint(R.string.hint);
                } else if(typeSecond == 5 && (x == firstObject.x || y == firstObject.y || Math.abs(firstObject.x - x) == Math.abs(firstObject.y - y))) {
                    rotateTriangle(firstObject, new Point(x, y));
                    collapseBrightHole(firstObject, new Point(x, y));
                } else showHint(R.string.hint);
                unMarkTile(firstObject.x, firstObject.y);
                unMarkTile(x, y);
                clicked = false;
            }
        }
    }

    private void showHint(int hint) {
        findViewById(R.id.HintBox).setTranslationX(size.x / 2);
        findViewById(R.id.HintBox).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textHint)).setText(getResources().getString(hint));
        ObjectAnimator translation_1 = ObjectAnimator.ofFloat(findViewById(R.id.HintBox), "translationX", size.x, 0);
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

    private void animateColorShape(final GradientDrawable shape, int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(duration);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animator) {
                shape.setColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    private void collapseTriangles(final Point first, final Point second, final Point midPoint) {
        moves++;
        setMoves();
        animationRunning = true;
        ObjectAnimator x_1 = ObjectAnimator.ofFloat(mapImageView[first.x][first.y], "x", mapImageView[first.x][first.y].getX(), mapImageView[midPoint.x][midPoint.y].getX());
        ObjectAnimator y_1 = ObjectAnimator.ofFloat(mapImageView[first.x][first.y], "y", mapImageView[first.x][first.y].getY(), mapImageView[midPoint.x][midPoint.y].getY());
        ObjectAnimator x_2 = ObjectAnimator.ofFloat(mapImageView[second.x][second.y], "x", mapImageView[second.x][second.y].getX(), mapImageView[midPoint.x][midPoint.y].getX());
        ObjectAnimator y_2 = ObjectAnimator.ofFloat(mapImageView[second.x][second.y], "y", mapImageView[second.x][second.y].getY(), mapImageView[midPoint.x][midPoint.y].getY());
        ObjectAnimator rotate_1 = ObjectAnimator.ofFloat(mapImageView[first.x][first.y], "rotation", mapImageView[first.x][first.y].getRotation(), getAngle(midPoint.x, midPoint.y, getDarkHole()));
        ObjectAnimator rotate_2 = ObjectAnimator.ofFloat(mapImageView[second.x][second.y], "rotation", mapImageView[second.x][second.y].getRotation(), getAngle(midPoint.x, midPoint.y, getDarkHole()));
        ObjectAnimator rotate_3 = ObjectAnimator.ofFloat(mapImageView[midPoint.x][midPoint.y], "rotation", mapImageView[midPoint.x][midPoint.y].getRotation(), getAngle(midPoint.x, midPoint.y,getDarkHole()));
        AnimatorSet set = new AnimatorSet();
        set.playTogether(x_1, x_2, y_1, y_2, rotate_1, rotate_2, rotate_3);
        set.setDuration(duration);
        set.setInterpolator(new AccelerateInterpolator(2f));
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                if(currentLevel.getMap()[first.x][first.y] == 4 || currentLevel.getMap()[second.x][second.y] == 4) {
                    currentLevel.getMap()[midPoint.x][midPoint.y] = 2;
                    setBoardByPos(midPoint.x, midPoint.y, false);
                    ObjectAnimator alpha = ObjectAnimator.ofFloat(mapImageView[midPoint.x][midPoint.y], "alpha", 1, 0);
                    alpha.setDuration(duration);
                    alpha.start();
                    alpha.addListener(new Animator.AnimatorListener() {
                        @Override public void onAnimationStart(Animator animator) {}
                        @Override public void onAnimationEnd(Animator animator) {
                            currentLevel.getMap()[midPoint.x][midPoint.y] = 0;
                            setBoardByPos(midPoint.x, midPoint.y, false);
                            animationRunning = false;
                            if (lastTriangle())
                                collapseDarkHole();
                        }
                        @Override public void onAnimationCancel(Animator animator) {}
                        @Override public void onAnimationRepeat(Animator animator) {}
                    });
                } else {
                    currentLevel.getMap()[midPoint.x][midPoint.y] = 2;
                    setBoardByPos(midPoint.x, midPoint.y, false);
                    animationRunning = false;
                }
                currentLevel.getMap()[first.x][first.y] = 0;
                currentLevel.getMap()[second.x][second.y] = 0;
                setBoardByPos(first.x, first.y, false);
                setBoardByPos(second.x, second.y, false);
                if(lastTriangle())
                    collapseDarkHole();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void collapseDarkHole() {
        ImageView lastTriangle = getLastTriangle();
        if(getLastTrianglePos().x == getDarkHolePos().x || getLastTrianglePos().y == getDarkHolePos().y ||
                Math.abs(getLastTrianglePos().x - getDarkHolePos().x) == Math.abs(getLastTrianglePos().y - getDarkHolePos().y)) {
            animationRunning = true;
            collapseTriangle(lastTriangle, new Runnable() {
                @Override
                public void run() {
                    Point lastTrianglePos = getLastTrianglePos();
                    currentLevel.getMap()[lastTrianglePos.x][lastTrianglePos.y] = 0;
                    setBoardByPos(lastTrianglePos.x, lastTrianglePos.y, false);
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(getDarkHole(), "scaleX", getDarkHole().getScaleX(), 4);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(getDarkHole(), "scaleY", getDarkHole().getScaleY(), 4);
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(scaleX, scaleY);
                    set.setDuration(duration);
                    set.setInterpolator(new DecelerateInterpolator(4f));
                    set.start();
                    collapseAllTiles();
                    set.addListener(new Animator.AnimatorListener() {
                        @Override public void onAnimationStart(Animator animator) {}
                        @Override public void onAnimationEnd(Animator animator) {
                            ObjectAnimator scaleX = ObjectAnimator.ofFloat(getDarkHole(), "scaleX", getDarkHole().getScaleX(), 0);
                            ObjectAnimator scaleY = ObjectAnimator.ofFloat(getDarkHole(), "scaleY", getDarkHole().getScaleY(), 0);
                            AnimatorSet set = new AnimatorSet();
                            set.playTogether(scaleX, scaleY);
                            set.setDuration(duration * 3);
                            set.setInterpolator(new AccelerateInterpolator(4f));
                            set.start();
                            set.addListener(new Animator.AnimatorListener() {
                                @Override public void onAnimationStart(Animator animator) {}
                                @Override public void onAnimationEnd(Animator animator) {
                                    MainActivity.currentLevel++;
                                    getLevel();
                                    setBoard();
                                    showBoard();
                                    animationRunning = false;
                                }
                                @Override public void onAnimationCancel(Animator animator) {}
                                @Override public void onAnimationRepeat(Animator animator) {}
                            });
                        }
                        @Override public void onAnimationCancel(Animator animator) {}
                        @Override public void onAnimationRepeat(Animator animator) {}
                    });
                }
            });
        }
    }

    private void collapseBrightHole(final Point firstObject, final Point hole) {
        ObjectAnimator x = ObjectAnimator.ofFloat(mapImageView[firstObject.x][firstObject.y], "x", mapImageView[firstObject.x][firstObject.y].getX(), mapImageView[hole.x][hole.y].getX());
        ObjectAnimator y = ObjectAnimator.ofFloat(mapImageView[firstObject.x][firstObject.y], "y", mapImageView[firstObject.x][firstObject.y].getY(), mapImageView[hole.x][hole.y].getY());
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mapImageView[firstObject.x][firstObject.y], "alpha", mapImageView[firstObject.x][firstObject.y].getAlpha(), 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(x, y, alpha);
        set.setDuration(duration);
        set.setInterpolator(new AccelerateInterpolator());
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                ObjectAnimator scaleX_1 = ObjectAnimator.ofFloat(mapImageView[firstObject.x][firstObject.y], "scaleX", mapImageView[firstObject.x][firstObject.y].getScaleX(), 0);
                ObjectAnimator scaleY_1 = ObjectAnimator.ofFloat(mapImageView[firstObject.x][firstObject.y], "scaleY", mapImageView[firstObject.x][firstObject.y].getScaleY(), 0);
                ObjectAnimator scaleX_2 = ObjectAnimator.ofFloat(mapImageView[hole.x][hole.y], "scaleX", mapImageView[hole.x][hole.y].getScaleX(), 0);
                ObjectAnimator scaleY_2 = ObjectAnimator.ofFloat(mapImageView[hole.x][hole.y], "scaleY", mapImageView[hole.x][hole.y].getScaleY(), 0);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(scaleX_1, scaleY_1, scaleX_2, scaleY_2);
                set.setDuration(duration);
                set.start();
                set.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animator) {}
                    @Override public void onAnimationEnd(Animator animator) {
                        switch(currentLevel.getMap()[firstObject.x][firstObject.y]) {
                            case 2:
                                currentLevel.getMap()[hole.x][hole.y] = 0;
                                break;
                            case 3:
                                currentLevel.getMap()[hole.x][hole.y] = 2;
                                break;
                            case 4:
                                currentLevel.getMap()[hole.x][hole.y] = 3;
                                break;
                        }
                        currentLevel.getMap()[firstObject.x][firstObject.y] = 0;
                        setBoardByPos(firstObject.x, firstObject.y, false);
                        setBoardByPos(hole.x, hole.y, true);
                        showBoardByPos(hole.x, hole.y);
                        rotateTriangles();
                    }
                    @Override public void onAnimationCancel(Animator animator) {}
                    @Override public void onAnimationRepeat(Animator animator) {}
                });
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void collapseAllTriangles(final Runnable runnable) {
        final int triangles = getCountOf(2) + getCountOf(3) + getCountOf(4) + getCountOf(5);
        for(int i = 0, count = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3 || currentLevel.getMap()[i][j] == 4) {
                count++;
                collapseTriangle(mapImageView[i][j], triangles != count ? null : new Runnable() {
                    @Override
                    public void run() {
                        if(runnable != null) runnable.run();
                    }
                });
            } else if(currentLevel.getMap()[i][j] == 5) {
                count++;
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(mapImageView[i][j], "scaleX", 0.6f, 0);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(mapImageView[i][j], "scaleY", 0.6f, 0);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(scaleX, scaleY);
                set.setDuration(duration);
                set.start();
                set.setInterpolator(new AnticipateOvershootInterpolator());
                final int finalCount = count;
                set.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animator) {}
                    @Override public void onAnimationEnd(Animator animator) {
                        if(triangles == finalCount && runnable != null) runnable.run();
                    }
                    @Override public void onAnimationCancel(Animator animator) {}
                    @Override public void onAnimationRepeat(Animator animator) {}
                });
            }
    }

    private void collapseTriangle(ImageView triangle, final Runnable runnable) {
        ObjectAnimator x = ObjectAnimator.ofFloat(triangle, "x", triangle.getX(), getDarkHole().getX());
        ObjectAnimator y = ObjectAnimator.ofFloat(triangle, "y", triangle.getY(), getDarkHole().getY());
        ObjectAnimator alpha = ObjectAnimator.ofFloat(triangle, "alpha", triangle.getAlpha(), 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(x, y, alpha);
        set.setDuration(duration);
        set.setInterpolator(new AccelerateInterpolator(2f));
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                if(runnable != null) runnable.run();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void rotateTriangles(Point first, Point second) {
        ImageView triangle_1, triangle_2;
        if(first.x == second.x) {
            triangle_1 = first.y > second.y ? mapImageView[first.x][first.y] : mapImageView[second.x][second.y];
            triangle_2 = first.y > second.y ? mapImageView[second.x][second.y] : mapImageView[first.x][first.y];
        } else {
            triangle_1 = first.x < second.x ? mapImageView[first.x][first.y] : mapImageView[second.x][second.y];
            triangle_2 = first.x < second.x ? mapImageView[second.x][second.y] : mapImageView[first.x][first.y];
        }
        ObjectAnimator rotate_1 = ObjectAnimator.ofFloat(triangle_1, "rotation", triangle_1.getRotation(), first.x == second.x ? 180 : 90);
        ObjectAnimator rotate_2 = ObjectAnimator.ofFloat(triangle_2, "rotation", triangle_2.getRotation(), first.x == second.x ? 0 : -90);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(rotate_1, rotate_2);
        set.setDuration(duration);
        set.setInterpolator(new DecelerateInterpolator(2f));
        set.start();
    }

    private void rotateTriangles() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3 || currentLevel.getMap()[i][j] == 4) {
                final int finalI = i;
                final int finalJ = j;
                mapImageView[i][j].post(new Runnable() {
                    @Override
                    public void run() {
                        float angle = getAngle(finalI, finalJ, getDarkHole());
                        ObjectAnimator rotate = ObjectAnimator.ofFloat(mapImageView[finalI][finalJ], "rotation", mapImageView[finalI][finalJ].getRotation(), angle);
                        rotate.setDuration(duration * 10);
                        rotate.setInterpolator(new DecelerateInterpolator(2f));
                        rotate.start();
                    }
                });
            }
    }

    private void rotateTriangle(Point firstObject, Point point) {
        float angle = getAngle(firstObject.x, firstObject.y, mapImageView[point.x][point.y]);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(mapImageView[firstObject.x][firstObject.y], "rotation", mapImageView[firstObject.x][firstObject.y].getRotation(), angle);
        rotate.setDuration(duration);
        rotate.setInterpolator(new DecelerateInterpolator(2f));
        rotate.start();
    }

    private void collapseAllTiles() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++) {
            float angle = new Random().nextFloat() * 360;
            ObjectAnimator x_1 = ObjectAnimator.ofFloat(tilesImageView[i][j], "x", tilesImageView[i][j].getX(), getDarkHole().getX());
            ObjectAnimator y_1 = ObjectAnimator.ofFloat(tilesImageView[i][j], "y", tilesImageView[i][j].getY(), getDarkHole().getY());
            ObjectAnimator rotation_1 = ObjectAnimator.ofFloat(tilesImageView[i][j], "rotation", tilesImageView[i][j].getRotation(), angle);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(x_1, y_1, rotation_1);
            set.setDuration(duration * 3);
            set.setStartDelay(new Random().nextInt(duration / 2));
            set.setInterpolator(new AccelerateInterpolator(4f));
            set.start();
            final int finalI = i;
            final int finalJ = j;
            set.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animator) {}
                @Override public void onAnimationEnd(Animator animator) {
                    tilesImageView[finalI][finalJ].setAlpha(0f);
                    tilesImageView[finalI][finalJ].setX(mapImageView[finalI][finalJ].getX());
                    tilesImageView[finalI][finalJ].setY(mapImageView[finalI][finalJ].getY());
                }
                @Override public void onAnimationCancel(Animator animator) {}
                @Override public void onAnimationRepeat(Animator animator) {}
            });
        }
    }

    private void unMarkTile(int x, int y) {
        GradientDrawable shape = ((GradientDrawable)((LayerDrawable) tilesImageView[x][y].getBackground()).findDrawableByLayerId(R.id.card));
        animateColorShape(shape, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorAccentBright));
    }

    private int getCountOf(int value) {
        int count = 0;
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == value) count++;
        return count;
    }

    private ImageView getLastTriangle() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if (currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3 || currentLevel.getMap()[i][j] == 4) return mapImageView[i][j];
        return null;
    }

    private Point getLastTrianglePos() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if (currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3 || currentLevel.getMap()[i][j] == 4) return new Point(i, j);
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

    private float getAngle(int x, int y, ImageView pivot) {
        float angle = -90;
        float dx = pivot.getX() - mapImageView[x][y].getX();
        float dy = pivot.getY() - mapImageView[x][y].getY();
        if(dy != 0) angle = -(float) (Math.atan(dx / dy) * 180 / Math.PI);
        if(mapImageView[x][y].getY() < pivot.getY() || (mapImageView[x][y].getY() == pivot.getY() && mapImageView[x][y].getX() < pivot.getX()))
            angle -= 180;
        if(angle > 180)
            angle = 360 - angle;
        else if(angle < -180)
            angle = angle + 360;
        return angle;
    }
}
