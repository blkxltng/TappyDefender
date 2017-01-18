package com.blkxltng.tappydefender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

    //For drawing to screen
    private Paint mPaint;
    private Canvas mCanvas;
    private SurfaceHolder mHolder;

    public TDView(Context context, int x, int y) {
        super(context);

        //Initialize objects for drawing
        mHolder = getHolder();
        mPaint = new Paint();

        //Initialize the player
        mPlayer = new PlayerShip(context, x, y);

        //Initialize enemy ships
        mEnemyShip1 = new EnemyShip(context, x, y);
        mEnemyShip2 = new EnemyShip(context, x, y);
        mEnemyShip3 = new EnemyShip(context, x, y);
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

        //Update the player
        mPlayer.update();

        //Update the enemies
        mEnemyShip1.update(mPlayer.getSpeed());
        mEnemyShip2.update(mPlayer.getSpeed());
        mEnemyShip3.update(mPlayer.getSpeed());
    }

    private void draw() {

        if(mHolder.getSurface().isValid()) {

            //Lock the area of memory we will draw to
            mCanvas = mHolder.lockCanvas();

            //Clear the last frame
            mCanvas.drawColor(Color.argb(255, 0, 0, 0));

            //Draw the player
            mCanvas.drawBitmap(mPlayer.getBitmap(), mPlayer.getX(), mPlayer.getY(), mPaint);

            //Draw the enemies
            mCanvas.drawBitmap(mEnemyShip1.getBitmap(), mEnemyShip1.getX(), mEnemyShip1.getY(), mPaint);
            mCanvas.drawBitmap(mEnemyShip2.getBitmap(), mEnemyShip2.getX(), mEnemyShip2.getY(), mPaint);
            mCanvas.drawBitmap(mEnemyShip3.getBitmap(), mEnemyShip3.getX(), mEnemyShip3.getY(), mPaint);

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
                break;
        }

        return true;
    }

}
