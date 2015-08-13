package com.ayit.arcmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by Sweetheart on 2015/8/7 20:26.
 * Email: 378398018@qq.com
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {

    private static final int DEFAULT_RADIUS = 100;
    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;
    private static final int DEFAULT_DURATION=300;
    /**
     * 菜单半径
     */
    private int mRadius;

    /**
     * 菜单主Button
     */
    private View menuBtn;
    /**
     * 默认菜单显示位置
     */
    private Position mPosition = Position.RIGHT_BOTTTOM;
    /**
     * default close
     */
    private Status mStatus=Status.CLOSE;

    /**
     * 菜单位置枚举类
     */
    public enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTTOM
    }

    /**
     * 状态枚举类
     */
    public enum Status {
        OPEN, CLOSE
    }

    private OnmenuItemClickListener itemClickListener;

    public void setOnItemClickListener(OnmenuItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    interface OnmenuItemClickListener{
        public void onClick(View v, int pos);
    }




    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RADIUS,
                getResources().getDisplayMetrics());
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcMenu, defStyleAttr, 0);
            mRadius = (int) typedArray.getDimension(R.styleable.ArcMenu_radius, mRadius);
            int pos = typedArray.getInt(R.styleable.ArcMenu_position, POS_RIGHT_BOTTOM);
            switch (pos) {
                case POS_LEFT_TOP:
                    mPosition = Position.LEFT_TOP;
                    break;
                case POS_LEFT_BOTTOM:
                    mPosition = Position.LEFT_BOTTOM;
                    break;
                case POS_RIGHT_TOP:
                    mPosition = Position.RIGHT_TOP;
                    break;
                case POS_RIGHT_BOTTOM:
                    mPosition = Position.RIGHT_BOTTTOM;
                    break;
            }

        } finally {

            typedArray.recycle();
        }

        Log.i("TAG", "" + mPosition + "_" + mRadius);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        //测量每一个子控件
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {

            layoutMenuBtn();
            // start child layout

            int childCount = getChildCount();
            for (int i = 0; i <childCount-1 ; i++) {
                View childView = getChildAt(i + 1);
                //As default close,default gone
                childView.setVisibility(View.GONE);
                //Calculate the average angle
                double angle=Math.PI/2/(childCount-2);
                int cl= (int) (mRadius*Math.sin(angle*i));
                int ct= (int) (mRadius*Math.cos(angle*i));

                int cWith = childView.getMeasuredWidth();
                int cHeight = childView.getMeasuredHeight();

                if(mPosition==Position.LEFT_BOTTOM || mPosition==Position.RIGHT_BOTTTOM){
                    ct=getMeasuredHeight()-cHeight-ct;
                }
                if(mPosition==Position.RIGHT_TOP || mPosition==Position.RIGHT_BOTTTOM){
                    cl=getMeasuredWidth()-cWith-cl;
                }

                childView.layout(cl,ct,cl+cWith,ct+cHeight);

            }


        }
    }

    /**
     * layout menuBtn
     */
    private void layoutMenuBtn() {
        menuBtn = getChildAt(0);
        menuBtn.setOnClickListener(this);

        int width = menuBtn.getMeasuredWidth();
        int height = menuBtn.getMeasuredHeight();
        //menuBtn的左和上的位置
        int l=0;
        int t=0;

        switch (mPosition) {
            case LEFT_TOP:
                l=0;
                t=0;
                break;
            case LEFT_BOTTOM:
                l=0;
                t=getMeasuredHeight()-height;
                break;
            case RIGHT_TOP:
                l=getMeasuredWidth()-width;
                t=0;

                break;
            case RIGHT_BOTTTOM:
                l=getMeasuredWidth()-width;
                t=getMeasuredHeight()-height;

                break;
        }

        menuBtn.layout(l, t, l + width, t + height);

    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        rotateMenuBtn(v,0f,720f,500);

        toogleMenu(DEFAULT_DURATION);
    }

    /**
     *切换菜单
     * @param duration
     */
    public void toogleMenu(int duration) {
        //为menuItem添加平移动画和旋转动画
        int count = getChildCount();
        for (int i = 0; i < count-1; i++) {

            final View childView= getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);
            //Calculate the average angle
            double angle=Math.PI/2/(count-2);
            //start
            // end 0,0
            int cl= (int) (mRadius*Math.sin(angle*i));
            int ct= (int) (mRadius*Math.cos(angle*i));
            int xflag=1;
            int yflag=1;
            if(mPosition==Position.LEFT_TOP || mPosition==Position.LEFT_BOTTOM){
                xflag=-1;
            }
            if(mPosition==Position.LEFT_TOP || mPosition==Position.RIGHT_TOP){
                yflag=-1;
            }

            AnimationSet set = new AnimationSet(true);
            Animation tranAnim=null;
            if(mStatus==Status.CLOSE){//to open
                tranAnim=new TranslateAnimation(xflag*cl,0,yflag*ct,0);
                childView.setClickable(true);
                childView.setFocusable(true);
            }else{//to close
                tranAnim=new TranslateAnimation(0,xflag*cl,0,yflag*ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            tranAnim.setDuration(duration);
            tranAnim.setFillAfter(true);
            //设置动画延迟时间
            tranAnim.setStartOffset((i * 100) / count);

            tranAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(mStatus==Status.CLOSE){
                        childView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            //RotateAnimation

            RotateAnimation rotateAnimation=new RotateAnimation(0,720, Animation.RELATIVE_TO_SELF,
                    0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation.setDuration(duration);
            rotateAnimation.setFillAfter(true);

            set.addAnimation(rotateAnimation);
            set.addAnimation(tranAnim);
            childView.startAnimation(set);

            final  int pos=i+1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener!=null)
                            itemClickListener.onClick(childView,pos);

                    menuItemAnimation(pos-1);
                    changeStatus();

                }
            });

        }

        //切换菜单状态
        changeStatus();

    }

    /**
     * 判断菜单是否打开
     * @return
     */
    public  boolean isOpen(){
        return mStatus == Status.OPEN;
    }



    /**
     * 切换菜单状态
     */
    private void changeStatus() {
       mStatus=(mStatus==Status.CLOSE? Status.OPEN : Status.CLOSE);
    }


    private void menuItemAnimation(int pos) {
        for (int i = 0; i <getChildCount()-1 ; i++) {
            View childView = getChildAt(i + 1);
            if(i==pos){
                childView.startAnimation(scaleBigAnim(300));
            }else {
                childView.startAnimation(scaleSmallAnim(300));
            }
        }
    }

    /**
     * 为选中的item设置放大 透明度降低的动画
     * @param duration
     * @return
     */
    private Animation scaleBigAnim(int duration){
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 3.0f, 1.0f, 3.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }


    /**
     * 为未选中的item设置缩小 透明度降低的动画
     * @param duration
     * @return
     */
    private Animation scaleSmallAnim(int duration){
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }



    /**
     * menuBtn RotateAnimation
     * @param v
     * @param start
     * @param end
     * @param duration
     */
    private void rotateMenuBtn(View v, float start, float end, int duration){
        RotateAnimation rotateAnimation=new RotateAnimation(start,end, Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setFillAfter(true);
        v.startAnimation(rotateAnimation);
    }

}
