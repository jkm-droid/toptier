package jkmdroid.toptier;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_screen);


        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //moving to another activity
                Intent registerActivity = new Intent(LauncherScreen.this, UserActivity.class);
                startActivity(registerActivity);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
