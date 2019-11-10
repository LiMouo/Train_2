package com.ziker.train.Utils.ToolClass;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ziker.train.R;

import java.util.ArrayList;
import java.util.List;

public class Menu extends LinearLayout {//
    public boolean needMove = false;
    public boolean isTitle = false;
    private Context context;
    private List<View> viewList;
    private List<Integer> idList;
    private LinearLayout linear;//侧滑菜单主体布局
    private LinearLayout linear_main_normal;//主体布局
    private LinearLayout linear_main_exception;//主体布局
    private LinearLayout linear_left;//侧滑菜单主体布局
    private LinearLayout linear_main_menu;//标题栏
    private TextView menu_title;//标题
    private LinearLayout menu_src_root;//图标根
    private CircleImageView menu_src;//图标
    private LinearLayout menu_expand_root;//标题expand父布局
    private View menu_expand;//用户传过来的expand
    private View exception_view;
    private View user_view;
    private onBackClick onBackClick = null;
    private int onTouchStartX = 0;
    private int onInterceptStartX = 0;
    private int onInterceptStartY = 0;
    private int bx = 0;
    private int offsetX;
    private boolean aBoolean = true;


    public interface onBackClick{
        void onClick();
    }

    public Menu(Context context) {
        super(context);
        SetLayout();
    }

    //动态添加布局代
    public Menu(Context context, View NormalView,Integer ExceptionView, String Title, Integer MenuIcon, Integer Expand_id, Integer menu_id) {
        super(context);
        this.context = context;
        user_view = NormalView;
        SetLayout();
        user_view = NormalView;
        if(NormalView != null)
            linear_main_normal.addView(user_view);
        if(ExceptionView != null){
            exception_view = LayoutInflater.from(context).inflate(ExceptionView, null);
            LayoutParams menu_more_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            exception_view.setLayoutParams(menu_more_params);
            linear_main_exception.addView(exception_view);
        }
        this.setBackgroundColor(Color.parseColor("#ffffff"));//this背景设置
        setResources(context, Title, MenuIcon, Expand_id, menu_id);
    }

