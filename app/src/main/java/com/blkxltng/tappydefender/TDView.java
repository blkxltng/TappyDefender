package com.blkxltng.tappydefender;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.x;
import static android.R.attr.y;

/**
 * Created by firej on 1/18/2017.
 */

public class TDView extends SurfaceView implements Runnable {

    //Thread related
    volatile boolean playing;
    Thread gameThread = null;

    //Game objects
    private PlayerShip mPlayer;
    public EnemyShip mEnemyShip1, mEnemyShip2, mEnemyShip3;

    //Make some dust
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    //For drawing to screen
    private Paint mPaint;
    private Canvas mCanvas;
    private SurfaceHolder mHolder;

    //Timing
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    //Resolution
    private int screenX;
    private int screenY;

    private Context mContext;

    private boolean gameEnded;

    //For sounds
    private SoundPool mSoundPool;
    int sndStart = -1;
    int sndBump = -1;
    int sndDestroyed = -1;
    int sndWin = -1;

    //For saving data ie. scores
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public TDView(Context context, int x, int y) {
        super(context);

        mContext = context;

        //Load the sounds
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = mContext.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("start.ogg");
            sndStart = mSoundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("win.ogg");
            sndWin = mSoundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("bump.ogg");
            sndBump = mSoundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("destroyed.ogg");
            sndDestroyed = mSoundPool.load(descriptor, 0);
        } catch (IOException e) {
            //Print an error
            Log.e("Error", "failed to load sound files!");
        }

        //Get Resolution
        screenX = x;
        screenY = y;

        //Initialize objects for drawing
        mHolder = getHolder();
        mPaint = new Paint();

//        //Initialize the player
//        mPlayer = new PlayerShip(context, x, y);
//
//        //Initialize enemy ships
//        mEnemyShip1 = new EnemyShip(context, x, y);
//        mEnemyShip2 = new EnemyShip(context, x, y);
//        mEnemyShip3 = new EnemyShip(context, x, y);
//
//        //Initialize dust
//        int numDust = 40;
//        for(int i = 0; i < numDust; i++) {
//            //Where to spawn?
//            SpaceDust speck = new SpaceDust(x,y);
//            dustList.add(speck);
//        }

        //Get a reference to a file called HighScores. It is created if the id doesn't exist
        mPreferences = mContext.getSharedPreferences("HighScores", mContext.MODE_PRIVATE);
        //Initialize the editor
        mEditor = mPreferences.edit();
        //Load the fastest time from a entry in the file labled "fastestTime" if none then score is 10000000
        fastestTime = mPreferences.getLong("fastestTime", 1000000);

