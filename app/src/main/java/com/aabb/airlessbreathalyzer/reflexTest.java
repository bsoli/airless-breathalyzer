package com.aabb.airlessbreathalyzer;


import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.TextView;

public class reflexTest extends AppCompatActivity {

    private int numRuns = 5;
    private int countRuns = 0;
    private long totalTime = 0;
    private long totalDelay = 0;

    private TextView timer;
    private TextView totalTimer;
    private long startTime = 0;
    private int height = 0;
    private int width = 0;
    private float bufferY = 310;//TODO Fix this for multiple platforms
    private float bufferX = 10;//TODO Fix this for multiple platforms
    private ImageButton button;

    private boolean buttonWasClicked;

    private Profile profile;

    //runs without a timer by reposting this handler at the end of the runnable
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            button.setVisibility(View.VISIBLE);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflex_test);
        //Log.d(tag, "on create called");

        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));

        buttonWasClicked = false;

        button = (ImageButton) findViewById(R.id.button);
        timer = (TextView) findViewById(R.id.timer);
        //totalTimer = (TextView) findViewById(R.id.totalTime);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        startTime = System.currentTimeMillis();
    }

    public void buttonClicked(View view) {
        if (countRuns < numRuns) {
            timerHandler.removeCallbacks(timerRunnable);
            button.setVisibility(View.INVISIBLE);
            totalTime += (System.currentTimeMillis() - startTime);
            String s = "drink2";
            switch (countRuns) {
                case 0 : button.setImageResource(R.drawable.drink2);
                    break;
                case 1 : button.setImageResource(R.drawable.drink3);
                    break;
                case 2 : button.setImageResource(R.drawable.drink4);
                    break;
                case 3 : button.setImageResource(R.drawable.drink5);
                    break;
                case 4 : button.setImageResource(R.drawable.drink6);
                    break;
            }


            int m = (int) totalTime % 1000;
            int seconds = (int) (totalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            //totalTimer.setText(String.format("%d:%02d:%03d", minutes, seconds, m));

            long randDelay = (int) (Math.random() * 3000);
            totalDelay += randDelay;
            startTime = System.currentTimeMillis() + randDelay;
            moveButton();
            timerHandler.postDelayed(timerRunnable, randDelay);

            countRuns++;
        } else {
            timerHandler.removeCallbacks(timerRunnable);
            double score = ((totalTime)/1000)/numRuns;
            profile.reflexScore = score;
            Intent myIntent = new Intent(getBaseContext(), mathTest.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.profile), profile);
            myIntent.putExtras(bundle);
            startActivity(myIntent);
        }
    }

    public void moveButton() {
       // Log.v(tag, "Width: " + width);
        float newX = (float) Math.random() * (width - (button.getWidth() + bufferX));
        //Log.v(tag, "newX: " + newX);
        //Log.v(tag, "Height: " + height);
        float newY = (float) Math.random() * (height - (button.getHeight() + bufferY));
        //Log.v(tag, "newY: " + newY);
        button.setX(newX);
        button.setY(newY);
    }
}