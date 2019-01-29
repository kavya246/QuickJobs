package ml.siddharthm.quickjobs_getthingsdone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import ml.siddharthm.quickjobs_getthingsdone.Adapters.SliderAdapter;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private Button buttonPrevious, buttonNext;

    private  int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mSlideViewPager = (ViewPager)findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        buttonNext = findViewById(R.id.btn_next);
        buttonPrevious = findViewById(R.id.btn_previous);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        mDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListner);

        buttonNext.setOnClickListener(this);
        buttonPrevious.setOnClickListener(this);




    }

    private void mDotsIndicator(int position){
        mDots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0 ; i < mDots.length ; i++ ){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light_pressed));
            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0 ){
            mDots[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    ViewPager.OnPageChangeListener viewListner = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mDotsIndicator(position);
            mCurrentPage = position;

            if (position == 0){
                buttonNext.setEnabled(true);
                buttonPrevious.setEnabled(false);
                buttonPrevious.setVisibility(View.INVISIBLE);

                buttonNext.setText("Next");
                buttonPrevious.setText("");
            } else if (position == mDots.length - 1){
                buttonNext.setEnabled(true);
                buttonPrevious.setEnabled(true);
                buttonPrevious.setVisibility(View.VISIBLE);

                buttonNext.setText("Finish");
                buttonPrevious.setText("Back");
            } else {
                buttonNext.setEnabled(true);
                buttonPrevious.setEnabled(true);
                buttonPrevious.setVisibility(View.VISIBLE);

                buttonNext.setText("Next");
                buttonPrevious.setText("Back");
            }



        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_next:
                if (buttonNext.getText().toString().equalsIgnoreCase("next")){
                    mSlideViewPager.setCurrentItem(mCurrentPage + 1);
                } else {
                    startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                }
                break;
            case R.id.btn_previous:
                mSlideViewPager.setCurrentItem(mCurrentPage - 1);
                break;
            default: break;
        }
    }
}


