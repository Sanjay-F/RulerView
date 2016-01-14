package com.xk.sanjay.rulerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.xk.sanjay.rulberview.RulerWheel;

public class MainActivity extends AppCompatActivity {


    private RulerWheel rulerView;
    private String TAG = this.getClass().getSimpleName();

    private TextView tvCurValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCurValue = (TextView) findViewById(R.id.curValue_tv);
        rulerView = (RulerWheel) findViewById(R.id.ruler_view);
        rulerView.setScrollingListener(new RulerWheel.OnWheelScrollListener() {
            @Override
            public void onChanged(RulerWheel wheel, int oldValue, final int newValue) {
                tvCurValue.setText(newValue+"");
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
