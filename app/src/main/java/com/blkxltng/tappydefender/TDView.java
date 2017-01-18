package com.blkxltng.tappydefender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    //For drawing to screen
    private Paint mPaint;
    private Canvas mCanvas;
    private SurfaceHolder mHolder;

    public TDView(Context context) {
        super(context);

        //Initialize objects for drawing
        mHolder = getHolder();
        mPaint = new Paint();

        //Initialize the player
        mPlayer = new PlayerShip(context);
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
    }

    private void draw() {

        if(mHolder.getSurface().isValid()) {

            //Lock the area of memory we will draw to
            mCanvas = mHolder.lockCanvas();

            //Clear the last frame
            mCanvas.drawColor(Color.argb(255, 0, 0, 0));

            //Draw the player
            mCanvas.drawBitmap(mPlayer.getBitmap(), mPlayer.getX(), mPlayer.getY(), mPaint);

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
}
