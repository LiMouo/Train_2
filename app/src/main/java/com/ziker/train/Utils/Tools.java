package com.ziker.train.Utils;

import android.graphics.drawable.AnimationDrawable;
import android.widget.Button;

public class Tools {
    public  void setbuttoncolor(Button button){
        AnimationDrawable animationDrawable = (AnimationDrawable) button.getBackground();
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
    }
}
