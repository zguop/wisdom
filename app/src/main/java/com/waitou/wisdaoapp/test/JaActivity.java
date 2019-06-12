package com.waitou.wisdaoapp.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.waitou.wisdaoapp.R;

/**
 * auth aboom
 * date 2019-06-06
 */
public class JaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ja);
        getSupportFragmentManager().beginTransaction().add(R.id.frame, new ListFragment(),"111").commitAllowingStateLoss();
    }
}
