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
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class StartActivity extends Activity {
    static int WIDTH = 7, HEIGHT = 7, duration = 1000, maxObjects = 5;
    static float backgroundTranslationX = 0, backgroundTranslationY = 0;
    boolean animationRunning = false, clicked = false, hintAvailable = true, addingMode = false;
    int tileSize = 50, moves = 0, hint = 0, availableHints = 20;
    ImageView[][] mapImageView = new ImageView[WIDTH][HEIGHT];
    ImageView[][] tilesImageView = new ImageView[WIDTH][HEIGHT];
    TextView[][] texts = new TextView[WIDTH][HEIGHT];
    ArrayList<Point> addingHints = new ArrayList<>();
    public static LevelObject currentLevel;
    public static Point firstObject, size;
    AnimatorSet hintAnimation;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_start);

        sharedPreferences = getSharedPreferences("Levels", MODE_PRIVATE);
        size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        findViewById(R.id.imageBackground).setTranslationX(StartActivity.backgroundTranslationX);
        findViewById(R.id.imageBackground).setTranslationY(StartActivity.backgroundTranslationY);

        getFinishedLevels();
        getBoard();
        setSizeOfTiles();
        getFirstUndoneLevel();
        getLevel();
        setBoard();
        animateIn();
        setupBackgroundAnimation();
    }

    private void getFinishedLevels() {
        LevelsActivity.finishedLevels.clear();
        availableHints = sharedPreferences.getInt("availableHints", 20);
        for (int i = 0; i < sharedPreferences.getInt("countOfItems", 0); i++)
            LevelsActivity.finishedLevels.add(sharedPreferences.getInt("level_" + i, 0));
    }

    private void saveFinishedLevels() {
        sharedPreferences.edit().putInt("countOfItems", LevelsActivity.finishedLevels.size()).apply();
        sharedPreferences.edit().putInt("availableHints", availableHints).apply();
        for (int i = 0; i < LevelsActivity.finishedLevels.size(); i++)
            sharedPreferences.edit().putInt("level_" + i, LevelsActivity.finishedLevels.get(i)).apply();
    }

    private void animateIn() {
        findViewById(R.id.InformationContainer).setTranslationX(size.x);
        ObjectAnimator x_1 = ObjectAnimator.ofFloat(findViewById(R.id.Board), "x", size.x, 0);
        ObjectAnimator x_2 = ObjectAnimator.ofFloat(findViewById(R.id.InformationContainer), "x", size.x, 0);
        ObjectAnimator x_3 = ObjectAnimator.ofFloat(findViewById(R.id.BottomBar), "x", size.x, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "alpha", 0, 1f);
        ObjectAnimator y_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "translationY", 50, 0);
        x_2.setStartDelay(duration / 4);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(x_1, x_2, x_3, alpha, y_1);
        set.setDuration(duration);
        set.setInterpolator(new AnticipateOvershootInterpolator());
        set.start();
    }

    public void setSizeOfTiles() {
        size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        tileSize = (size.x - 100) / WIDTH;
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mapImageView[i][j].getLayoutParams();
            layoutParams.height = tileSize;
            layoutParams.width = tileSize;
            mapImageView[i][j].setLayoutParams(layoutParams);
        }
    }

    public void setupBackgroundAnimation() {
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
                                if(currentLevel.getMap()[finalI][finalJ] > 1 || addingMode)
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

    private void getFirstUndoneLevel() {
        if(TutorialActivity.chosenLevel == 0) {
            int maxLevels = 0;
            for (int section : LevelsActivity.sections) maxLevels += section;
            for (int i = 1; i <= maxLevels; i++)
                if (!LevelsActivity.finishedLevels.contains(i)) {
                    TutorialActivity.chosenLevel = i;
                    break;
                }
        }
    }

    public void getLevel() {
        if(hintAnimation != null) hintAnimation.cancel();
        hintAvailable = true;
        hint = moves = 0;
        currentLevel = Levels.getLevel(TutorialActivity.chosenLevel);
        setHintAvailable();
        setMoves();
        setLevel();
        rotateTriangles();
    }

    private void setHintAvailable() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(findViewById(R.id.imageHint), "alpha", findViewById(R.id.imageHint).getAlpha(), 1);
        alpha.setDuration(duration);
        alpha.start();
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

    public void clickBack(View view) {
        if(findViewById(R.id.HintChoiceBox).getVisibility() != View.VISIBLE) {
            if (clicked) {
                GradientDrawable shape = (GradientDrawable) ((LayerDrawable) tilesImageView[firstObject.x][firstObject.y].getBackground()).findDrawableByLayerId(R.id.card);
                animateColorShape(shape, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorAccentBright));
                clicked = false;
            }
            ObjectAnimator x_1 = ObjectAnimator.ofFloat(findViewById(R.id.Board), "x", findViewById(R.id.Board).getX(), size.x);
            ObjectAnimator x_2 = ObjectAnimator.ofFloat(findViewById(R.id.InformationContainer), "x", findViewById(R.id.InformationContainer).getX(), size.x);
            ObjectAnimator x_3 = ObjectAnimator.ofFloat(findViewById(R.id.BottomBar), "x", findViewById(R.id.BottomBar).getX(), size.x);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "alpha", findViewById(R.id.imageLogo).getAlpha(), 0);
            ObjectAnimator y_1 = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "translationY", findViewById(R.id.imageLogo).getTranslationY(), -50);
            x_2.setStartDelay(duration / 4);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(x_1, x_2, x_3, alpha, y_1);
            set.setDuration(duration);
            set.setInterpolator(new AnticipateOvershootInterpolator());
            set.start();
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    showAllLevels();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }
    }

    private void showAllLevels() {
        backgroundTranslationX = findViewById(R.id.imageBackground).getTranslationX();
        backgroundTranslationY = findViewById(R.id.imageBackground).getTranslationY();
        startActivity(new Intent(getApplicationContext(), LevelsActivity.class));
        finish();
        overridePendingTransition(0, 0);
    }

    public void clickRestart(View view) {
        if(!animationRunning) {
            animationRunning = true;
            if(clicked) {
                GradientDrawable shape = (GradientDrawable) ((LayerDrawable) tilesImageView[firstObject.x][firstObject.y].getBackground()).findDrawableByLayerId(R.id.card);
                animateColorShape(shape, getResources().getColor(R.color.colorAccent), getResources().getColor(R.color.colorAccentBright));
                clicked = false;
            }
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
        ((TextView) findViewById(R.id.textLevel)).setText(getResources().getString(R.string.level) + " " + TutorialActivity.chosenLevel);
    }

    public void clickAtPos(final int x, final int y) {
        if(addingMode) {
            if(currentLevel.getMap()[x][y] == 0) addingHints.add(new Point(x, y));
            else if(currentLevel.getMap()[x][y] == maxObjects) addingHints.remove(addingHints.size() - 1);
            currentLevel.getMap()[x][y] = currentLevel.getMap()[x][y] >= maxObjects ? 0 : currentLevel.getMap()[x][y] + 1;
            setBoardByPos(x, y, false);
            texts[x][y].setText("" + addingHints.size());
        } else if(!animationRunning) {
            animationRunning = true;
            GradientDrawable shape = (GradientDrawable) ((LayerDrawable) tilesImageView[x][y].getBackground()).findDrawableByLayerId(R.id.card);
            animateColorShape(shape, getResources().getColor(R.color.colorAccentBright), getResources().getColor(R.color.colorAccent));
            rescaleTile(x, y);
            if(!clicked) {
//                checkHint(new Point(x, y));
                hint++;
                if (hintAnimation != null) hintAnimation.cancel();
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
                        checkHint(new Point(x, y));
                        rotateTriangles(firstObject, new Point(x, y));
                        collapseTriangles(firstObject, new Point(x, y), midPoint);
                    } else {
                        showHint(R.string.hint);
                        animationRunning = false;
                        hint--;
                    }
                } else if(typeSecond == 5 && (x == firstObject.x || y == firstObject.y || Math.abs(firstObject.x - x) == Math.abs(firstObject.y - y))) {
                    checkHint(new Point(firstObject.x, firstObject.y));
                    rotateTriangle(firstObject, new Point(x, y));
                    collapseBrightHole(firstObject, new Point(x, y));
                } else if(!firstObject.equals(new Point(x, y))) {
                    showHint(R.string.hint);
                    animationRunning = false;
                    hint--;
                } else {
                    animationRunning = false;
                    hint--;
                }
                unMarkTile(firstObject.x, firstObject.y);
                unMarkTile(x, y);
                clicked = false;
            }
        }
    }

    private void rescaleTile(int x, int y) {
        ObjectAnimator sX = ObjectAnimator.ofFloat(mapImageView[x][y], "scaleX", mapImageView[x][y].getScaleX(), 0.6f);
        ObjectAnimator sY = ObjectAnimator.ofFloat(mapImageView[x][y], "scaleY", mapImageView[x][y].getScaleY(), 0.6f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sX, sY);
        set.setDuration(duration);
        set.start();
    }

    public void clickHint(View view) {
        findViewById(R.id.HintChoiceBox).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textAvailableHints)).setText(getResources().getString(R.string.you_have) + " " + availableHints + " " + getResources().getString(R.string.hints));
        ObjectAnimator sX_1 = ObjectAnimator.ofFloat(findViewById(R.id.HintChoiceBox), "scaleX", 0, 1);
        ObjectAnimator sY_1 = ObjectAnimator.ofFloat(findViewById(R.id.HintChoiceBox), "scaleY", 0, 1);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sX_1, sY_1);
        set.setDuration(duration);
        set.setInterpolator(new AnticipateOvershootInterpolator());
        set.start();
    }

    public void hideHints(View view) {
        ObjectAnimator sX_1 = ObjectAnimator.ofFloat(findViewById(R.id.HintChoiceBox), "scaleX", 1, 0);
        ObjectAnimator sY_1 = ObjectAnimator.ofFloat(findViewById(R.id.HintChoiceBox), "scaleY", 1, 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sX_1, sY_1);
        set.setDuration(duration);
        set.setInterpolator(new AnticipateOvershootInterpolator());
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                findViewById(R.id.HintChoiceBox).setVisibility(View.INVISIBLE);
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    public void useHint(View view) {
        if(findViewById(R.id.imageHint).getAlpha() == 1 && hint < Hints.getHint(TutorialActivity.chosenLevel).size() && availableHints > 0) {
            availableHints--;
            hideHints(null);
            ImageView imageView = mapImageView[Hints.getHint(TutorialActivity.chosenLevel).get(hint).x][Hints.getHint(TutorialActivity.chosenLevel).get(hint).y];
            float previousScale = imageView.getScaleX();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", previousScale, 1, previousScale);
            scaleX.setRepeatCount(ValueAnimator.INFINITE);
            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", previousScale, 1, previousScale);
            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);
            hintAnimation = new AnimatorSet();
            hintAnimation.playTogether(scaleX, scaleY);
            hintAnimation.setDuration(duration * 2);
            hintAnimation.start();
        }
    }

    private void checkHint(Point point) {
        try {
            hintAvailable = Hints.getHint(TutorialActivity.chosenLevel).get(hint).equals(point) ||
                    (Hints.getHint(TutorialActivity.chosenLevel).size() > hint + 1 && Hints.getHint(TutorialActivity.chosenLevel).get(hint + 1).equals(point)) ||
                    (hint > 0 && Hints.getHint(TutorialActivity.chosenLevel).get(hint - 1).equals(point));
            ObjectAnimator alpha = ObjectAnimator.ofFloat(findViewById(R.id.imageHint), "alpha", findViewById(R.id.imageHint).getAlpha(), hintAvailable ? 1f : 0.3f);
            alpha.setDuration(duration);
            alpha.start();
            hint++;
            if (hintAnimation != null) hintAnimation.cancel();
        } catch (Exception ignored) {}
    }

    public void showHint(int hint) {
        showHint(hint, 0);
    }

    public void showHint(int hint, final int delay) {
        if(findViewById(R.id.HintBox).getVisibility() != View.VISIBLE) {
            findViewById(R.id.HintBox).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textHint)).setText(getResources().getString(hint));
            ObjectAnimator translation_1 = ObjectAnimator.ofFloat(findViewById(R.id.HintBox), "translationX", size.x, 0);
            AnimatorSet set = new AnimatorSet();
            set.playSequentially(translation_1);
            set.setDuration(duration);
            set.setInterpolator(new AnticipateOvershootInterpolator());
            set.start();
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    new CountDownTimer(delay, delay) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            ObjectAnimator translation_2 = ObjectAnimator.ofFloat(findViewById(R.id.HintBox), "translationX", 0, -findViewById(R.id.HintBox).getWidth() * 2);
                            translation_2.setDuration(duration);
                            translation_2.setStartDelay(duration);
                            translation_2.setInterpolator(new AnticipateOvershootInterpolator());
                            translation_2.start();
                            translation_2.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    findViewById(R.id.HintBox).setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {
                                }
                            });
                        }
                    }.start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }
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
        moves++;
        setMoves();
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
                            if(currentLevel.getMoves() - moves == 0 && !lastTriangle()) {
                                showHint(R.string.too_much_moves);
                                animationRunning = false;
                                clickRestart(null);
                            } else collapseDarkHole();
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
                collapseDarkHole();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    public void collapseDarkHole() {
        ImageView lastTriangle = getLastTriangle();
        if(lastTriangle() && (getLastTrianglePos().x == getDarkHolePos().x || getLastTrianglePos().y == getDarkHolePos().y ||
                Math.abs(getLastTrianglePos().x - getDarkHolePos().x) == Math.abs(getLastTrianglePos().y - getDarkHolePos().y))) {
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
                                    if(!LevelsActivity.finishedLevels.contains(TutorialActivity.chosenLevel))
                                        LevelsActivity.finishedLevels.add(TutorialActivity.chosenLevel);
                                    int maxLevels = 0;
                                    for(int section : LevelsActivity.sections) maxLevels += section;
                                    if(TutorialActivity.chosenLevel < maxLevels)
                                        TutorialActivity.chosenLevel++;
                                    else clickBack(null);
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
        } else if(lastTriangle()){
            showHint(R.string.wrong_direction, 2000);
            clickRestart(null);
        }
    }

    public void collapseBrightHole(final Point firstObject, final Point hole) {
        moves++;
        setMoves();
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
                showBoardByPos(hole.x, hole.y);
                rotateTriangles();
                if(currentLevel.getMoves() - moves == 0 && !lastTriangle()) {
                    showHint(R.string.too_much_moves);
                    animationRunning = false;
                    clickRestart(null);
                } else collapseDarkHole();
                animationRunning = false;
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

    private int getCountOf(int value) {
        int count = 0;
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == value) count++;
        return count;
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
            if (currentLevel.getMap()[i][j] == 2 || currentLevel.getMap()[i][j] == 3 || currentLevel.getMap()[i][j] == 4) count++;
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

    public void clickAddLevel(View view) {
        if(!addingMode) {
            setNumbers();
            addingHints.clear();
            startRotation();
            resetBoard();
            setBoard();
            addingMode = true;
            animationRunning = true;
        } else {
            removeTexts();
            printMap();
            printHints();
            addingMode = false;
            animationRunning = false;
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    private void removeTexts() {
        takeScreenshot();
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            ((RelativeLayout) findViewById(R.id.Board)).removeView(texts[i][j]);
    }

    private void setNumbers() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++) {
            final int finalJ = j;
            final int finalI = i;
            mapImageView[i][j].post(new Runnable() {
                @Override
                public void run() {
                    TextView text = new TextView(getApplicationContext());
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mapImageView[finalI][finalJ].getWidth(), mapImageView[finalI][finalJ].getHeight());
                    text.setX(mapImageView[finalI][finalJ].getX() - mapImageView[0][0].getLeft());
                    text.setY(mapImageView[finalI][finalJ].getY() - mapImageView[0][0].getTop());
                    text.setLayoutParams(layoutParams);
                    text.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    text.setTextSize(20);
                    text.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    ((RelativeLayout) findViewById(R.id.Board)).addView(text);
                    text.bringToFront();
                    text.invalidate();
                    texts[finalI][finalJ] = text;
                }
            });
        }
    }

    private void printHints() {
        String output = "";
        addingHints.remove(0);
        for(int i = 0; i < addingHints.size(); i++)
            output += "clicks.add(new Point(" + addingHints.get(i).x + ", " + addingHints.get(i).y + "));\n";
        output += "break;\n";
        Log.d("Hint", output);
    }

    private void printMap() {
        int moves = 0;
        String output = "";
        output += "map[" + getDarkHolePos().x + "][" + getDarkHolePos().y + "] = 1; //Dark hole\n";
        for(int i = 0, count = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 2) {
                output += "map[" + i + "][" + j + "] = 2" + (count == 0 ? "; //Triangle\n" : ";\n");
                count++;
            }
        for(int i = 0, count = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 3) {
                output += "map[" + i + "][" + j + "] = 3" + (count == 0 ? "; //Empty triangle\n" : ";\n");
                count++;
                moves++;
            }
        for(int i = 0, count = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 4) {
                output += "map[" + i + "][" + j + "] = 4" + (count == 0 ? "; //Dotted triangle\n" : ";\n");
                count++;
                moves++;
            }
        for(int i = 0, count = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            if(currentLevel.getMap()[i][j] == 5) {
                output += "map[" + i + "][" + j + "] = 5" + (count == 0 ? "; //Bright hole\n" : ";\n");
                count++;
            }
        output += "moves = " + moves + ";\n";
        output += "difficulty = 4;\n";
        output += "break;\n";
        Log.d("Level", output);
    }

    private void resetBoard() {
        for(int i = 0; i < WIDTH; i++) for(int j = 0; j < HEIGHT; j++)
            currentLevel.getMap()[i][j] = 0;
    }

    private void startRotation() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(findViewById(R.id.imageLogo), "rotation", 0, 10, -10);
        rotation.setRepeatCount(1);
        rotation.setRepeatMode(ValueAnimator.REVERSE);
        rotation.setDuration(LevelsActivity.duration);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setStartDelay(StartActivity.duration);
        rotation.start();
        rotation.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {}
            @Override public void onAnimationEnd(Animator animator) {
                if(addingMode) startRotation();
            }
            @Override public void onAnimationCancel(Animator animator) {}
            @Override public void onAnimationRepeat(Animator animator) {}
        });
    }

    @Override
    public void onBackPressed() {
        saveFinishedLevels();
        if(findViewById(R.id.HintBox).getVisibility() == View.VISIBLE) super.onBackPressed();
        else if(findViewById(R.id.HintChoiceBox).getVisibility() == View.VISIBLE)
            hideHints(null);
        else showHint(R.string.sure);
    }

    @Override
    public void onPause() {
        saveFinishedLevels();
        super.onPause();
    }

    @Override
    protected void onStop() {
        saveFinishedLevels();
        super.onStop();
    }
}