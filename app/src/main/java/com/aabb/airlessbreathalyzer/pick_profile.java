package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.aabb.airlessbreathalyzer.R.id.Start;
import static com.aabb.airlessbreathalyzer.R.id.delete;
import static com.aabb.airlessbreathalyzer.R.id.imageButton;
import static com.aabb.airlessbreathalyzer.R.id.newProfile;

public class pick_profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<String> spinnerArray = new ArrayList<>();
        try {
            spinnerArray = populateNames();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spinner);
        sItems.setAdapter(adapter);

        Button start = (Button) findViewById(Start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here we should probs create a profile object so that it is easier to use
                Intent myIntent = new Intent(getBaseContext(), null); //this will lead to the command manager. will also need to send the profile 'Object'
                startActivity(myIntent);
            }
        });

        Button newProf = (Button) findViewById(newProfile);
        newProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getBaseContext(), new_profile.class);
                startActivity(myIntent);
            }
        });

        Button deleter = (Button) findViewById(delete);
        deleter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFile(getString(R.string.filename));
                Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(myIntent);
            }
        });
    }

    private List<String> populateNames() throws Exception {
        FileInputStream in = openFileInput(getString(R.string.filename));
        InputStreamReader stream = new InputStreamReader(in);
        BufferedReader read = new BufferedReader(stream);

        List<String> names = new ArrayList<>();
        String next;
        while ((next = read.readLine()) != null) {
            String[] line = next.split("`");
            names.add(line[0]);
        }
        return names;
    }



}
