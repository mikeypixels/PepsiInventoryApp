package com.example.michael.pepsiinventory;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.gson.Gson;

public class PopUpActivity extends Activity {

    Animation fromsmall,fromnothing;
    ConstraintLayout constraintLayout;
    SalesRow salesRow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);

        String sale = getIntent().getStringExtra("sale");
        salesRow = new Gson().fromJson("sale",SalesRow.class);



        fromnothing = AnimationUtils.loadAnimation(this,R.anim.fromnothing);
        fromsmall = AnimationUtils.loadAnimation(this,R.anim.fromsmall);
        constraintLayout = findViewById(R.id.const_layout);

        constraintLayout.setAnimation(fromsmall);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));


    }
}
