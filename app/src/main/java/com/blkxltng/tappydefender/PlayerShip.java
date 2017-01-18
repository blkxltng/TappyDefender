package com.blkxltng.tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by firej on 1/18/2017.
 */

public class PlayerShip {

    private Bitmap mBitmap;
    private int x, y;
    private int speed = 0;

    public PlayerShip(Context context) {
        x = 50;
        y = 50;
        speed = 1;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
    }

    public void update() {
        x++;
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
}
