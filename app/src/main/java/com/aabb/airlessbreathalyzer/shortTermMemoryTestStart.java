package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.UserDictionary;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

public class shortTermMemoryTestStart extends AppCompatActivity {
    private TextView t1, t2, t3, t4, t5, timer;

    private long startTime;

    private Profile profile;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            long millis = System.currentTimeMillis() - startTime;
            int m = (int) millis % 1000;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timer.setText(String.format("%d", 5-seconds));

            timerHandler.postDelayed(this, 3);

            if(seconds == 5) {
                startTest();
            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_term_memory_test_start);

        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));

        t1 = (TextView) findViewById(R.id.textView);
        t2 = (TextView) findViewById(R.id.textView2);
        t3 = (TextView) findViewById(R.id.textView3);
        t4 = (TextView) findViewById(R.id.textView4);
        t5 = (TextView) findViewById(R.id.textView5);

        timer = (TextView) findViewById(R.id.timer);

        populateWords();

        startTime = System.currentTimeMillis();

        timerHandler.postDelayed(timerRunnable, 0);

    }

    private void populateWords() {
        String[] words = getResources().getStringArray(R.array.Wordlist);
        String [] memory =  new String[5];
        int i = 0;
        Random rand = new Random();
        while( i < 5) {
            int index = rand.nextInt(words.length);
            if(!Arrays.asList(memory).contains(words[index])){
                memory[i] = words[index];
                i++;
            }
        }
        t1.setText(memory[0]);
        t2.setText(memory[1]);
        t3.setText(memory[2]);
        t4.setText(memory[3]);
        t5.setText(memory[4]);
    }

    public void startTest() {
        Intent myIntent = new Intent(getBaseContext(), reflexTest.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.profile), profile);
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }
}