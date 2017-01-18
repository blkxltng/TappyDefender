package com.blkxltng.tappydefender;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;

public class GameActivity extends AppCompatActivity {

    private TDView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get a display object to access screen details
        Display mDisplay = getWindowManager().getDefaultDisplay();
        //Load the resolution into a point object
        Point size = new Point();
        mDisplay.getSize(size);

        gameView = new TDView(this, size.x, size.y);
        setContentView(gameView);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    //Pause the thread if the activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    //Resume the thread if the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

}
