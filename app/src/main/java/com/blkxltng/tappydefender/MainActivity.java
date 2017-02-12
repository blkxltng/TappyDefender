package com.blkxltng.tappydefender;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get ready to load fastest time
        SharedPreferences mPreferences;
        SharedPreferences.Editor mEditor;
        mPreferences = getSharedPreferences("HighScores", MODE_PRIVATE);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
//        // Remember that you should never show the action bar if the
//        // status bar is hidden, so hide that too if necessary.
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();

        final Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(this);

        final TextView textFastestTime = (TextView) findViewById(R.id.textHighScore);
        long fastestTime = mPreferences.getLong("fastestTime", 10000000);
        textFastestTime.setText("Fastest Time: " + fastestTime);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.buttonPlay:
                Intent i = new Intent(this, GameActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }
}
