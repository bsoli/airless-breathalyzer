package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class mathTest extends AppCompatActivity {

    private Profile profile;
    private long startTime = System.currentTimeMillis();
    private int times = 0;
    private int questions = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_test);

        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));

        final String[] questionAndAnswers = generateQuestionAndAnswers();

        TextView question = (TextView) findViewById(R.id.question);
        Button answer1 = (Button) findViewById(R.id.answer1);
        Button answer2 = (Button) findViewById(R.id.answer2);
        Button answer3 = (Button) findViewById(R.id.answer3);

        final String[] randomizedQuestions = randomizeQuestions(Arrays.copyOfRange(questionAndAnswers, 1,4));

        question.setText(questionAndAnswers[0]);
        answer1.setText(randomizedQuestions[0]);
        answer2.setText(randomizedQuestions[1]);
        answer3.setText(randomizedQuestions[2]);

        answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wasClicked(questionAndAnswers[1], randomizedQuestions[0]);
            }
        });

        answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wasClicked(questionAndAnswers[1], randomizedQuestions[1]);
            }
        });

        answer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wasClicked(questionAndAnswers[1], randomizedQuestions[2]);
            }
        });
    }

    private void wasClicked(String correctAnswer, String attemptedAnswer) {
        if (correctAnswer.equals(attemptedAnswer)) {
            questions--;
        }
        if (times < 3) {

        }
        else {
            long endTime = System.currentTimeMillis();
            double score = ((startTime-endTime)/3) * questions;
            profile.mathScore = score;
            Intent myIntent = new Intent(getBaseContext(), shortTermMemoryTestFinish.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.profile), profile);
            myIntent.putExtras(bundle);
            startActivity(myIntent);
        }
    }

    private String[] randomizeQuestions(String[] questions) {
        String[] newQuestions = new String[3];
        ArrayList<Integer> newIndices = new ArrayList<>();
        Random r = new Random();
        while(newIndices.size() < 3) {
            int newIndex = r.nextInt(3);
            if (!newIndices.contains(newIndex)) {
                newQuestions[newIndex] = questions[newIndex];
                newIndices.add(newIndex);
            }
        }
        return newQuestions;
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
