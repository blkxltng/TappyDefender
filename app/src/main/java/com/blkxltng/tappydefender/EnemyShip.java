package com.blkxltng.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
    }
}
