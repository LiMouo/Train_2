package com.ziker.train.Utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ziker.train.R;

import java.util.ArrayList;
import java.util.List;

public class Menu extends LinearLayout {//
    private LinearLayout linear;//侧滑菜单主体布局
    private LinearLayout linear_main;//主体布局
    private LinearLayout linear_left;//侧滑菜单主体布局
    private LinearLayout linear_main_menu;//标题栏
    private TextView menu_title;//标题
    private LinearLayout menu_src_root;//图标根
    private ImageView menu_src;//图标
    private LinearLayout menu_more_root;//标题more父布局
    private View menu_more;//用户传过来的more
    private List<View> viewList;
    private List<Integer> idList;
    private int onTouchStartX = 0;
    private int onInterceptStartX = 0;
    private int onInterceptStartY = 0;
    private int bx = 0;
    private int offsetX;
    private boolean aBoolean = true;

    public Menu(Context context) {
        super(context);
        setLinear();

    }

    //动态添加布局代
    public Menu(Context context, View view, String Title, Integer MenuIcon, Integer Top_more_id, Integer Left_menu_id) {
        super(context);
        setLinear();
        linear_main.addView(view);
        this.setBackgroundColor(Color.parseColor("#ffffff"));//this参数设置
        setResources(context, Title, MenuIcon, Top_more_id, Left_menu_id);
    }

