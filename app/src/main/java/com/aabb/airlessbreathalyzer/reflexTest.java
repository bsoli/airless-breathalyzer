package com.aabb.airlessbreathalyzer;


import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    private long startTime = 0;
    private int height = 0;
    private int width = 0;
    private float bufferY = 310;//TODO Fix this for multiple platforms
    private float bufferX = 10;//TODO Fix this for multiple platforms
    private ImageButton button;

    private Profile profile;

    /*
    only thing that we use the threads for is setting the button to visible after a delay
     */
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

        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));

        button = (ImageButton) findViewById(R.id.button);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        startTime = System.currentTimeMillis();
    }

    /*
    hide button
    update the total time
    set the button image
    move the button
    tell it to reappear using a runnable thread
     */
    public void buttonClicked(View view) {
        if (countRuns < numRuns) {
            timerHandler.removeCallbacks(timerRunnable);
            button.setVisibility(View.INVISIBLE);
            totalTime += (System.currentTimeMillis() - startTime);
            switch (countRuns) {
                case 0:
                    button.setImageResource(R.drawable.drink2);
                    break;
                case 1:
                    button.setImageResource(R.drawable.drink3);
                    break;
                case 2:
                    button.setImageResource(R.drawable.drink4);
                    break;
                case 3:
                    button.setImageResource(R.drawable.drink5);
                    break;
                case 4:
                    button.setImageResource(R.drawable.drink6);
                    break;
            }

            long randDelay = (int) (Math.random() * 3000);
            totalDelay += randDelay;
            startTime = System.currentTimeMillis() + randDelay;
            moveButton();
            timerHandler.postDelayed(timerRunnable, randDelay);

            countRuns++;
        } else {
            timerHandler.removeCallbacks(timerRunnable);
            double score = (((totalTime) / 1000.0) / 6.0) * .01;
            profile.reflexScore = score;
            startTest();
        }
    }

    /*
    moves button using math
     */
    public void moveButton() {
        float newX = (float) Math.random() * (width - (button.getWidth() + bufferX));
        float newY = (float) Math.random() * (height - (button.getHeight() + bufferY));
        button.setX(newX);
        button.setY(newY);
    }

    public void startTest() {
        this.setContentView(R.layout.pre_bal);
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Intent myIntent = new Intent(getBaseContext(), LevelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(getString(R.string.profile), profile);
                myIntent.putExtras(bundle);
                startActivity(myIntent);
            }
        }.start();

    }
}