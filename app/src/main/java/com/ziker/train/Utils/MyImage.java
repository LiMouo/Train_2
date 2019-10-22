package com.ziker.train.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.ziker.train.R;

public class MyImage extends View {
    private Matrix matrix =  new Matrix();
    private Bitmap bitmap;
    public float scnle = 1.0f;
    private float totaltranslateX = 0.0f;
    private float totaltranslateY = 0.0f;
    private float translateX = 0.0f;
    private float translateY = 0.0f;
    public boolean isTranslate = false;
    private float prevDist = 0;
    private float centerX,centerY;
    private Paint paint = new Paint();

    public MyImage(Context context) {
        super(context);
    }

    public MyImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Menu);
        int id = ta.getResourceId(R.styleable.Menu_src,0);
        ta.recycle();
        if(id != 0)
            setImage(id);
        this.setFocusable(true);
        paint.setColor(Color.RED);
    }

    public MyImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImage(int resourceId){
        Bitmap temp = BitmapFactory.decodeResource(getResources(), resourceId);
        setImage(temp);
    }

    public void setImage(Bitmap bitmap){
        this.bitmap = bitmap;
        centerX = bitmap.getWidth();
        centerY = bitmap.getHeight();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        matrix.reset();
        matrix.setScale(scnle,scnle,centerX,centerY);
//        matrix.setScale(scnle,scnle);
        matrix.postTranslate(totaltranslateX,totaltranslateY);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        canvas.drawBitmap(bitmap1,matrix,null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getPointerCount()>=2){
            switch (event.getActionMasked()){
                case MotionEvent.ACTION_POINTER_DOWN:
                    prevDist = calSpace(event);
                    centerX = (event.getX(0)+event.getX(1)) / 2;
                    centerY = (event.getY(0)+event.getY(1)) / 2;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float curvDist = calSpace(event);
                    scnle = scnle*(curvDist/prevDist);
                    invalidate();
                    prevDist = curvDist;
                    break;
            }
            isTranslate = false;
        }else {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    translateX = event.getX();
                    translateY = event.getY();
                    isTranslate = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(isTranslate){
                        float x = event.getX() - translateX;
                        float y = event.getY() - translateY;
                        totaltranslateX += x;
                        totaltranslateY += y;
                        invalidate();
                    }
                    translateX = event.getX();
                    translateY = event.getY();
                    isTranslate = true;
                    break;
            }
        }
        return true;
    }

    private float calSpace(MotionEvent event){
        float x = event.getX(0)-event.getX(1);
        float y = event.getY(0)-event.getY(1);
        return (float)Math.sqrt(x*x+y*y);
    }
}
