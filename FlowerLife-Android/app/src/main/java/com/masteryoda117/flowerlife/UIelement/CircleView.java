package com.masteryoda117.flowerlife.UIelement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.masteryoda117.flowerlife.R;

public class CircleView extends View {

    private int width, height;

    private float percent = (float) 0.2, arcWidth;

    private RectF rect;

    Paint arcPaint = new Paint();

    public float getArcWidth(){
        return arcWidth;
    }

    public void setArcWidth(float width){
        this.arcWidth = width;
    }

    public float val = 0.3f;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = this.getMeasuredWidth();
        height = this.getMeasuredHeight();
        float cirCenterX = width/2;
        float cirCenterY = height/2;

        float radius = (float) (height/2.5);

        rect = new RectF(cirCenterX - radius, cirCenterY - radius, cirCenterX + radius, cirCenterY + radius);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onDraw(Canvas canvas) {
        arcPaint.setAntiAlias(true);
        arcPaint.setColor(Color.rgb(230, 230, 230));
        canvas.drawArc(rect, 135f, 270f, true, arcPaint);

        arcPaint.setColor(Color.rgb(20,160,151));
        canvas.drawArc(rect, 135f, percent, true, arcPaint);

        int color = getResources().getColor(R.color.colorBackGround);
        arcPaint.setColor(color);
        canvas.drawCircle(width/2, height/2, (float) (height/2.5) - arcWidth, arcPaint);
    }

    public void setValue(float percentage){
        if(percentage > 1)
            percentage = 1;
        percent = percentage * 270;
        this.invalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
