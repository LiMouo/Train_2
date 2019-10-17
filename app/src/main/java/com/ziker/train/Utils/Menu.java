package com.ziker.train.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ziker.train.R;

import java.util.ArrayList;
import java.util.List;

public class Menu extends LinearLayout {//
    private final static String TAG = "Menu";
    private LinearLayout linear;//侧滑菜单主体布局
    private LinearLayout linear_main;//主体布局
    private LinearLayout linear_left;//侧滑菜单主体布局
    private View topmenu_more;//More布局
    private List<View> viewList;
    private List<Integer> idList;
    private int onTouchStartX = 0;
    private int onInterceptStartX = 0;
    private int onInterceptStartY = 0;
    private int bx=0;
    private int index = 0;
    private int offsetX;
    private boolean aBoolean =true;
    //动态添加布局代
    public Menu(Context context, View view,String Title, Integer MenuIcon,Integer Top_more_id,Integer Left_menu_id){
        super(context);
        setLinear();
        linear_main.addView(view);
        this.setBackgroundColor(Color.parseColor("#00ffffff"));//this参数设置
        this.setOrientation(HORIZONTAL);
        //侧滑菜单内容到侧滑菜单布局
        if(Left_menu_id!=0){
            LinearLayout linear_menu = (LinearLayout) LayoutInflater.from(context).inflate(Left_menu_id, null);
            linear_left.addView(linear_menu);
            //找到所有子控件及ID
            viewList = getAllChildViews(linear_menu);
            idList = new ArrayList<>();
            for (int i = 0; i < viewList.size(); i++) {
                idList.add(viewList.get(i).getId());
            }
        }
        //找到子控件
        TextView title = findViewById(R.id.title);//标题
        ImageView menu = findViewById(R.id.menu);//src图标
        LinearLayout linear_more = findViewById(R.id.linear_more);//more布局
        LinearLayout linear_top = findViewById(R.id.linear_menumain);//上菜单
        AnimationDrawable animationDrawable = (AnimationDrawable) linear_top.getBackground();//设置上菜单动画
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
        menu.setOnClickListener(v -> {//设置菜单图表点击事件
            if(getScrollX()==0)
                scrollBy(linear_left.getWidth(),0);
            else if(getScrollX() >=linear_left.getWidth()){
                scrollTo(0,0);
            }
        });
        if(Top_more_id!=null){//more布局不为空加载
            topmenu_more = LayoutInflater.from(context).inflate(Top_more_id,null);
            linear_more.addView(topmenu_more);
        }
        if(MenuIcon != null){//图标不为空加载
            menu.setImageResource(MenuIcon);
        }
        title.setTextSize(23);
        if(Title!=null) {//标题不为空
            title.setText(Title);
        }else {
            title.setText("菜单标题未设置");
        }
        if(topmenu_more ==null && MenuIcon==null){//图标与more布局为空则让text占满
            LinearLayout layout = findViewById(R.id.src_root);
            layout.setVisibility(GONE);
            linear_more.setVisibility(GONE);
        }
    }


    public Menu(Context context) {
        super(context);
        setLinear();

    }

