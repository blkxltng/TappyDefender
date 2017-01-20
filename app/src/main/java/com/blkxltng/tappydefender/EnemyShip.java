package com.blkxltng.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by firej on 1/18/2017.
 */

public class EnemyShip {

    private Bitmap mBitmap;
    private int x, y;
    private int speed = 1;

    //Detect enemies leaving screen
    private int maxX;
    private int minX;

    //spawn enemies within bounds of screen
    private int maxY;
    private int minY;

    //Hitbox for collision
    private Rect hitBox;

    public Rect getHitBox() {
        return hitBox;
    }

    //Getters
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    //Constructor
    public EnemyShip(Context context, int screenX, int screenY) {

        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        Random generator = new Random();
        speed = generator.nextInt(6) + 10;

        x = screenX;
        y = generator.nextInt(maxY) - getBitmap().getHeight();

        //Initialize the hitbox
        hitBox = new Rect(x, y, mBitmap.getWidth(), mBitmap.getHeight());
    }

    //Used by the TDView update() method to make an enemy out of bounds and force respawn
    public void setX(int x) {
        this.x = x;
    }

    public void update(int playerSpeed) {

        //Move to the left
        x -= playerSpeed;
        x -= speed;

        //respawn when off screen
        if(x < minX-mBitmap.getWidth()) {
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;

            x = maxX;
            y = generator.nextInt(maxY) - mBitmap.getHeight();
        }

        //Refresh the hitbox
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + mBitmap.getWidth();
        hitBox.bottom = y + mBitmap.getHeight();
    }
}
