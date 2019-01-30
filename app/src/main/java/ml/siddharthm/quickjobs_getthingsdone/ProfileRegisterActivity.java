package ml.siddharthm.quickjobs_getthingsdone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

public class ProfileRegisterActivity extends AppCompatActivity {

    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_register);

        myToolbar = findViewById(R.id.my_profile_toolbar);
        if (myToolbar!=null) {
            setSupportActionBar(myToolbar);
            getSupportActionBar().setTitle("Register");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            myToolbar.setNavigationIcon(R.drawable.back_btn);
            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

        }
    }
}
