package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class analyzeData extends AppCompatActivity {

    private Profile profile;
    private double reflexWeight = 1;
    private double mathWeight = 1;
    private double memWeight = 1;
    private double maleMultiplier = 1;
    private double femaleMultiplier = 1;
    private double weightModifier = .01;
    private double ageModifier = .01;
    private double whiteMultiplier = 1;
    private double blackMultiplier = 1;
    private double asianMultiplier = 1;
    private double hispanicMultiplier = 1;
    private double middleEasternMultiplier = 1;
    private double otherMultiplier = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_data);

        //get the profile
        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));
        double score = getScore(profile);

        TextView scoreview = (TextView) findViewById(R.id.finalScore);
        scoreview.setText(String.valueOf(score));

        TextView race = (TextView) findViewById(R.id.race);
        race.setText(profile.race);

        TextView weight = (TextView) findViewById(R.id.weight);
        weight.setText(profile.weight);

        TextView sex = (TextView) findViewById(R.id.sex);
        sex.setText(profile.sex);

        TextView age = (TextView) findViewById(R.id.age);
        age.setText(profile.age);

        TextView ref = (TextView) findViewById(R.id.reflex);
        ref.setText(profile.reflexScore);

        TextView math = (TextView) findViewById(R.id.math);
        math.setText(String.valueOf(profile.mathScore));

        TextView mem = (TextView) findViewById(R.id.mem);
        mem.setText(profile.memScore);

    }

    private double getScore(Profile profile) {
        double sexModifier, raceModifier = 1;
        if (profile.sex.equals("Male")) {
            sexModifier = maleMultiplier;
        } else {
            sexModifier = femaleMultiplier;
        }
        switch (profile.race) {
            case "White":
                raceModifier = whiteMultiplier;
                break;
            case "Black":
                raceModifier = blackMultiplier;
                break;
            case "Asian":
                raceModifier = asianMultiplier;
                break;
            case "Hispanic":
                raceModifier = hispanicMultiplier;
                break;
            case "Middle Eastern":
                raceModifier = middleEasternMultiplier;
                break;
            case "Other":
                raceModifier = otherMultiplier;
        }

        double testScore = ((reflexWeight * profile.reflexScore) + (mathWeight * profile.mathScore)
                + (memWeight * profile.memScore))/3;
        double score = testScore * raceModifier * sexModifier * weightModifier * profile.weight * ageModifier * profile.age;
        return score;
    }
}
