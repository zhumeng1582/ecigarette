package com.industio.ecigarette.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.industio.ecigarette.ui.MainActivity;


public class InterceptLinearLayout extends LinearLayout {

    private long lastTime;

    public InterceptLinearLayout(Context context) {
        super(context);
    }

    public InterceptLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InterceptLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private boolean disableAllClick() {
        //不等于0，表示在抽烟，需要禁用点击操作
        return MainActivity.currentTime != 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!disableAllClick()) {
            return super.dispatchTouchEvent(ev);
        } else {
            if (TimeUtils.getNowMills() - lastTime > 5000) {
                lastTime = TimeUtils.getNowMills();
                ToastUtils.showShort("抽烟模式，禁止修改");
            }
            return true;
        }
    }
}
