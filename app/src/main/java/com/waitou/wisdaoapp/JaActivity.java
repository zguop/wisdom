package com.waitou.wisdaoapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * auth aboom
 * date 2019-06-06
 */
public class JaActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ja);
        findViewById(R.id.firstSharedView).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, AnimActivity.class);

        Bundle sharedView = ActivityOptions.makeSceneTransitionAnimation(this, v, "sharedView").toBundle();
        Log.e("aa" , " sharedView " + sharedView );
        startActivity(intent,sharedView);

    }
}
