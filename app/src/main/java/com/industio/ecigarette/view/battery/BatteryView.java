package com.industio.ecigarette.view.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.industio.ecigarette.R;

public class BatteryView extends View{
    // 电池方向
    private BatteryViewOrientation orientation;
    // 电池边框与内部电量的间隔
    private float borderPadding;
    // 电池边框宽度
    private float borderWidth;
    // 边框颜色
    private int borderColor;
    // 边框圆角
    private float radis;

    private float headerWidth;

    // 电池电量及对应的颜色
    // 低电量 默认值 0%-10% 红色
    private int lowColor;
    private int lowValue;
    // 中等电量 默认值 11%-20% 黄色
    private int mediumColor;
    private int mediumValue;
    // 高电量 默认值 21%-100% 白色
    private int highColor;
    private int headerColor;
    private int currentPower = 60; // 当前电量

    // 未充电时高电量颜色
    private int noChargingHighColor;

    private int chargingSpeed;

    private int width;
    private int height;
    private Context mContext;

    private Handler mHandler = new Handler();
    private Runnable runnable;

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getAttrs(context, attrs);
        initPaints();
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BatteryView);
        // 默认水平头朝右
        int orientationInt = ta.getInt(R.styleable.BatteryView_bv_orientation, 1);
        switch (orientationInt) {
            case 0:
                orientation = BatteryViewOrientation.HORIZONTAL_LEFT;
                break;
            case 1:
                orientation = BatteryViewOrientation.HORIZONTAL_RIGHT;
                break;
            case 2:
                orientation = BatteryViewOrientation.VERTICAL_TOP;
                break;
            case 3:
                orientation = BatteryViewOrientation.VERTICAL_BOTTOM;
                break;
        }
        // 电池内边距
        borderPadding = ta.getDimensionPixelSize(R.styleable.BatteryView_bv_border_padding, 2);
        // 电池边框厚度
        borderWidth = ta.getDimensionPixelSize(R.styleable.BatteryView_bv_border_width, 2);
        headerWidth = ta.getDimensionPixelSize(R.styleable.BatteryView_bv_header_width, 10);
        // 电池边框圆角
        radis = ta.getDimensionPixelSize(R.styleable.BatteryView_bv_radis, 2);
        // 电池边框颜色
        borderColor = ta.getColor(R.styleable.BatteryView_bv_border_color, Color.WHITE);

        // 电池实心部分
        lowColor = ta.getColor(R.styleable.BatteryView_bv_power_color_low, mContext.getResources().getColor(R.color.low));
        lowValue = ta.getInt(R.styleable.BatteryView_bv_power_value_low, 10);
        mediumColor = ta.getColor(R.styleable.BatteryView_bv_power_color_medium, mContext.getResources().getColor(R.color.medium));
        mediumValue = ta.getInt(R.styleable.BatteryView_bv_power_value_medium, 20);
        highColor = ta.getColor(R.styleable.BatteryView_bv_power_color_high, mContext.getResources().getColor(R.color.high));

        headerColor = ta.getColor(R.styleable.BatteryView_bv_header_color, Color.WHITE);

        noChargingHighColor = ta.getColor(R.styleable.BatteryView_bv_no_charging_color_high, mContext.getResources().getColor(R.color.high));

        chargingSpeed = ta.getInt(R.styleable.BatteryView_bv_charging_speed, 2) % 10;
        if(chargingSpeed == 0) chargingSpeed = 1;
        ta.recycle();
    }

    private Paint borderPaint;
    private Paint powerPaint;
    private Paint headerPaint;

    // 初始化画笔
    private void initPaints() {
        //
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);
        // 电量的实心部分
        powerPaint = new Paint();
        powerPaint.setAntiAlias(true);
        powerPaint.setStyle(Paint.Style.FILL);
        powerPaint.setColor(highColor);
        powerPaint.setStrokeWidth(0);
        // 电池头部
        headerPaint = new Paint();
        headerPaint.setAntiAlias(true);
        headerPaint.setStyle(Paint.Style.FILL);
        headerPaint.setColor(headerColor);
        headerPaint.setStrokeWidth(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    private RectF borderRf;
    private RectF powerRf;
    private RectF headerRf;

    @Override
    protected void onDraw(Canvas canvas) {
        if(orientation == BatteryViewOrientation.HORIZONTAL_LEFT){
            drawHorizontalLeft(canvas);
        }else if(orientation == BatteryViewOrientation.HORIZONTAL_RIGHT){
            drawHorizontalRight(canvas);
        }else if (orientation == BatteryViewOrientation.VERTICAL_TOP){
            drawVerticalTop(canvas);
        }else if (orientation == BatteryViewOrientation.VERTICAL_BOTTOM){
            drawVerticalBottom(canvas);
        }
    }

    // 朝左的电池
    private void drawHorizontalLeft(Canvas canvas) {
        // 绘制边框
        if (borderRf == null) {
            borderRf = new RectF(headerWidth+borderPadding+borderWidth, borderWidth, width - borderWidth, height - borderWidth);
        }
        canvas.drawRoundRect(borderRf, radis, radis, borderPaint);
        // 绘制实心区域
        if (powerRf == null) {
            setPower(currentPower);
        }
        canvas.drawRoundRect(powerRf, radis, radis, powerPaint);

        // 绘制电池头
        if (headerRf == null) {
            float headerHeight = height / 3f;
            headerRf = new RectF(0, headerHeight, headerWidth, headerHeight * 2);
        }
        canvas.drawRoundRect(headerRf, radis, radis, headerPaint);
    }
    // 朝右的电池
    private void drawHorizontalRight(Canvas canvas) {
        // 绘制边框
        if (borderRf == null) {
            borderRf = new RectF(borderWidth, borderWidth, width - borderWidth - borderPadding - headerWidth, height - borderWidth);
        }
        canvas.drawRoundRect(borderRf, radis, radis, borderPaint);
        // 绘制实心区域
        if (powerRf == null) {
            setPower(currentPower);
        }
        canvas.drawRoundRect(powerRf, radis, radis, powerPaint);

        // 绘制电池头
        if (headerRf == null) {
            float headerHeight = height / 3f;
            headerRf = new RectF(width - headerWidth, headerHeight, width, headerHeight * 2);
        }
        canvas.drawRoundRect(headerRf, radis, radis, headerPaint);
    }
    // 电池横向的宽度
    private float getHorizontalWidth(int power) {
        // 满电量宽度
        float fullWidth = width - borderWidth * 2 - borderPadding * 2 - borderPadding - headerWidth;
        return fullWidth * power / 100f;
    }

    // 电池头朝上
    private void drawVerticalTop(Canvas canvas) {
        // 绘制边框
        if (borderRf == null) {
            borderRf = new RectF(borderWidth, headerWidth + borderPadding + borderWidth, width - borderWidth, height - borderWidth);
        }
        canvas.drawRoundRect(borderRf, radis, radis, borderPaint);
        // 绘制实心区域
        if (powerRf == null) {
            setPower(currentPower);
        }
        canvas.drawRoundRect(powerRf, radis, radis, powerPaint);

        // 绘制电池头
        if (headerRf == null) {
            float headerWidth1 = width / 3f;
            headerRf = new RectF(headerWidth1, 0, headerWidth1 * 2, headerWidth);
        }
        canvas.drawRoundRect(headerRf, radis, radis, headerPaint);
    }

    // 电池头朝下
    private void drawVerticalBottom(Canvas canvas) {
        // 绘制边框
        if (borderRf == null) {
            borderRf = new RectF(borderWidth, borderWidth, width - borderWidth, height - headerWidth - borderPadding - borderWidth);
        }
        canvas.drawRoundRect(borderRf, radis, radis, borderPaint);
        // 绘制实心区域
        if (powerRf == null) {
            setPower(currentPower);
        }
        canvas.drawRoundRect(powerRf, radis, radis, powerPaint);

        // 绘制电池头
        if (headerRf == null) {
            float headerWidth1 = width / 3f;
            headerRf = new RectF(headerWidth1, height - headerWidth, headerWidth1 * 2, height);
        }
        canvas.drawRoundRect(headerRf, radis, radis, headerPaint);
    }

    private float getVerticalHeight(int power){
        // 满电量宽度
        float fullHeight = height - borderWidth * 2 - borderPadding * 3 - headerWidth;
        return fullHeight * power / 100f;
    }

    public void setPower(int power) {
        if(mOnBatteryPowerListener != null) mOnBatteryPowerListener.onPower(currentPower);

        if (power <= lowValue) {
            powerPaint.setColor(lowColor);
        } else if (power < mediumValue) {
            powerPaint.setColor(mediumColor);
        } else {
            if(runnable == null){
                powerPaint.setColor(noChargingHighColor);
            }else{
                powerPaint.setColor(highColor);
            }
        }

        if(orientation == BatteryViewOrientation.HORIZONTAL_RIGHT){
            float realWidth = getHorizontalWidth(power);
            powerRf = new RectF(borderWidth + borderPadding, borderWidth + borderPadding, borderWidth + borderPadding + realWidth, height - borderWidth - borderPadding);
            postInvalidate();
        }else if(orientation == BatteryViewOrientation.HORIZONTAL_LEFT){
            float realWidth = getHorizontalWidth(power);
            powerRf = new RectF(width-borderWidth-borderPadding-realWidth, borderWidth + borderPadding, width - borderWidth - borderPadding, height - borderWidth - borderPadding);
            postInvalidate();
        } else if(orientation == BatteryViewOrientation.VERTICAL_TOP){
            float realHeight = getVerticalHeight(power);
            powerRf = new RectF(borderWidth + borderPadding, height - borderWidth - borderPadding - realHeight, width - borderWidth - borderPadding, height - borderWidth - borderPadding);
            postInvalidate();
        }else if(orientation == BatteryViewOrientation.VERTICAL_BOTTOM){
            float realHeight = getVerticalHeight(power);
            powerRf = new RectF(borderWidth + borderPadding, borderWidth + borderPadding, width - borderWidth - borderPadding, borderWidth + borderPadding + realHeight);
            postInvalidate();
        }
    }

    private int power;

    // 充电动态显示
    private void startCharge() {
        if (runnable != null) return;
        power = currentPower;
        runnable = new Runnable() {
            @Override
            public void run() {
                power %= 100;
                setPower(power);
                power += chargingSpeed;
                //延迟执行
                mHandler.postDelayed(this, 200);
            }
        };
        mHandler.post(runnable);
    }

    private void stopCharge() {
        if (runnable != null) {
            mHandler.removeCallbacks(runnable);
            runnable = null;
        }
    }

    //    private
    private OnBatteryPowerListener mOnBatteryPowerListener;


    public void setOnBatteryPowerListener(OnBatteryPowerListener onBatteryPowerListener){
        mOnBatteryPowerListener = onBatteryPowerListener;
    }
    public void removeOnBatteryPowerListener(){
        mOnBatteryPowerListener = null;
    }

    /**
     * 充电动画速度
     * @param speed 1-9之前的数值
     */
    public void setChargingSpeed(int speed){
        this.chargingSpeed = speed;
    }

}