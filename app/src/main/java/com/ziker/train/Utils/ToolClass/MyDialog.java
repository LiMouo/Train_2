package com.ziker.train.Utils.ToolClass;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class MyDialog extends android.app.Dialog {
    private DoSomeThing doSome;
    private Double width,height;
    private int LayoutResID;
    public interface DoSomeThing{
         void Do(Dialog v);
    }

    public MyDialog(@NonNull Context context,Double width,Double height,int LayoutResID) {
        super(context);
        this.width = width;
        this.height = height;
        this.LayoutResID = LayoutResID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LayoutResID);
        WindowManager m = getWindow().getWindowManager();
        Display d= m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);

        if(width != null)
            p.width = (int)(size.x*width);
        if(height != null)
            p.height = (int)(size.x*height*0.5);
        getWindow().setAttributes(p);

        if(doSome != null)
            doSome.Do(this);
    }

    public MyDialog Do(DoSomeThing doSome){
        this.doSome = doSome;
        return this;
    }
}
