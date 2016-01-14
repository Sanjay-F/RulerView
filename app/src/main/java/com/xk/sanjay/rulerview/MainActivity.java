package com.xk.sanjay.rulerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.xk.sanjay.rulberview.RulerWheel;

public class MainActivity extends AppCompatActivity {


    private RulerWheel rulerView;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rulerView = (RulerWheel) findViewById(R.id.ruler_view);
        rulerView.setScrollingListener(new RulerWheel.OnWheelScrollListener() {
            @Override
            public void onChanged(RulerWheel wheel, int oldValue, int newValue) {
                Log.e(TAG, "curValue=" + newValue);
            }

            @Override
            public void onScrollingStarted(RulerWheel wheel) {

            }

            @Override
            public void onScrollingFinished(RulerWheel wheel) {

            }
        });

    }


}
