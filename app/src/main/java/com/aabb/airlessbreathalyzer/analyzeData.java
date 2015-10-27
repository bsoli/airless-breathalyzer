package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class analyzeData extends AppCompatActivity {

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_data);

        //get the profile
        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));
        //analyze

    }
}
