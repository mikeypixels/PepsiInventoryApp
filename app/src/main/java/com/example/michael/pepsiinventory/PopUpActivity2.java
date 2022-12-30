package com.example.michael.pepsiinventory;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class PopUpActivity2 extends AppCompatActivity {

    Animation fromsmall,fromnothing;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up2);

        constraintLayout = findViewById(R.id.const_layout2);

        fromnothing = AnimationUtils.loadAnimation(this,R.anim.fromnothing);
        fromsmall = AnimationUtils.loadAnimation(this,R.anim.fromsmall);

        constraintLayout.setAnimation(fromsmall);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.7));
    }
}
