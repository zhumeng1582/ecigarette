package com.industio.ecigarette.view.battery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.industio.ecigarette.R;

public class BatteryView extends View {
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
    public static int currentPower = -1; // 当前电量

    // 未充电时高电量颜色
    private int noChargingHighColor;

    private int width;
    private int height;
    private float fullWidth;

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        initPaints();
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BatteryView);
        // 电池内边距
        borderPadding = ta.getDimensionPixelSize(R.styleable.BatteryView_bv_border_padding, 2);
        // 电池边框厚度
        borderWidth = ta.getDimensionPixelSize(R.styleable.BatteryView_bv_border_width, 2);
        headerWidth = ta.getDimensionPixelSize(R.styleable.BatteryView_bv_header_width, 6);
        // 电池边框圆角
        radis = ta.getDimensionPixelSize(R.styleable.BatteryView_bv_radis, 2);
        // 电池边框颜色
        borderColor = ta.getColor(R.styleable.BatteryView_bv_border_color, Color.WHITE);

        // 电池实心部分
        lowColor = ta.getColor(R.styleable.BatteryView_bv_power_color_low, context.getResources().getColor(R.color.low));
        lowValue = ta.getInt(R.styleable.BatteryView_bv_power_value_low, 10);
        mediumColor = ta.getColor(R.styleable.BatteryView_bv_power_color_medium, context.getResources().getColor(R.color.medium));
        mediumValue = ta.getInt(R.styleable.BatteryView_bv_power_value_medium, 20);
        highColor = ta.getColor(R.styleable.BatteryView_bv_power_color_high, context.getResources().getColor(R.color.high));

        headerColor = ta.getColor(R.styleable.BatteryView_bv_header_color, Color.WHITE);

        noChargingHighColor = ta.getColor(R.styleable.BatteryView_bv_no_charging_color_high, context.getResources().getColor(R.color.high));
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
        powerPaint.setTextSize(24);
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
        fullWidth = (width - borderWidth * 2 - borderPadding * 2 - borderPadding - headerWidth) / 100f;
    }

    private RectF borderRf;
    private RectF powerRf;
    private RectF headerRf;

    @Override
    protected void onDraw(Canvas canvas) {
        drawHorizontalRight(canvas);
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
            headerRf = new RectF(borderRf.right, headerHeight, width, headerHeight * 2);
        }
        canvas.drawRoundRect(headerRf, radis, radis, headerPaint);
        if (currentPower < 0) {
            canvas.drawText("?", (borderRf.right + borderRf.left) / 2 - 5, borderRf.bottom - 3, powerPaint);
        }

    }


    public void setPower(int power) {
        currentPower = power;
        if (power <= lowValue) {
            powerPaint.setColor(lowColor);
        } else if (power < mediumValue) {
            powerPaint.setColor(mediumColor);
        } else {
            powerPaint.setColor(noChargingHighColor);
        }

        float realWidth =fullWidth * power;
        powerRf = new RectF(borderWidth + borderPadding, borderWidth + borderPadding, borderWidth + borderPadding + realWidth, height - borderWidth - borderPadding);
        postInvalidate();
    }
}