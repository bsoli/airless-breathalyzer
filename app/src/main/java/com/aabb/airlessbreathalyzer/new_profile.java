package com.aabb.airlessbreathalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class new_profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        Button b = (Button) findViewById(R.id.button);
        /*
        gets the data from the UI
        StreamWriters to write to local storage
        returns to the original activity
         */
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String thisProfile = getProfileData();
                    FileOutputStream fos = openFileOutput(getString(R.string.filename), MODE_APPEND);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write(thisProfile);
                    osw.flush();
                    osw.close();
                    fos.close();
                } catch (Exception e) {
                    Snackbar.make(view, "Profile Not Created", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    e.printStackTrace();
                }
                Snackbar.make(view, "Profile Created!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent myIntent = new Intent(getBaseContext(), pick_profile.class);
                startActivity(myIntent);
            }
        });
    }

    private String getProfileData() {
        String name =((EditText) findViewById(R.id.name)).getText().toString();
        String age = ((EditText) findViewById(R.id.age)).getText().toString();
        String weight = ((EditText) findViewById(R.id.myweight)).getText().toString();
        String sex = ((Spinner) findViewById(R.id.SexType)).getSelectedItem().toString();

        return name + "`" + age + "`" + weight + "`" + "`" + sex + "\n";

    }
}