    //xml静态添加布局
    public Menu(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        SetLayout();
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

    private void setResources(Context context, String Title, Integer MenuIcon, Integer expand_id, Integer menu_id) {
        //找到子控件
        if (menu_id != null) {
            View left_item =LayoutInflater.from(context).inflate(menu_id, null);
            linear_left.addView(left_item);
            //找到所有子控件及ID
            viewList = getAllChildViews(left_item);
            idList = new ArrayList<>();
            for (int i = 0; i < viewList.size(); i++) {
                idList.add(viewList.get(i).getId());
            }
        }

        menu_src.setOnClickListener(v -> {//设置菜单图标击事件
            if(!isTitle){
                if (linear.getTranslationX() == 0){
                    ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(linear, "translationX",-linear_left.getWidth());
                    valueAnimator.setDuration(300);
                    valueAnimator.start();
                } else if (linear.getTranslationX() == -linear_left.getWidth()) {
                    ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(linear, "translationX",0);
                    valueAnimator.setDuration(300);
                    valueAnimator.start();
                }
            }else {
                if(onBackClick!=null){
                    onBackClick.onClick();
                }
            }
        });
        if (expand_id != null) {//expand布局不为空加载
            menu_expand = LayoutInflater.from(context).inflate(expand_id, null);
            LayoutParams menu_more_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            menu_expand.setLayoutParams(menu_more_params);
            menu_expand_root.addView(menu_expand);
        }
        if (MenuIcon != null) {//图标不为空加载
            menu_src.setImageResource(MenuIcon);
        }
        if (Title != null) {//标题不为空
            menu_title.setText(Title);
        } else {
            menu_title.setText("菜单标题未设置");
        }
        if (expand_id == null && MenuIcon == null) {//图标与expand布局为空则让text占满
            menu_src_root.setVisibility(GONE);
            menu_expand_root.setVisibility(GONE);
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

    protected void SetLayout() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(dm);
        int width = dm.widthPixels;
//        int width = getContext().getResources().getDisplayMetrics().widthPixels;//获取的不包含导航栏高度，不管显示还是隐藏
        int height = getResources().getDisplayMetrics().heightPixels;
        int menu_height = (int)(56* getContext().getResources().getDisplayMetrics().density+0.5f);
        /**
         * 根布局
         * 左侧滑动区
         * 内容区
         */
        linear = GetLayout(LinearLayout.HORIZONTAL, "#eeeeee", width + width / 3, height);
        linear_left = GetLayout(LinearLayout.VERTICAL, "#eeeeee", width / 3, height);
        linear_main_normal = GetLayout(LinearLayout.VERTICAL, "#ffffff", width, height);
        linear_main_exception = GetLayout(LinearLayout.VERTICAL, "#ffffff", width, height);
        linear_main_menu = GetLayout(LinearLayout.HORIZONTAL, "#eeeeee", width, menu_height);
        menu_src_root = GetLayout(LinearLayout.HORIZONTAL, null, width / 8 * 3, menu_height);
        menu_expand_root = GetLayout(LinearLayout.HORIZONTAL, null, width / 8 * 3, menu_height);

        menu_src_root.setPadding(35,5,5,0);
        menu_src = new CircleImageView(getContext());
        menu_src.setLayoutParams(new LayoutParams(menu_height,menu_height));

        menu_title = new TextView(getContext());
        menu_title.setText("标题");
        menu_title.setWidth(width / 8 * 2);
        menu_title.setHeight(height);
        menu_title.setTextSize(23);

        linear_main_exception.setVisibility(View.GONE);
        menu_expand_root.setGravity(Gravity.CENTER);
        menu_title.setGravity(Gravity.CENTER);
        menu_src_root.setGravity(Gravity.START);
        linear_main_menu.setGravity(Gravity.CENTER);
        menu_src_root.addView(menu_src);
        linear_main_menu.addView(menu_src_root);
        linear_main_menu.addView(menu_title);
        linear_main_menu.addView(menu_expand_root);
        linear_main_normal.addView(linear_main_menu);
        linear_main_normal.addView(linear_main_exception);
        linear.addView(linear_left);
        linear.addView(linear_main_normal);
        this.addView(linear);
    }

    private LinearLayout GetLayout(int Orientation, String BackgroundColor, int width, int height) {
        LayoutParams linear_params = new LayoutParams(width, height);
        LinearLayout Temp = new LinearLayout(getContext());
        Temp.setOrientation(Orientation);
        Temp.setLayoutParams(linear_params);
        if(BackgroundColor != null)
            Temp.setBackgroundColor(Color.parseColor(BackgroundColor));
        return Temp;
    }




    /*第一次测量位置后，调整位置以达到隐藏侧滑菜单*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (aBoolean) {
            linear.setTranslationX(-linear_left.getMeasuredWidth());
            aBoolean = false;
        }
    }

    /*拦截子控件onTouchEvent事件，例RecycleView滑动事件如不拦截，检测，onTouchEvent将被RecycleView拦截，在RecycleView上将无法拉出侧滑菜单*/
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!isTitle){
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    int nextX = (int) ev.getX();
                    int nextY = (int) ev.getY();
                    if (nextX - onInterceptStartX > 30 && Math.abs(nextY - onInterceptStartY) <= 20) {//从左向右拉超过30并且y差距小于20
                        return !needMove;
                    }else if (linear.getTranslationX() == 0 && nextX > linear_left.getWidth() && nextY > menu_src.getWidth()) {//侧滑菜单出现并且是一个单击行为
                        return true;
                    }
                    return false;
                case MotionEvent.ACTION_DOWN:
                    onInterceptStartX = (int) ev.getX();
                    onInterceptStartY = (int) ev.getY();
                    onTouchStartX = (int) ev.getX();
                    bx = (int) ev.getX();
                    if (linear.getTranslationX() == 0 && onInterceptStartX > linear_left.getWidth() && onInterceptStartY > menu_src.getWidth()) {//侧滑菜单出现并且是一个单击行为
                        return true;
                    }
                    return false;
            }
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isTitle){
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    /**
                     * x 方向偏移
                     */
                    offsetX = (int) (event.getX() - bx);
                    if (offsetX < 0 && linear.getTranslationX() != -linear_left.getWidth() && -offsetX < linear_left.getWidth()) {
                        /**
                         * 从右向左收回菜单并拉出部分超过五分之一
                         */
                        if (linear_left.getWidth() / 5 < -offsetX) {
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
                        /**
                         * 从左向右拉出菜单并拉出部分超过五分之一
                         */
                        if (offsetX > linear_left.getWidth() / 5 && linear.getTranslationX() != 0 && offsetX < linear_left.getWidth()) {
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
                    /**
                     *  X方向偏移量
                     */
                    offsetX = (int) (event.getX() - onTouchStartX);
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
        return  false;
    }


    public void SetMenuItem(List<Integer> IDList, List<Class<?>> ActivityList, OnClickListener listener) throws Exception {
        if (idList == null) {
            throw new ExceptionInInitializerError("Please add a menu file in the Menu XML File attribute of the XML layout");
        } else if (IDList.size() != ActivityList.size()) {
            throw new ExceptionInInitializerError("PPlease check whether the length of IDlist is consistent with that of Actvitylist");
        } else {
            for (int i = 0; i < IDList.size(); i++) {
                int itemId = idList.indexOf(IDList.get(i));
                if (itemId >= 0) {
                    View view = findViewById(IDList.get(i));
                    int finalI = i;
                    view.setOnClickListener(v -> {
                        if(getContext().getClass() != ActivityList.get(finalI)){
                            if (listener != null) {
                                listener.onClick(view);
                                Intent intent = new Intent(getContext(), ActivityList.get(finalI));
                                getContext().startActivity(intent);
                            }else {
                                Intent intent = new Intent(getContext(), ActivityList.get(finalI));
                                getContext().startActivity(intent);
                            }
                        } else{
                            ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(linear, "translationX",-linear_left.getWidth());
                            valueAnimator.setDuration(300);
                            valueAnimator.start();
                        }
                    });
                } else {
                    throw new ExceptionInInitializerError("Please check that all IDS exist in the Menu XML File");
                }
            }
        }
    }
    public View getExpand(){
        return menu_expand;
    }
    public View getTitle() {
        return linear_main_menu;
    }
    public View getMenu() {
        return linear_left;
    }
    public View getExceptionView(){
        return exception_view;
    }
    public void hide_menu(){
        linear.setTranslationX(-linear_left.getWidth());
    }
    public void SetOnBackClick(Integer icon, onBackClick Back){
        isTitle = true;
        onBackClick = Back;
        if(icon != null)
            menu_src.setImageResource(icon);
        else
            menu_src.setVisibility(GONE);
    }
    public void SetNetWorkState(boolean isNormal){
        if(isNormal){
            user_view.setVisibility(View.VISIBLE);
            linear_main_exception.setVisibility(View.GONE);
            if(menu_expand != null)
                menu_expand.setVisibility(View.VISIBLE);
        }else {
            user_view.setVisibility(View.GONE);
            linear_main_exception.setVisibility(View.VISIBLE);
            if(menu_expand != null)
                menu_expand.setVisibility(View.GONE);
        }
    }
}

