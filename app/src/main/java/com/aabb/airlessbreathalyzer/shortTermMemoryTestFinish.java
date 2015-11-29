package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class shortTermMemoryTestFinish extends AppCompatActivity {

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_term_memory_test_finish);

        //get the profile
        final Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));

        final EditText word1 = (EditText) findViewById(R.id.word1);
        final EditText word2 = (EditText) findViewById(R.id.word2);
        final EditText word3 = (EditText) findViewById(R.id.word3);
        final EditText word4 = (EditText) findViewById(R.id.word4);
        final EditText word5 = (EditText) findViewById(R.id.word5);

        Button sub = (Button) findViewById(R.id.button2);

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double score = 0;
                String[] attemptedWords = {word1.getText().toString(), word2.getText().toString(),
                        word3.getText().toString(), word4.getText().toString(),
                        word5.getText().toString()};
                String[] correctWords = {profile.word1, profile.word2, profile.word3,
                        profile.word4, profile.word5};
                boolean foundMatch = false;
                for (String attempt : attemptedWords) {
                    for (String correct : correctWords) {
                        if (attempt.equals(correct)) {
                            foundMatch = true;
                        }
                        if (foundMatch) {
                            score += .1;
                            break;
                        }
                    }
                }

                for (int i = 0; i < attemptedWords.length; i++) {
                    if (attemptedWords[i].equals(correctWords[i]))
                        score += .3;
                }

                profile.memScore = 2 - score;
                //start new activity and send the profile
                Intent myIntent = new Intent(getBaseContext(), analyzeData.class);
                Bundle newBundle = new Bundle();
                newBundle.putParcelable(getString(R.string.profile), profile);
                myIntent.putExtras(newBundle);
                startActivity(myIntent);
            }
        });





    }
}