    //xml静态添加布局
    public Menu(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLinear();
        //获取自定义参数值
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.Menu);
        Drawable Menu_icon = ta.getDrawable(R.styleable.Menu_src);
        String Menu_Title = ta.getString(R.styleable.Menu_title);
        int Top_More_Id = ta.getResourceId(R.styleable.Menu_linear_top_more,0);
        int Left_Menu_Id = ta.getResourceId(R.styleable.Menu_linear_left_menu,0);
        ta.recycle();
        this.setBackgroundColor(Color.parseColor("#00ffffff"));//this参数设置
        this.setOrientation(HORIZONTAL);
        //侧滑菜单内容到侧滑菜单布局
        if(Left_Menu_Id!=0){
            LinearLayout linear_menu = (LinearLayout) LayoutInflater.from(context).inflate(Left_Menu_Id, null);
            linear_left.addView(linear_menu);
            //找到所有子控件及ID
            viewList = getAllChildViews(linear_menu);
            idList = new ArrayList<>();
            for (int i = 0; i < viewList.size(); i++) {
                idList.add(viewList.get(i).getId());
            }
        }
        //找到子控件
        TextView title = findViewById(R.id.title);//标题
        ImageView menu = findViewById(R.id.menu);//src图标
        LinearLayout linear_more = findViewById(R.id.linear_more);//more布局
        LinearLayout linear_top = findViewById(R.id.linear_menumain);//上菜单
        AnimationDrawable animationDrawable = (AnimationDrawable) linear_top.getBackground();//设置上菜单动画
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
        menu.setOnClickListener(v -> {//设置菜单图表点击事件
            if(getScrollX()==0)
                scrollBy(linear_left.getWidth(),0);
            else if(getScrollX() >=linear_left.getWidth()){
                scrollTo(0,0);
            }
        });
        if(Top_More_Id!=0){//more布局不为空加载
            topmenu_more = LayoutInflater.from(context).inflate(Top_More_Id,null);
            linear_more.addView(topmenu_more);
        }
        if(Menu_icon != null){//图标不为空加载
            menu.setImageDrawable(Menu_icon);
        }
        title.setTextSize(23);
        if(Menu_Title!=null) {//标题不为空
            title.setText(Menu_Title);
        }else {
            title.setText("首页");
        }
        if(topmenu_more ==null && Menu_icon==null){//图标与more布局为空则让text占满
            LinearLayout layout = findViewById(R.id.src_root);
            layout.setVisibility(GONE);
            linear_more.setVisibility(GONE);
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

    protected void setLinear(){
//        LayoutParams linear_params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//        linear = new LinearLayout(getContext());
//        linear.setOrientation(LinearLayout.VERTICAL);
//        linear.setLayoutParams(linear_params);
//        linear.setBackgroundColor(Color.parseColor("#eeeeee"));
//        linear_params.width = getResources().getDisplayMetrics().widthPixels /3 + getResources().getDisplayMetrics().widthPixels;
//        linear.setLayoutParams(linear_params);
//        this.addView(linear);
        //定义一个linear_left主体填充侧滑菜单
        LayoutParams left_params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        linear_left = new LinearLayout(getContext());
        linear_left.setOrientation(LinearLayout.VERTICAL);
        linear_left.setLayoutParams(left_params);
        linear_left.setBackgroundColor(Color.parseColor("#eeeeee"));
        left_params.width = getResources().getDisplayMetrics().widthPixels /3;
        linear_left.setLayoutParams(left_params);
        this.addView(linear_left);
        //定义一个linear_main主体填充侧滑菜单
        LayoutParams main_params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        linear_main = new LinearLayout(getContext());
        linear_main.setOrientation(LinearLayout.VERTICAL);
        linear_main.setLayoutParams(main_params);
        linear_main.setBackgroundColor(Color.parseColor("#ffffff"));
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        main_params.width = getResources().getDisplayMetrics().widthPixels * 1;//为LinearLayout设置高度
        main_params.height = displayMetrics.heightPixels;//为LinearLayout设置高度
        linear_main.setLayoutParams(main_params);
        this.addView(linear_main);
        //添加菜单到主体主体布局中
        LayoutInflater.from(getContext()).inflate(R.layout.my_menu, linear_main);
    }

    public void setOnMenuItemStartActivity(List<Integer> IDList, List<Class<?>> ActivitClassList,OnClickListener listener) throws Exception{
        if(idList == null){
            throw new ExceptionInInitializerError("Please add a menu file in the linear_Left_Menu attribute of the XML layout");
        }else if(IDList.size()!=ActivitClassList.size()){
            throw new ExceptionInInitializerError("PPlease check whether the length of IDlist is consistent with that of Actvitylist");
        }else{
            for (int i=0;i<IDList.size();i++) {
                int itemid = idList.indexOf(IDList.get(i));
                if(itemid>=0){
                    View view = findViewById(IDList.get(i));
                    int finalI = i;
                    view.setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(),ActivitClassList.get(finalI));
                        getContext().startActivity(intent);
                        if(listener != null)
                            listener.onClick(view);
                        else{
                            try {
                                Thread.sleep(300);
                                scrollTo(linear_left.getMeasuredWidth(),0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    throw new ExceptionInInitializerError("Please check that all IDS exist in the linear_Left_Menu");
                }
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        Log.d(TAG, "onFinishInflate: +");
        super.onFinishInflate();
        View view_main = getChildAt(2);//通过xml添加布局，将第二个位置布局也就是Menu的唯一子布局加入Linear_main中，代码不影响
        removeView(view_main);
        linear_main.addView(view_main);
        Log.d(TAG, "Menu: "+getParent()+"\t"+this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: onMeasure");
        if(aBoolean){//第一次测量位置后，调整位置以达到隐藏侧滑菜单
            scrollTo(linear_left.getMeasuredWidth(),0);
            aBoolean =false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {//拦截子控件ontouchevent事件，例RecycleView滑动事件如不拦截，检测，onTouchevent将被RecycleView拦截，在RecycleView上将无法拉出侧滑菜单
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                int nextX = (int) ev.getX();
                int nextY = (int) ev.getY();
                if (getScrollX() == 0 && (Math.abs(nextX-onInterceptStartX))<7 && Math.abs (nextY-onInterceptStartY)<5){//侧滑菜单出现并且是一个单击行为
                    return false;
                }else if(getScrollX() == 0 && ((Math.abs(nextX-onInterceptStartX))>=7 || Math.abs (nextY-onInterceptStartY)>=5)){//侧滑菜单出现并且不是一个单击行为
                    return true;
                }else if(onInterceptStartX-nextX>0){//从右向左滑动，不处理
                    return false;
                }else  if(nextX -onInterceptStartX>30 && Math.abs(nextY-onInterceptStartY)<=20){//从左向右拉超过30并且y差距小于20
                    return true;
                }
                return false;
            case MotionEvent.ACTION_DOWN:
                onInterceptStartX  = (int) ev.getX();
                onInterceptStartY = (int) ev.getY();
                onTouchStartX = (int) ev.getX();
                bx = (int) ev.getX();
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
//                left_menu.setTranslationX(200);//是设置view相对原始位置的偏移量
//                left_menu.setAlpha(0.5f);//改变透明度
//                left_menu.setScaleX(0.5f);//设置缩放基准点
//                left_menu.setScaleY(0.5f);//设置缩放基准点
//                left_menu.setPivotX(10);
//                left_menu.setPivotY(10);
                offsetX = (int) (event.getX() - bx);// x 方向偏移
                if (offsetX<0)
                if(linear_left.getWidth()/5<-offsetX) {//从右向左收回菜单并拉出部分超过五分之一
//                    TranslateAnimation animation = new TranslateAnimation(0,-200,0,0);
//                    animation.setFillAfter(true);
//                    animation.setDuration(500);
//                    startAnimation(animation);
                    scrollTo(linear_left.getWidth(),0);
                }else{
                    scrollTo(0,0);
                }
                if(offsetX>0)
                    if (offsetX > linear_left.getWidth() / 5) {//向左向右拉出菜单并拉出部分超过五分之一
                        scrollTo(0,0);
                    }else {
                        scrollTo(linear_left.getWidth(),0);
                    }
                invalidate();
                onTouchStartX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "onTouchEvent: onTouchEvent-> MotionEvent.ACTION_MOVE" );
                offsetX = (int) (event.getX() - onTouchStartX);// x 方向偏移量
                if(getScrollX() -offsetX<0){//从左向右拉出将超界
                    scrollTo(0,0);
                }else if(offsetX<0&&Math.abs(offsetX ) +getScrollX()>linear_left.getWidth()){//从右向左收回将超界
                    scrollTo(linear_left.getWidth(),0);
                }else if(getScrollX()<=linear_left.getWidth())//不超界正常偏移
                    scrollBy(-offsetX,0);
                invalidate();//更新视图
                onTouchStartX = (int) event.getX();
                break;
            case MotionEvent.ACTION_DOWN:
                onTouchStartX = (int) event.getX();
                bx = (int) event.getX();
                break;
        }
        return true;//return true 将拦截事件，不在向父类传递该事件，细节百度view事件分发机制
    }

    public View getLinearMore() throws  Exception{//返回Linear_more,方便调用类定制Linear_more
        if(topmenu_more !=null)
            return topmenu_more;
        else
            return null;
    }

}

