package com.a2bsystem.vente.Activities.config;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.a2bsystem.vente.R;

public class Config extends AppCompatActivity {

    public static final String Config = "Config";
    public static final String url = "URL";
    public static final String bdd = "BDD";
    public static final String foretagkod = "Foretagkod";
    SharedPreferences sharedPreferences;

    private EditText mUrl;
    private EditText mBdd;
    private EditText mForetagkod;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        initFields();
        initListeners();
    }

    public void initFields() {

        mUrl          = findViewById(R.id.config_url);
        mBdd          = findViewById(R.id.config_bdd);
        mForetagkod   = findViewById(R.id.config_foretagkod);

        bottomNavigationView = findViewById(R.id.bottom_navigation_config);

        sharedPreferences = getBaseContext().getSharedPreferences(Config,MODE_PRIVATE);

        //Rempli avec les données existantes
        if(sharedPreferences.contains(url)
                && sharedPreferences.contains(bdd)
                && sharedPreferences.contains(foretagkod)
        )
        {
            mUrl.setText(sharedPreferences.getString(url,null));
            mBdd.setText(sharedPreferences.getString(bdd,null));
            mForetagkod.setText(sharedPreferences.getString(foretagkod,null));
        }
    }

    public void initListeners() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.config_valid_onglet:

                        sharedPreferences
                                .edit()
                                .putString(url,mUrl.getText().toString())
                                .putString(bdd,mBdd.getText().toString())
                                .putString(foretagkod,mForetagkod.getText().toString())
                                .apply();

                        Config.this.finish();

                        break;
                }
                return false;
            }
        });
    }
}