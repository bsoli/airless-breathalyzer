package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class analyzeData extends AppCompatActivity {

    private Profile profile;
    private double reflexWeight = .1;
    private double mathWeight = .1;
    private double memWeight = .1;
    private double maleMultiplier = 1;
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
        double score = getScore(profile);

        TextView scoreview = (TextView) findViewById(R.id.finalScore);
        scoreview.setText("total: " + String.valueOf(score));

        TextView weight = (TextView) findViewById(R.id.thisweight);
        weight.setText("pounds: " + String.valueOf(profile.weight));

        TextView sex = (TextView) findViewById(R.id.sex);
        sex.setText("genitals: " + profile.sex);

        TextView age = (TextView) findViewById(R.id.age);
        age.setText("old-ness: " + String.valueOf(profile.age));

        TextView ref = (TextView) findViewById(R.id.reflex);
        ref.setText("reflexScore: " + String.valueOf(profile.reflexScore));

        TextView math = (TextView) findViewById(R.id.math);
        math.setText("mathScore: " + String.valueOf(profile.mathScore));

        TextView mem = (TextView) findViewById(R.id.mem);
        mem.setText("memScore: " + String.valueOf(profile.memScore));
    }

    private double getScore(Profile profile) {
        double sexModifier = 1;
        if (profile.sex.equals("Male")) {
            sexModifier = maleMultiplier;
        } else {
            sexModifier = femaleMultiplier;
        }

        double testScore = ((reflexWeight * profile.reflexScore) + (mathWeight * profile.mathScore)
                + (memWeight * profile.memScore))/3;
        double score = testScore * sexModifier * weightModifier * profile.weight * ageModifier * profile.age;
        return score;
    }
}
