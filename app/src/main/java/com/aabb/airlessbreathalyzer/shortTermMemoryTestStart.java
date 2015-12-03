package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    private TextView t1, t2, t3, timer;

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_term_memory_test_start);

        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));

        t1 = (TextView) findViewById(R.id.textView);
        t2 = (TextView) findViewById(R.id.textView2);
        t3 = (TextView) findViewById(R.id.textView3);

        timer = (TextView) findViewById(R.id.timer);

        populateWords();

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                startTest();
            }
        }.start();
    }

    private void populateWords() {
        String[] words = getResources().getStringArray(R.array.Wordlist);
        String [] memory =  new String[3];
        int i = 0;
        Random rand = new Random();
        while( i < 3) {
            int index = rand.nextInt(words.length);
            if(!Arrays.asList(memory).contains(words[index])){
                memory[i] = words[index];
                i++;
            }
        }
        profile.word1 = memory[0];
        profile.word2 = memory[1];
        profile.word3 = memory[2];
        t1.setText(memory[0]);
        t2.setText(memory[1]);
        t3.setText(memory[2]);
    }

    public void startTest() {
        this.setContentView(R.layout.pre_reflex);
        new CountDownTimer(5000, 1000) {
            final TextView timer2 = (TextView) findViewById(R.id.timer2);

            public void onTick(long millisUntilFinished) {
                timer2.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                Intent myIntent = new Intent(getBaseContext(), reflexTest.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(getString(R.string.profile), profile);
                myIntent.putExtras(bundle);
                startActivity(myIntent);
            }
        }.start();

    }
}