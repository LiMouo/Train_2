package com.ziker.train.Utils.ToolClass;

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
    private Paint paint = new Paint();
    public float scnle = 1.0f;
    private float totaltranslateX = 0.0f;
    private float totaltranslateY = 0.0f;
    private float translateX = 0.0f;
    private float translateY = 0.0f;
    public boolean isTranslate = false;
    public boolean isScanle = false;
    private float prevDist = 0;
    private float centerX,centerY;
    private boolean isFirst = true;

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
        if (isFirst){
//            scnle = Math.min((getWidth()*1.0f) / bitmap.getWidth(), (getHeight()*1.0f) / bitmap.getHeight());
//            matrix.postScale(scnle,scnle,centerX,centerY);
//            if(bitmap.getWidth()>getWidth() || bitmap.getHeight()>getHeight()){
//                scnle = Math.min((getWidth()*1.0f) / bitmap.getWidth(), (getHeight()*1.0f) / bitmap.getHeight());
//                Log.d(TAG, "onDraw: "+getWidth()+"\t"+bitmap.getWidth()+"\t"+getHeight()+"\t"+bitmap.getHeight());
//                Log.d(TAG, "onDraw: "+scnle);
//                if(scnle > 2.4)
//                    scnle = 2;
//                if(scnle <0.25)
//                    scnle = 0.25f;
//                totaltranslateX = getWidth() / 2 - (bitmap.getWidth() *scnle)/2-(bitmap.getWidth() *scnle)/8;
//                totaltranslateY = getHeight() /2- (bitmap.getHeight() *scnle)/3;
//                Log.d(TAG, "onDraw: "+(bitmap.getHeight() *scnle)/2+"\t"+getHeight());
//                Log.d(TAG, "onDraw: "+(bitmap.getWidth() *scnle)/2+"\t"+getWidth());
//            }else {
                totaltranslateX = (getRight() - getLeft()) / 2 - bitmap.getWidth()/2;
                totaltranslateY = (getBottom() - getTop()) / 2 - bitmap.getHeight()/2;
//            }
//            centerX = totaltranslateX;
//            centerY = totaltranslateY;
//            matrix.postScale(scnle,scnle);
//            matrix.postTranslate(totaltranslateX,totaltranslateY);
//            Bitmap bitmap1 = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
//            canvas.drawBitmap(bitmap1,matrix,null);
            isFirst = false;
        }
        centerX = (bitmap.getWidth() *scnle) / 2.0f;
        centerY = getHeight() /2- (bitmap.getHeight() *scnle)/2;
        matrix.postScale(scnle,scnle,centerX,centerY);
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
//                    centerX = (event.getX(0)+event.getX(1)) / 2;
//                    centerY = (event.getY(0)+event.getY(1)) / 2;
                    isScanle = false;
                    break;
                case MotionEvent.ACTION_MOVE:

                    float curvDist = calSpace(event);
                    scnle = scnle*(curvDist/prevDist);
                    if(scnle > 2.4)
                        scnle = 2;
                    if(scnle <0.25)
                        scnle = 0.25f;
//                    if(isScanle)

                    prevDist = curvDist;
//                    centerX = (event.getX(0)+event.getX(1)) / 2;
//                    centerY = (event.getY(0)+event.getY(1)) / 2;
                    invalidate();
                    isScanle = true;
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
                    }
                    invalidate();
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
