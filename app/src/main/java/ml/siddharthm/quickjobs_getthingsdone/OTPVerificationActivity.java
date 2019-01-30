package ml.siddharthm.quickjobs_getthingsdone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OTPVerificationActivity extends AppCompatActivity {
    private TextView phonenumber;
    private Button gonext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        phonenumber = (TextView)findViewById(R.id.phone_otp);

        final String phone = phonenumber.getText().toString();

        gonext = (Button)findViewById(R.id.gonext);
        gonext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OTPVerificationActivity.this, ProfileRegisterActivity.class);
                i.putExtra("phonenumber", phone);
                startActivity(i);
            }
        });




    }
}
