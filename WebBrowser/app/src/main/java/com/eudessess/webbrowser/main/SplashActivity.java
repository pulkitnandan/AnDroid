package com.eudessess.webbrowser.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.eudessess.webbrowser.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread thread = new Thread() {
            public void run() {
                try {
                    TextView tv = (TextView) findViewById(R.id.tv1);
                    SharedPreferences gotPrefs = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());
                    String pref = gotPrefs.getString("start", "User");
                    tv.setText("Welcome " + pref);
                    tv.setBackgroundColor(Color.BLUE);
                    sleep(1000);
                } catch (InterruptedException ix) {
                    ix.printStackTrace();
                } finally {
                    Intent startCounterActivity = new Intent(
                            "com.eudessess.webbrowser.main.browseractivity");
                    startActivity(startCounterActivity);
                }
            }
        };
        thread.start();
    }

    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // startup.release();
        finish();
    }

}
