package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class shortTermMemoryTestFinish extends AppCompatActivity {

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_term_memory_test_finish);

        //get the profile
        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));

        //start new activity and send the profile
        Intent myIntent = new Intent(getBaseContext(), analyzeData.class);
        Bundle newBundle = new Bundle();
        bundle.putParcelable(getString(R.string.profile), profile);
        myIntent.putExtras(bundle);
        startActivity(myIntent);
    }
}

