package ml.siddharthm.quickjobs_getthingsdone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {

    private static int Splash_Screen_Time = 3000;
    private Boolean isFirstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        isFirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isfirstrun",true);

        if (isFirstRun){
            getSharedPreferences("PREFERENCE",MODE_PRIVATE).edit().putBoolean("isfirstrun",false).commit();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent welcomeIntent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                    startActivity(welcomeIntent);
                    finish();
                }
            },Splash_Screen_Time);
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent loginIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            },Splash_Screen_Time);


        }

    }
}
