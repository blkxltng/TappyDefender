package com.blkxltng.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by firej on 1/18/2017.
 */

public class PlayerShip {

    private Bitmap mBitmap;
    private int x, y;
    private int speed = 0;
    private boolean boosting;
    private final int GRAVITY = -12;

    //To keep the ship from leaving the screen
    private int maxY;
    private int minY;

    //Limit the bounds of the ships speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    //Hitbox for collision
    private Rect hitBox;

    private int shieldStrength;

    public Rect getHitBox() {
        return hitBox;
    }

    public int getShieldStrength() {
        return shieldStrength;
    }

    public PlayerShip(Context context, int screenX, int screenY) {
        x = 50;
        y = 50;
        speed = 1;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        scaleBitmap(screenX);
        maxY = screenY - mBitmap.getHeight();
        minY = 0;

        //Initialize the hitbox
        hitBox = new Rect(x, y, mBitmap.getWidth(), mBitmap.getHeight());

        shieldStrength = 2;

    }

    public void update() {

        //Is we boostin?
        if(boosting) {
            //Speed up
            speed += 2;
        } else {
            //Slow down
            speed -= 5;
        }

        //Limit the top speed
        if(speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        //Never stop altogether
        if(speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        //Move the ship vertically
        y -= speed + GRAVITY;

        //Don't let the ship leave the screen
        if(y < minY) {
            y = minY;
        }
        if(y > maxY) {
            y = maxY;
        }

        //Refresh the hitbox
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + mBitmap.getWidth();
        hitBox.bottom = y + mBitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setBoosting() {
        boosting = true;
    }

    public void stopBoosting() {
        boosting = false;
    }

    public void reduceShieldStrength() {
        shieldStrength--;
    }

    public void scaleBitmap(int x) {

        if(x < 1000) {
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth()/3, mBitmap.getHeight()/3, false);
        } else if(x < 1200) {
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth()/2, mBitmap.getHeight()/2, false);
        }
    }
}
