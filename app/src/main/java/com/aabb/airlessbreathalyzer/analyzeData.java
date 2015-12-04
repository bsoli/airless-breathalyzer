package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class analyzeData extends AppCompatActivity {

    private Profile profile;
    private double reflexWeight = .5;
    private double mathWeight = .5;
    private double memWeight = .5;
    private double femaleMultiplier = .8;
    private double weightModifier = .008;
    private double ageModifier = .01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_data);

        //get the profile
        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));
        String score = getScore(profile);

        TextView scoreview = (TextView) findViewById(R.id.finalScore);
        String result = R.string.resultText1 + score + R.string.resultText2;
        scoreview.setText(score);

        TextView ref = (TextView) findViewById(R.id.reflex);
        ref.setText("reflexScore: " + String.valueOf(profile.reflexScore));

        TextView math = (TextView) findViewById(R.id.math);
        math.setText("mathScore: " + String.valueOf(profile.mathScore));

        TextView mem = (TextView) findViewById(R.id.mem);
        mem.setText("memScore: " + String.valueOf(profile.memScore));

        Button home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getBaseContext(), pick_profile.class);
                startActivity(myIntent);
            }
        });
    }

    private String getScore(Profile profile) {
        double sexModifier = 1;
        if (profile.sex.equals("Female")) {
            sexModifier = femaleMultiplier;
        }

        double testScore = ((reflexWeight * profile.reflexScore) + (mathWeight * profile.mathScore)
                + (memWeight * profile.memScore));
        double score = testScore * sexModifier;
        score = score * weightModifier * profile.weight;
        score = score * ageModifier * profile.age;
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        String res = df.format(score);
        return res;
    }
}
