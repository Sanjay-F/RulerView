 
# 介绍
##模式1

![](art/demo.png)

##模式2

![UpModel_HalfModel](art/demo_up_half.png)

这是一个水平的滚轮支持插件

#特性

1. 支持定义字体的大小和颜色和是否显示刻度值
2. 支持定义各个刻度的颜色，目前有大,中,小三者，如图片所示，有最大的是黑色，中等的为绿色，最小的是蓝色。同时刻度间隔和刻度的大小可以调节。
3. 支持自定义中间的指标器。
4. 可以有上和下，两种对齐的方式。
5. 支持指定默认值。
6. 支持设置最大和最小值。
7. 两种刻度格式SCALE和HALF，前者为有10格刻度，后者为只有0.5一格小的刻度。参考图片


# 不支持的

1.现在还在加刻度尺的渐显效果

 

# 使用方法

## XML
    <com.xk.sanjay.rulberview.RulerWheel
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:alignMode="DOWN"
            app:def_value="40"
            app:lineColorMax="#000000"
            app:lineColorMid="#00aa00"
            app:lineColorMin="#0e27ca"
            app:line_divider="10dp"
            app:max_value="100"
            app:min_value="20"
            app:mode="HALF"
            app:scaleWidth="1dp"
            app:showScaleValue="true"
            app:text_Size="30"/>
            
##  Activity

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
            
# 鸣谢
特别感谢[獨行Zz](http://blog.csdn.net/dashu8193058/article/details/45846189)，这个项目是在他基础上改的。

还有需要别的欢迎在这个基础自己定制