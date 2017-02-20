package com.rusticsam;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.rusticsam.sprites.Background;
import com.rusticsam.sprites.BadTile;
import com.rusticsam.sprites.GoodTile;
import com.rusticsam.sprites.PauseButton;
import com.rusticsam.sprites.Tile;
import com.rusticsam.sprites.Tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Debug;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView{
    public static final long UPDATE_INTERVAL = 50;// = 20 FPS
    public static final int MIN_TIME_RED = 500;
    public static final int MAX_TIME_RED = 1500;
    public static final int NB_TILES = 5;
    
    private Timer timer = new Timer();
    private Timer spawnTimer = new Timer();
    private TimerTask timerTask;

    private SurfaceHolder holder;
    
    private Game game;
    private Tile[][] tiles;
    private Background background;
    
    private PauseButton pauseButton;
    volatile private boolean paused = true;
    
    private Tutorial tutorial;
    private boolean tutorialIsShown = true;

    public GameView(Context context) {
        super(context);
        this.game = (Game) context;
        setFocusable(true);

        tiles = new Tile[NB_TILES][NB_TILES];
        for (int i = 0; i < NB_TILES; i++)
            for (int j = 0; j < NB_TILES; j++)
                tiles[i][j] = new BadTile(this, game, i, j);
        background = new Background(this, game);

        holder = getHolder();
        pauseButton = new PauseButton(this, game);
        tutorial = new Tutorial(this, game);
    }
    
    private void startTimer() {
        setUpTimerTask();
        timer = new Timer();
        timer.schedule(timerTask, UPDATE_INTERVAL, UPDATE_INTERVAL);

        spawnTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                spawnTile();
            }
        }, 1500, 1500);
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
    }
    
    private void setUpTimerTask() {
        stopTimer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                GameView.this.run();
            }
        };
    }
    
    @Override
    public boolean performClick() {
        return super.performClick();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        { // No support for dead players
            if(tutorialIsShown){
                // dismiss tutorial
                tutorialIsShown = false;
                resume();
                onTap(event);
            }else if(paused){
                resume();
            }else if(pauseButton.isTouching((int) event.getX(), (int) event.getY()) && !this.paused){
                pause();
            }else{
                onTap(event);
            }
        }
        return true;
    }

    private void onTap(MotionEvent event)
    {
        for (int i = 0; i < NB_TILES; i++) {
            for (int j = 0; j < NB_TILES; j++) {
                if (tiles[i][j].isTouching((int)event.getX(), (int) event.getY())) {
                    tiles[i][j].onPress();
                    if (tiles[i][j].isGood()) {
                        tiles[i][j].release();
                        tiles[i][j] = new BadTile(this, game, i, j);
                        Random r = new Random();
                        int randomTime = r.nextInt(MAX_TIME_RED - MIN_TIME_RED + 1) + MIN_TIME_RED;
                        if (spawnTimer != null)
                            spawnTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                spawnTile();
                            }
                        }, randomTime);
                    }
                }
            }
        }
    }

    public void destroyGoodTileAt(int x, int y)
    {
        Log.d("GameView", "Destroyed at: " + x + y + "is good" + tiles[x][y].isGood());
        //gameOver();
        if (spawnTimer == null)
            return;

        spawnTimer.cancel();
        spawnTimer = null;

        for (int i = 0; i < NB_TILES; i++)
            for (int j = 0; j < NB_TILES; j++)
                if (i != x && j != y)
                    tiles[i][j] = new BadTile(this, game, i, j);
    }

    private void spawnTile()
    {
        Random r = new Random();
        int randomX = r.nextInt(NB_TILES);
        int randomY = r.nextInt(NB_TILES);

        tiles[randomX][randomY] = new GoodTile(this, game, randomX, randomY);
    }
    
    /**
     * content of the timertask
     */
    public void run() {
        move();

        draw();
    }
    
    /**
     * Draw Tutorial
     */
    public void showTutorial(){
        pauseButton.move();
        
        while(!holder.getSurface().isValid()){
            /*wait*/
            try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        
        Canvas canvas = holder.lockCanvas();
        drawCanvas(canvas, true);
        tutorial.move();
        tutorial.draw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }
    
    public void pause(){
        stopTimer();
        paused = true;
    }
    
    public void drawOnce(){
        (new Thread(new Runnable() {
            @Override
            public void run() {
                if(tutorialIsShown){
                    showTutorial();
                } else {
                    draw();
                }
            }
        })).start();
    }
    
    public void resume(){
        paused = false;
        startTimer();
    }
    
    /**
     * Draws all gameobjects on the surface
     */
    private void draw() {
        while(!holder.getSurface().isValid()){
            /*wait*/
            try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        Canvas canvas = holder.lockCanvas();
        drawCanvas(canvas, true);
        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * Draws everything normal,
     * except the player will only be drawn, when the parameter is true
     * @param drawPlayer
     */
    private void drawCanvas(Canvas canvas, boolean drawPlayer){
        canvas.drawColor(Color.rgb(50, 50, 50));
        background.draw(canvas);

        pauseButton.draw(canvas);

        for (int i = 0; i < NB_TILES; i++)
            for (int j = 0; j < NB_TILES; j++)
                tiles[i][j].draw(canvas);
        
        // Score Text
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(getScoreTextMetrics());
        canvas.drawText(game.getResources().getString(R.string.onscreen_score_text) + " " + game.accomplishmentBox.points
                        + " / " + game.getResources().getString(R.string.onscreen_coin_text) + " " + game.score,
                        0, getScoreTextMetrics(), paint);
    }

    
    /**
     * Update sprite movements
     */
    private void move(){
        pauseButton.move();
    }


    /**
     * Let's the player fall down dead, makes sure the runcycle stops
     * and invokes the next method for the dialog and stuff.
     */
    public void gameOver(){
        pause();
        game.gameOver();
    }
    
    public void revive() {
        game.numberOfRevive++;
        
        // This needs to run another thread, so the dialog can close.
        new Thread(new Runnable() {
            @Override
            public void run() {
                setupRevive();
            }
        }).start();
    }
    
    /**
     * Sets the player into startposition
     * Removes obstacles.
     * Let's the character blink a few times.
     */
    private void setupRevive(){
        game.gameOverDialog.hide();
        for(int i = 0; i < 6; ++i){
            while(!holder.getSurface().isValid()){/*wait*/}
            Canvas canvas = holder.lockCanvas();
            drawCanvas(canvas, i%2 == 0);
            holder.unlockCanvasAndPost(canvas);
            // sleep
            try { Thread.sleep(UPDATE_INTERVAL*6); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        resume();
    }
    
    /**
     * A value for the position and size of the onScreen score Text
     */
    public int getScoreTextMetrics(){
        return (int) (this.getHeight() / 21.0f);
        /*/ game.getResources().getDisplayMetrics().density)*/
    }
    
    public Game getGame(){
        return this.game;
    }

}
