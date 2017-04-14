package com.materialdesign.oneway;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class TutorialActivity extends Activity {
    static int WIDTH = 5, HEIGHT = 3, duration = 1000;
    boolean animationRunning = false, clicked = false;
    int tileSize = 50;
    ImageView[][] mapImageView = new ImageView[WIDTH][HEIGHT];
    ImageView[][] tilesImageView = new ImageView[WIDTH][HEIGHT];
    static LevelObject currentLevel;
    static Point firstObject, size;
    SharedPreferences sharedPreferences;
    static int chosenLevel = 0;
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_tutorial);

        PACKAGE_NAME = getPackageName();
        ((TextView) findViewById(R.id.textHint)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Arcon.ttf"));
        sharedPreferences = getSharedPreferences("Tutorial", MODE_PRIVATE);

        animateIn();
        getBoard();
        getLevel();
        setBoard();
        setSizeOfTiles();
        setupBackgroundAnimation();
    }

    private void animateIn() {
        ObjectAnimator a_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "alpha", 0, 1);
        ObjectAnimator a_2 = ObjectAnimator.ofFloat(findViewById(R.id.Board), "alpha", 0, 1);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(a_1, a_2);
        set.setDuration(duration);
        set.start();
    }

    public void setSizeOfTiles() {
        size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        tileSize = (size.x - 100) / StartActivity.WIDTH;
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mapImageView[i][j].getLayoutParams();
            layoutParams.height = tileSize;
            layoutParams.width = tileSize;
            mapImageView[i][j].setLayoutParams(layoutParams);
        }
    }

    public void setupBackgroundAnimation() {
        ObjectAnimator translationY_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "translationY", findViewById(R.id.imageBackground).getTranslationY(), new Random().nextInt(200) - 100);
        ObjectAnimator translationX_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "translationX", findViewById(R.id.imageBackground).getTranslationX(), new Random().nextInt(80) - 40);
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

    public void getBoard() {
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
                        tile.setBackground(getResources().getDrawable(R.drawable.tile));
                        tile.setLayoutParams(layoutParams);
                        ((RelativeLayout) findViewById(R.id.Board)).addView(tile);
                        tilesImageView[finalI][finalJ] = tile;
                        tilesImageView[finalI][finalJ].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(currentLevel.getMap()[finalI][finalJ] > 1)
                                    clickAtPos(finalI, finalJ);
                            }
                        });
                        sendViewToBack(tile);
                    }
                });
            }
        }
    }

    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    public void getLevel() {
        currentLevel = Levels.getLevel(chosenLevel);
        showHint(getResources().getIdentifier("hint_" + (-chosenLevel), "string", PACKAGE_NAME));
        rotateTriangles();
    }

    public void setBoard() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            setBoardByPos(i, j, true);
    }

    public void setBoardByPos(int x, int y, boolean hide) {
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

    public void showBoard() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            showBoardByPos(i, j);
    }

    public void showBoardByPos(int x, int y) {
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

    public void showBoardByPos(int x, int y, final Runnable runnable) {
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
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                runnable.run();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    public void clickAtPos(final int x, final int y) {
        if(!animationRunning) {
            animationRunning = true;
            GradientDrawable shape = (GradientDrawable) ((LayerDrawable) tilesImageView[x][y].getBackground()).findDrawableByLayerId(R.id.card);
            animateColorShape(shape, getResources().getColor(R.color.colorAccentBright), getResources().getColor(R.color.colorAccent));
            if(!clicked) {
                firstObject = new Point(x, y);
                clicked = true;
                animationRunning = false;
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

                    if(midPoint != null && currentLevel.getMap()[midPoint.x][midPoint.y] == 0) {
                        rotateTriangles(firstObject, new Point(x, y));
                        collapseTriangles(firstObject, new Point(x, y), midPoint);
                    } else animationRunning = false;
                } else if(typeSecond == 5 && (x == firstObject.x || y == firstObject.y || Math.abs(firstObject.x - x) == Math.abs(firstObject.y - y))) {
                    rotateTriangle(firstObject, new Point(x, y));
                    collapseBrightHole(firstObject, new Point(x, y));
                } else if(!firstObject.equals(new Point(x, y)))
                    animationRunning = false;
                else animationRunning = false;
                unMarkTile(firstObject.x, firstObject.y);
                unMarkTile(x, y);
                clicked = false;
            }
        }
    }

    public void showHint(final int hint) {
        ObjectAnimator a = ObjectAnimator.ofFloat(findViewById(R.id.textHint), "alpha", findViewById(R.id.textHint).getAlpha(), 0);
        ObjectAnimator tY = ObjectAnimator.ofFloat(findViewById(R.id.textHint), "translationY", findViewById(R.id.textHint).getTranslationY(), 50);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(a, tY);
        set.setDuration(duration);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                ((TextView) findViewById(R.id.textHint)).setText(getResources().getString(hint));
                ObjectAnimator a = ObjectAnimator.ofFloat(findViewById(R.id.textHint), "alpha", findViewById(R.id.textHint).getAlpha(), 1);
                ObjectAnimator tY = ObjectAnimator.ofFloat(findViewById(R.id.textHint), "translationY", -50, 0);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(a, tY);
                set.setDuration(duration);
                set.setInterpolator(new DecelerateInterpolator());
                set.start();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    public void animateColorShape(final GradientDrawable shape, int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(duration);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animator) {
                shape.setColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    public void collapseTriangles(final Point first, final Point second, final Point midPoint) {
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
                            if (lastTriangle())
                                collapseDarkHole();
                        }
                        @Override public void onAnimationCancel(Animator animator) {}
                        @Override public void onAnimationRepeat(Animator animator) {}
                    });
                } else {
                    currentLevel.getMap()[midPoint.x][midPoint.y] = 2;
                    setBoardByPos(midPoint.x, midPoint.y, false);
                }
                animationRunning = false;
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

    public void collapseDarkHole() {
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
                                    chosenLevel--;
                                    if(chosenLevel != -7) {
                                        getLevel();
                                        setBoard();
                                        showBoard();
                                        animationRunning = false;
                                    } else {
                                        showHint(R.string.hint_7);
                                        new CountDownTimer(5000, 5000) {
                                            @Override public void onTick(long l) {}
                                            @Override public void onFinish() {
                                                startGame();
                                            }
                                        }.start();
                                    }
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
        } else {
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
                    chosenLevel--;
                    if(chosenLevel != -7) {
                        getLevel();
                        setBoard();
                        showBoard();
                        animationRunning = false;
                    } else {
                        showHint(R.string.hint_7);
                        new CountDownTimer(5000, 5000) {
                            @Override public void onTick(long l) {}
                            @Override public void onFinish() {
                                startGame();
                            }
                        }.start();
                    }
                }
                @Override public void onAnimationCancel(Animator animator) {}
                @Override public void onAnimationRepeat(Animator animator) {}
            });
        }
    }

    public void collapseBrightHole(final Point firstObject, final Point hole) {
        ObjectAnimator x = ObjectAnimator.ofFloat(mapImageView[firstObject.x][firstObject.y], "x", mapImageView[firstObject.x][firstObject.y].getX(), mapImageView[hole.x][hole.y].getX());
        ObjectAnimator y = ObjectAnimator.ofFloat(mapImageView[firstObject.x][firstObject.y], "y", mapImageView[firstObject.x][firstObject.y].getY(), mapImageView[hole.x][hole.y].getY());
        ObjectAnimator scaleX_2 = ObjectAnimator.ofFloat(mapImageView[hole.x][hole.y], "scaleX", mapImageView[hole.x][hole.y].getScaleX(), 0);
        scaleX_2.setInterpolator(new AnticipateOvershootInterpolator());
        ObjectAnimator scaleY_2 = ObjectAnimator.ofFloat(mapImageView[hole.x][hole.y], "scaleY", mapImageView[hole.x][hole.y].getScaleY(), 0);
        scaleY_2.setInterpolator(new AnticipateOvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.playTogether(x, y, scaleX_2, scaleY_2);
        set.setDuration(duration);
        set.setInterpolator(new AccelerateInterpolator());
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
                showBoardByPos(hole.x, hole.y, new Runnable() {
                    @Override
                    public void run() {
                        if(lastTriangle())
                            collapseDarkHole();
                    }
                });
                rotateTriangles();
                animationRunning = false;
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    public void collapseTriangle(ImageView triangle, final Runnable runnable) {
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

    public void rotateTriangles(Point first, Point second) {
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

    public void rotateTriangles() {
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

    public void rotateTriangle(Point firstObject, Point point) {
        float angle = getAngle(firstObject.x, firstObject.y, mapImageView[point.x][point.y]);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(mapImageView[firstObject.x][firstObject.y], "rotation", mapImageView[firstObject.x][firstObject.y].getRotation(), angle);
        rotate.setDuration(duration);
        rotate.setInterpolator(new DecelerateInterpolator(2f));
        rotate.start();
    }

    public void collapseAllTiles() {
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

    public void unMarkTile(int x, int y) {
        GradientDrawable shape = ((GradientDrawable)((LayerDrawable) tilesImageView[x][y].getBackground()).findDrawableByLayerId(R.id.card));
        animateColorShape(shape, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorAccentBright));
    }

    public ImageView getLastTriangle() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if (currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3 || currentLevel.getMap()[i][j] == 4) return mapImageView[i][j];
        return null;
    }

    public Point getLastTrianglePos() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if (currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3 || currentLevel.getMap()[i][j] == 4) return new Point(i, j);
        return null;
    }

    public ImageView getDarkHole() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 1) return mapImageView[i][j];
        return null;
    }

    public Point getDarkHolePos() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 1) return new Point(i, j);
        return null;
    }

    public boolean lastTriangle() {
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

    private void startGame() {
        if(clicked) unMarkTile(firstObject.x, firstObject.y);
        ObjectAnimator a_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageBackground), "alpha", findViewById(R.id.imageBackground).getAlpha(), 0.1f);
        ObjectAnimator a_2 = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "alpha", findViewById(R.id.imageLogo).getAlpha(), 0);
        ObjectAnimator a_3 = ObjectAnimator.ofFloat(findViewById(R.id.textHint), "alpha", findViewById(R.id.textHint).getAlpha(), 0);
        ObjectAnimator tY_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "translationY", findViewById(R.id.imageLogo).getTranslationY(), -50);
        ObjectAnimator tX_1 = ObjectAnimator.ofFloat(findViewById(R.id.Board), "translationX", findViewById(R.id.Board).getTranslationY(), size.x);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(a_1, a_2, a_3, tY_1, tX_1);
        set.setDuration(duration);
        set.setInterpolator(new AnticipateOvershootInterpolator());
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                sharedPreferences.edit().putBoolean("tutorialEnded", true).apply();
                chosenLevel = 0;
                StartActivity.backgroundTranslationX = findViewById(R.id.imageBackground).getTranslationX();
                StartActivity.backgroundTranslationY = findViewById(R.id.imageBackground).getTranslationY();
                startActivity(new Intent(getApplicationContext(), StartActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }
}