    //xml静态添加布局
    public Menu(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLinear();
        //获取自定义参数值
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Menu);
        int MenuIcon = ta.getResourceId(R.styleable.Menu_src, 0);
        String Title = ta.getString(R.styleable.Menu_title);
        int Top_more_id = ta.getResourceId(R.styleable.Menu_linear_top_more, 0);
        int Left_menu_id = ta.getResourceId(R.styleable.Menu_linear_left_menu, 0);
        ta.recycle();
        if (MenuIcon != 0)
            setResources(context, Title, MenuIcon, Top_more_id, Left_menu_id);
        else
            setResources(context, Title, null, Top_more_id, Left_menu_id);
    }

    private void setResources(Context context, String Title, Integer MenuIcon, Integer Top_more_id, Integer Left_menu_id) {
        //找到子控件
        if (Left_menu_id != null) {
            LinearLayout left_item = (LinearLayout) LayoutInflater.from(context).inflate(Left_menu_id, null);
            linear_left.addView(left_item);
            //找到所有子控件及ID
            viewList = getAllChildViews(left_item);
            idList = new ArrayList<>();
            for (int i = 0; i < viewList.size(); i++) {
                idList.add(viewList.get(i).getId());
            }
        }

        menu_src.setOnClickListener(v -> {//设置菜单图标击事件
            if (linear.getTranslationX() == 0)
                linear.setTranslationX(-linear_left.getWidth());
            else if (linear.getTranslationX() == -linear_left.getWidth()) {
                linear.setTranslationX(0);
            }
        });
        if (Top_more_id != null) {//more布局不为空加载
            menu_more = LayoutInflater.from(context).inflate(Top_more_id, null);
            menu_more_root.addView(menu_more_root);
        }
        if (MenuIcon != null) {//图标不为空加载
            menu_src.setImageResource(MenuIcon);
        }
        if (Title != null) {//标题不为空
            menu_title.setText(Title);
        } else {
            menu_title.setText("菜单标题未设置");
        }
        if (Top_more_id == null && MenuIcon == null) {//图标与more布局为空则让text占满
            menu_src_root.setVisibility(GONE);
            menu_more_root.setVisibility(GONE);
        }
    }

    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                //再次 调用本身（递归）
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    protected void setLinear() {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int menu_height = (int)(56* getContext().getResources().getDisplayMetrics().density+0.5f);

        linear = getLinearLayout(LinearLayout.HORIZONTAL, "#eeeeee", width + width / 3, height);//根布局
        linear_left = getLinearLayout(LinearLayout.VERTICAL, "#eeeeee", width / 3, height);//左侧滑动区
        linear_main = getLinearLayout(LinearLayout.VERTICAL, "#ffffff", width, height);//内容区
        linear_main_menu = getLinearLayout(LinearLayout.HORIZONTAL, "#eeeeee", width, menu_height);
        menu_src_root = getLinearLayout(LinearLayout.HORIZONTAL, null, width / 8 * 3, menu_height);
        menu_more_root = getLinearLayout(LinearLayout.HORIZONTAL, null, width / 8 * 3, menu_height);

        menu_src_root.setPadding(5,5,5,5);

        menu_src = new ImageView(getContext());
        menu_src.setLayoutParams(new LayoutParams(menu_height,menu_height));

        menu_title = new TextView(getContext());
        menu_title.setText("标题");
        menu_title.setWidth(width / 4);
        menu_title.setHeight(height);
        menu_title.setTextSize(23);

        menu_more_root.setGravity(Gravity.CENTER);
        menu_title.setGravity(Gravity.CENTER);
        menu_src_root.setGravity(Gravity.START);

        menu_src_root.addView(menu_src);
        linear_main_menu.addView(menu_src_root);
        linear_main_menu.addView(menu_title);
        linear_main_menu.addView(menu_more_root);
        linear_main.addView(linear_main_menu);
        linear.addView(linear_left);
        linear.addView(linear_main);
        this.addView(linear);
    }

    private LinearLayout getLinearLayout(int Orientation, String BackgroundColor, int width, int height) {
        LayoutParams linear_params = new LayoutParams(width, height);
        LinearLayout Temp = new LinearLayout(getContext());
        Temp.setOrientation(Orientation);
        Temp.setLayoutParams(linear_params);
        if(BackgroundColor != null)
            Temp.setBackgroundColor(Color.parseColor(BackgroundColor));
        return Temp;
    }

    public void setOnMenuItemStartActivity(List<Integer> IDList, List<Class<?>> ActivitClassList, OnClickListener listener) throws Exception {
        if (idList == null) {
            throw new ExceptionInInitializerError("Please add a menu file in the linear_Left_Menu attribute of the XML layout");
        } else if (IDList.size() != ActivitClassList.size()) {
            throw new ExceptionInInitializerError("PPlease check whether the length of IDlist is consistent with that of Actvitylist");
        } else {
            for (int i = 0; i < IDList.size(); i++) {
                int itemid = idList.indexOf(IDList.get(i));
                if (itemid >= 0) {
                    View view = findViewById(IDList.get(i));
                    int finalI = i;
                    view.setOnClickListener(v -> {
                        if(getContext().getClass() != ActivitClassList.get(finalI)){
                            Intent intent = new Intent(getContext(), ActivitClassList.get(finalI));
                            getContext().startActivity(intent);
                            if (listener != null)
                                listener.onClick(view);
                            else {
                                ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(linear, "translationX",-linear_left.getWidth());
                                valueAnimator.setDuration(300);
                                valueAnimator.start();
                            }
                        }
                        else{
                            ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(linear, "translationX",-linear_left.getWidth());
                            valueAnimator.setDuration(300);
                            valueAnimator.start();
                        }
                    });
                } else {
                    throw new ExceptionInInitializerError("Please check that all IDS exist in the linear_Left_Menu");
                }
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view_main = getChildAt(2);//通过xml添加布局，将第二个位置布局也就是Menu的唯一子布局加入Linear_main中，代码不影响
        removeView(view_main);
        linear_main.addView(view_main);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (aBoolean) {//第一次测量位置后，调整位置以达到隐藏侧滑菜单
            linear.setTranslationX(-linear_left.getMeasuredWidth());
            aBoolean = false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {//拦截子控件ontouchevent事件，例RecycleView滑动事件如不拦截，检测，onTouchevent将被RecycleView拦截，在RecycleView上将无法拉出侧滑菜单
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int nextX = (int) ev.getX();
                int nextY = (int) ev.getY();
                if (nextX - onInterceptStartX > 30 && Math.abs(nextY - onInterceptStartY) <= 20) {//从左向右拉超过30并且y差距小于20
                    return true;
                } else if (linear.getTranslationX() == 0 && nextX > linear_left.getWidth() && nextY > menu_src.getWidth()) {//侧滑菜单出现并且是一个单击行为
                    return true;
                }
                return false;
            case MotionEvent.ACTION_DOWN:
                onInterceptStartX = (int) ev.getX();
                onInterceptStartY = (int) ev.getY();
                onTouchStartX = (int) ev.getX();
                bx = (int) ev.getX();
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                offsetX = (int) (event.getX() - bx);// x 方向偏移
                if (offsetX < 0 && linear.getTranslationX() != -linear_left.getWidth() && -offsetX < linear_left.getWidth()) {
                    if (linear_left.getWidth() / 5 < -offsetX) {//从右向左收回菜单并拉出部分超过五分之一
                        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(linear, "translationX", -linear_left.getWidth());
                        valueAnimator.setDuration((linear_left.getWidth() + offsetX) / 2);
                        valueAnimator.start();
                    } else {
                        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(linear, "translationX",0);
                        valueAnimator.setDuration(300);
                        valueAnimator.start();
                    }
                }
                if (offsetX > 0 && linear.getTranslationX() != 0) {
                    if (offsetX > linear_left.getWidth() / 5 && linear.getTranslationX() != 0 && offsetX < linear_left.getWidth()) {//从左向右拉出菜单并拉出部分超过五分之一
                        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(linear, "translationX", 0);
                        valueAnimator.setDuration((linear_left.getWidth() - offsetX) / 2);
                        valueAnimator.start();
                    } else {
                        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(linear, "translationX",-linear_left.getWidth());
                        valueAnimator.setDuration(300);
                        valueAnimator.start();
                    }
                }
                invalidate();
                onTouchStartX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = (int) (event.getX() - onTouchStartX);// x 方向偏移量
                if (linear.getTranslationX() + offsetX >= 0) {//从左向右拉出将超界
                    linear.setTranslationX(0);
                } else if (offsetX < 0 && Math.abs(linear.getTranslationX() + offsetX) > linear_left.getWidth()) {//从右向左收回将超界
                    linear.setTranslationX(-linear_left.getWidth());
                } else if (linear.getTranslationX() <= linear_left.getWidth()) {//不超界正常偏移
                    linear.setTranslationX(linear.getTranslationX() + offsetX);
                }
                invalidate();//更新视图
                onTouchStartX = (int) event.getX();//
                break;
            case MotionEvent.ACTION_DOWN:
                onTouchStartX = (int) event.getX();
                bx = (int) event.getX();
                break;
        }
        return true;//return true 将拦截事件，不在向父类传递该事件，细节百度view事件分发机制
    }

    public View getLinear_main_more() throws Exception {//返回Linear_more,方便调用类定制Linear_more
        return menu_more;
    }
    public View getLinear_main_menu() {
        return linear_main_menu;
    }
    public View getLinear_left() {
        return linear_left;
    }
}

