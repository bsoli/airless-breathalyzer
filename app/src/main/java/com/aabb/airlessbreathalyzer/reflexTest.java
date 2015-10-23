package com.aabb.airlessbreathalyzer;


import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class reflexTest extends AppCompatActivity {

    private int numRuns = 20;
    private int countRuns = 0;
    private long totalTime = 0;

    private TextView timer;
    private TextView totalTimer;
    private long startTime = 0;
    private int height = 0;
    private int width = 0;
    private float bufferY = 310;//TODO Fix this for multiple platforms
    private float bufferX = 10;//TODO Fix this for multiple platforms
    private String tag = "Reflex";
    private Button button;


    //runs without a timer by reposting this handler at the end of the runnable
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            button.setVisibility(View.VISIBLE);

            long millis = System.currentTimeMillis() - startTime;
            int m = (int) millis % 1000;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timer.setText(String.format("%d:%02d:%03d", minutes, seconds, m));

            timerHandler.postDelayed(this, 3);

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflex_test);

        button = (Button) findViewById(R.id.button);
        timer = (TextView) findViewById(R.id.timer);
        totalTimer = (TextView) findViewById(R.id.totalTime);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        startTime = System.currentTimeMillis();

        timerHandler.postDelayed(timerRunnable, 0);

    }

    public void buttonClicked(View view) {
        if (countRuns < numRuns) {
            timerHandler.removeCallbacks(timerRunnable);
            button.setVisibility(View.INVISIBLE);
            totalTime += (System.currentTimeMillis() - startTime);

            int m = (int) totalTime % 1000;
            int seconds = (int) (totalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            totalTimer.setText(String.format("%d:%02d:%03d", minutes, seconds, m));

            long randDelay = (int) (Math.random() * 3000);

            startTime = System.currentTimeMillis() + randDelay;
            moveButton();
            timerHandler.postDelayed(timerRunnable, randDelay);
            countRuns++;
        } else {
            //TODO Move to next test
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    public void moveButton() {
        Log.v(tag, "Width: " + width);
        float newX = (float) Math.random() * (width - (button.getWidth() + bufferX));
        Log.v(tag, "newX: " + newX);
        Log.v(tag, "Height: " + height);
        float newY = (float) Math.random() * (height - (button.getHeight() + bufferY));
        Log.v(tag, "newY: " + newY);
        button.setX(newX);
        button.setY(newY);
    }
}






















