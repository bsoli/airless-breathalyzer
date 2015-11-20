package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class mathTest extends AppCompatActivity {
    private final String tag = "MathActivity";

    private Profile profile;
    private TextView question;
    private Button answer1;
    private Button answer2;
    private Button answer3;
    private long startTime;
    private int times = 0;
    private int questions = 4;
    private String correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_test);

        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));

        startTime = System.currentTimeMillis();

        question = (TextView) findViewById(R.id.question);
        answer1 = (Button) findViewById(R.id.answer1);
        answer2 = (Button) findViewById(R.id.answer2);
        answer3 = (Button) findViewById(R.id.answer3);

        populateView();
    }

    public void populateView() {
        String[] questionAndAnswers = generateQuestionAndAnswers();
        //String[] randomizedQuestions = randomizeQuestions(Arrays.copyOfRange(questionAndAnswers, 1, 4));
        final ArrayList<String> randomizedQuestions = new ArrayList<String>();
        correctAnswer = questionAndAnswers[1];
        randomizedQuestions.add(questionAndAnswers[1]);
        randomizedQuestions.add(questionAndAnswers[2]);
        randomizedQuestions.add(questionAndAnswers[3]);
        Collections.shuffle(randomizedQuestions);

        for(int i = 0; i < questionAndAnswers.length; i++) {
            Log.d(tag, questionAndAnswers[i]);
        }
        for(int i = 0; i < randomizedQuestions.size(); i++) {
            Log.d(tag, randomizedQuestions.get(i));
        }

        question.setText(questionAndAnswers[0]);
        answer1.setText(randomizedQuestions.get(0));
        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wasClicked(randomizedQuestions.get(0));
            }
        });
        answer2.setText(randomizedQuestions.get(1));
        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wasClicked(randomizedQuestions.get(1));
            }
        });
        answer3.setText(randomizedQuestions.get(2));
        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wasClicked(randomizedQuestions.get(2));
            }
        });
    }

    private void wasClicked(String attemptedAnswer) {
        times++;
        if (correctAnswer.equals(attemptedAnswer)) {
            questions--;
        }
        if (times < 3) {
            populateView();
        }
        else {
            long endTime = System.currentTimeMillis();
            double score = (((endTime-startTime)/1000)/3) * questions;
            profile.mathScore = score;
            Intent myIntent = new Intent(getBaseContext(), shortTermMemoryTestFinish.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.profile), profile);
            myIntent.putExtras(bundle);
            startActivity(myIntent);
        }
    }

    private String[] generateQuestionAndAnswers() {
        Random r = new Random();
        int num1 = r.nextInt(15) + 2;
        int num2 = r.nextInt(15) + 2;
        int ans;
        String question;
        if (num1 % num2 == 0 && num1 != num2) {
            ans = num1/num2;
            question = num1 + " / " + num2;
        }
        else if (num2 % num1 == 0 && num1 != num2) {
            ans = num2/num1;
            question = num2 + " / " + num1;
        }
        else {
            ans = num2 * num1;
            question = num1 + " X " + num2;
        }

        int mod1, mod2;
        while (true) {
            int attempt = r.nextInt(4) - 2;
            if (attempt != 0) {
                mod1 = attempt;
                break;
            }
        }
        while (true) {
            int attempt = r.nextInt(4) - 2;
            if (attempt != 0 && attempt != mod1) {
                mod2 = attempt;
                break;
            }
        }

        int wrong1 = ans + mod1;
        int wrong2 = ans + mod2;

        String[] QandA = new String[4];
        QandA[0] = question;
        QandA[1] = String.valueOf(ans);
        QandA[2] = String.valueOf(wrong1);
        QandA[3] = String.valueOf(wrong2);

        return QandA;
    }
}