        startGame();
    }

    @Override
    public void run() {
        while(playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {

        //Collision detection on new positions before move because we are testing the last frame's
        //position which has just been drawn
        boolean hitDetected = false;

        //If you use images in excess of 100 pixels wide, increase the -100 value accordingly
        if(Rect.intersects(mPlayer.getHitBox(), mEnemyShip1.getHitBox())) {
            hitDetected = true;
            mEnemyShip1.setX(-100);
        }
        if(Rect.intersects(mPlayer.getHitBox(), mEnemyShip2.getHitBox())) {
            hitDetected = true;
            mEnemyShip2.setX(-100);
        }
        if(Rect.intersects(mPlayer.getHitBox(), mEnemyShip3.getHitBox())) {
            hitDetected = true;
            mEnemyShip3.setX(-100);
        }

        if(hitDetected) {
            mSoundPool.play(sndBump, 1, 1, 0, 0, 1);
            mPlayer.reduceShieldStrength();
            if(mPlayer.getShieldStrength() < 0) {
                //Game over
                mSoundPool.play(sndDestroyed, 1, 1, 0, 0, 1);
                gameEnded = true;
            }
        }

        //Update the player
        mPlayer.update();

        //Update the enemies
        mEnemyShip1.update(mPlayer.getSpeed());
        mEnemyShip2.update(mPlayer.getSpeed());
        mEnemyShip3.update(mPlayer.getSpeed());

        //For the dust
        for(SpaceDust spaceDust : dustList) {
            spaceDust.update(mPlayer.getSpeed());
        }

        //Change the distance remaining
        if(!gameEnded) {
            //Subtract distance to the home planet based on the current speed
            distanceRemaining -= mPlayer.getSpeed();

            //How long has the player ben flying?
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        //Finished the game
        if(distanceRemaining < 0) {
            mSoundPool.play(sndWin, 1, 1, 0, 0, 1);
            //Check for a fastest time
            if(timeTaken < fastestTime) {
                //Save the score
                mEditor.putLong("fastestTime", timeTaken);
                mEditor.commit();
                fastestTime = timeTaken;
            }

            //No negative numbers
            distanceRemaining = 0;

            //end the game
            gameEnded = true;
        }
    }

    private void draw() {

        if(mHolder.getSurface().isValid()) {

            //Lock the area of memory we will draw to
            mCanvas = mHolder.lockCanvas();

            //Clear the last frame
            mCanvas.drawColor(Color.argb(255, 0, 0, 0));

//            //FOR DEBUGGING
//            mPaint.setColor(Color.argb(255, 255, 255, 255));
//            //Draw hitboxes
//            mCanvas.drawRect(mPlayer.getHitBox().left, mPlayer.getHitBox().top,
//                    mPlayer.getHitBox().right, mPlayer.getHitBox().bottom, mPaint);
//            mCanvas.drawRect(mEnemyShip1.getHitBox().left, mEnemyShip1.getHitBox().top,
//                    mEnemyShip1.getHitBox().right, mEnemyShip1.getHitBox().bottom, mPaint);
//            mCanvas.drawRect(mEnemyShip2.getHitBox().left, mEnemyShip2.getHitBox().top,
//                    mEnemyShip2.getHitBox().right, mEnemyShip2.getHitBox().bottom, mPaint);
//            mCanvas.drawRect(mEnemyShip3.getHitBox().left, mEnemyShip3.getHitBox().top,
//                    mEnemyShip3.getHitBox().right, mEnemyShip3.getHitBox().bottom, mPaint);
//            //DEBUGGING END

            //Set white for dust
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            //Draw the dust
            for(SpaceDust spaceDust : dustList) {
                mCanvas.drawPoint(spaceDust.getX(), spaceDust.getY(), mPaint);
            }

            //Draw the player
            mCanvas.drawBitmap(mPlayer.getBitmap(), mPlayer.getX(), mPlayer.getY(), mPaint);

            //Draw the enemies
            mCanvas.drawBitmap(mEnemyShip1.getBitmap(), mEnemyShip1.getX(), mEnemyShip1.getY(), mPaint);
            mCanvas.drawBitmap(mEnemyShip2.getBitmap(), mEnemyShip2.getX(), mEnemyShip2.getY(), mPaint);
            mCanvas.drawBitmap(mEnemyShip3.getBitmap(), mEnemyShip3.getX(), mEnemyShip3.getY(), mPaint);

            if(!gameEnded) {
                //Draw the HUD
                mPaint.setTextAlign(Paint.Align.LEFT);
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(25);
                mCanvas.drawText("Fastest: " + fastestTime + "s", 10, 20, mPaint);
                mCanvas.drawText("Time: " + timeTaken + "s", screenX/2, 20, mPaint);
                mCanvas.drawText("Distance: " + distanceRemaining / 1000 + " KM", screenX/3, screenY - 20, mPaint);
                mCanvas.drawText("Shield: " + mPlayer.getShieldStrength(), 10, screenY-20, mPaint);
                mCanvas.drawText("Speed: " + mPlayer.getSpeed() * 60 + " MPS", (screenX/3)*2, screenY-20, mPaint);
            } else {
                //What to do if the game has ended
                //Show the pause screen
                mPaint.setTextSize(80);
                mPaint.setTextAlign(Paint.Align.CENTER);
                mCanvas.drawText("Game Over", screenX/2, 100, mPaint);
                mPaint.setTextSize(25);
                mCanvas.drawText("Fastest: " + fastestTime + "s", screenX/2, 160, mPaint);
                mCanvas.drawText("Time: " + timeTaken + "s", screenX/2, 200, mPaint);
                mCanvas.drawText("Distance Remaining: " + distanceRemaining + "KM", screenX/2, 240, mPaint);
                mPaint.setTextSize(80);
                mCanvas.drawText("Tap to Replay!", screenX/2, 350, mPaint);
            }

            //Unlock and draw the scene
            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            //catch things here
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            //catch things here
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        switch(event.getAction()& MotionEvent.ACTION_MASK) {
            //Is the player's finger up?
            case MotionEvent.ACTION_UP:
                mPlayer.stopBoosting();
                break;

            //Is the player's finger on the screen?
            case MotionEvent.ACTION_DOWN:
                mPlayer.setBoosting();
                //Start the game again if it has ended
                if(gameEnded) {
                    startGame();
                }
                break;
        }

        return true;
    }

    private void startGame() {
        //Initialize the game objects
        mPlayer = new PlayerShip(mContext, screenX, screenY);
        mEnemyShip1 = new EnemyShip(mContext, screenX, screenY);
        mEnemyShip2 = new EnemyShip(mContext, screenX, screenY);
        mEnemyShip3 = new EnemyShip(mContext, screenX, screenY);

        int numDust = 40;
        for(int i = 0; i < numDust; i++) {
            //Where to spawn?
            SpaceDust speck = new SpaceDust(x,y);
            dustList.add(speck);
        }

        //Reset the time and distance
        distanceRemaining = 10000; //10 KM
        timeTaken = 0;

        //Get the start time
        timeStarted = System.currentTimeMillis();

        gameEnded = false;

        mSoundPool.play(sndStart, 1, 1, 0, 0, 1);
    }

}
