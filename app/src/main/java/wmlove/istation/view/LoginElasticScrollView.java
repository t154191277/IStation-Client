package wmlove.istation.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import wmlove.istation.R;

public class LoginElasticScrollView extends NestedScrollView {

    public LoginElasticScrollView(Context context) {
        super(context);
    }

    public LoginElasticScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private float mScrollRate = 0.5f;
    private int mTouchSlop;

    private float mLastX;
    private float mLastY;


    private boolean mScalingDown;
    private boolean mScalingUp;
    private boolean mUpHandle = true;
    private boolean mDownHandle = true;
    private boolean enableSlide = false;

    private View mTopHalfView;
    private View mBottomHalfView;
    private View mBackView;

    private int mTopWrapInitHeight = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTopWrapInitHeight == 0) {
            setTopViewInitHeight();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getScrollY() == 0) {
                    mLastX = event.getX();
                    mLastY = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mScalingDown) {
                    mScalingDown = false;
                    onReplyImage();
                    return true;
                } else if (mScalingUp) {
                    mScalingUp = false;
                    onReplyLayout();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float eventX = event.getX();
                float eventY = event.getY();

                float diffY = eventY - mLastY;

                mLastY = eventY;
                mLastX = eventX;

                if (getScrollY() == 0) {
                    //mTouchSlop解决点击问题
                    if (!mScalingDown && !mScalingUp) {
                        if (diffY > 0) {
                            mDownHandle = onRollingDown((int) (diffY * mScrollRate));
                            mScalingDown = true;
                        }

                        if (diffY < 0) {
                            mUpHandle = onRollingUp((int) (diffY * mScrollRate));
                            mScalingUp = true;
                        }
                    } else if (mScalingDown && mDownHandle) {
                        mDownHandle = onRollingDown((int) (diffY * mScrollRate));

                        if (!mDownHandle) {
                            mScalingDown = false;
                            mScalingUp = true;
                            mUpHandle = true;
                            onStateChange(false);
                        }
                    } else if (mScalingUp && mUpHandle) {
                        mUpHandle = onRollingUp((int) (diffY * mScrollRate));
                        if (!mUpHandle) {
                            mDownHandle = true;
                            mScalingUp = false;
                            mScalingDown = true;
                            onStateChange(true);
                        }
                    }
                    return true;
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    public LoginElasticScrollView setTopHalfView(View topView) {
        if (topView != null) {
            this.mTopHalfView = topView;
        }
        return this;
    }

    public LoginElasticScrollView setBottomHalfView(View bottomView) {
        if (bottomView != null) {
            this.mBottomHalfView = bottomView;
        }
        return this;
    }

    public LoginElasticScrollView setBackView(View backView) {
        if (backView != null) {
            this.mBackView = backView;
        }
        return this;
    }

    public void show() {
        if (mTopHalfView != null && mBottomHalfView != null && mBackView != null) {
            enableSlide = true;
        } else {
            try {
                enableSlide = false;
                throw new Exception("缺少参数，初始化失败！");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setTopViewInitHeight() {
        if (mTopHalfView == null || !enableSlide) {
            return;
        }
        mTopHalfView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mTopWrapInitHeight == 0) {//只初始化一遍
                    mTopWrapInitHeight = mTopHalfView.getMeasuredHeight();
                }
            }
        });
        mTopWrapInitHeight = mTopHalfView.getMeasuredHeight();
    }

    private void onReplyImage() {
        if (mTopHalfView == null || !enableSlide) {
            return;
        }
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-1, -1);
        final float h = mTopHalfView.getLayoutParams().height;// 图片当前高度
        final float newH = mTopWrapInitHeight;// 图片原高度
        // 设置动画
        ValueAnimator anim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration(180);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                lp.height = (int) (h - (h - newH) * cVal);
                mTopHalfView.setLayoutParams(lp);
            }
        });
        anim.start();
    }

    private void onReplyLayout() {
        if (mBottomHalfView == null || !enableSlide) {
            return;
        }
        ValueAnimator anim = ObjectAnimator.ofFloat(1.0F, 0.0F).setDuration(200);
        final ViewGroup.MarginLayoutParams mp = (ViewGroup.MarginLayoutParams) mBottomHalfView.getLayoutParams();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                if (cVal == 0.0) {
                    mp.topMargin = 0;
                } else {
                    float m = mp.topMargin > 0 ? (mTopWrapInitHeight - mp.topMargin) * -1 : mp.topMargin;
                    mp.topMargin = (int) (m * cVal);
                }
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mp);
                lp.addRule(RelativeLayout.BELOW, mTopHalfView.getId());
                mBottomHalfView.setLayoutParams(lp);

                if (mBackView != null || !enableSlide) {
                    ((ImageView) mBackView).setImageDrawable(getResources().getDrawable(R.drawable.goods_detail_serve));
                }

            }
        });
        anim.start();
    }

    private boolean onRollingUp(int diffY) {
        if (mBottomHalfView == null || !enableSlide) {
            return false;
        }

        boolean handle;
        ViewGroup.MarginLayoutParams mp = (ViewGroup.MarginLayoutParams) mBottomHalfView.getLayoutParams();
        mp.topMargin = mp.topMargin == 0.0 ? mTopWrapInitHeight : mp.topMargin;
        if (mp.topMargin > mTopWrapInitHeight) {
            mp.topMargin = mTopWrapInitHeight;
            handle = false;
        } else {
            mp.topMargin += diffY;
            handle = true;

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mp);
            mBottomHalfView.setLayoutParams(lp);
        }

        if (mBackView != null || !enableSlide) {
            if (Math.abs(mp.topMargin) < 300) {
                ((ImageView) mBackView).setImageDrawable(getResources().getDrawable(R.drawable.goods_detail_back));
            } else {
                ((ImageView) mBackView).setImageDrawable(getResources().getDrawable(R.drawable.goods_detail_serve));
            }
        }

        return handle;
    }

    private boolean onRollingDown(int diffY) {
        if (mTopHalfView == null || !enableSlide) {
            return false;
        }

        boolean handle = false;
        ViewGroup.LayoutParams lp = mTopHalfView.getLayoutParams();
        int height = lp.height;
        if (height + diffY < mTopWrapInitHeight) {
            lp.height = mTopWrapInitHeight;
        } else {
            lp.height += diffY;
            handle = true;
        }
        mTopHalfView.setLayoutParams(lp);
        return handle;
    }

    private void onStateChange(boolean isScalingDown) {
        if (mBottomHalfView == null || !enableSlide) {
            return;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBottomHalfView.getLayoutParams();
        if (isScalingDown) {
            lp.topMargin = 0;
            lp.addRule(RelativeLayout.BELOW, mTopHalfView.getId());
        }
        mBottomHalfView.setLayoutParams(lp);
    }

    private float interceptDownY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            interceptDownY = ev.getY();

            mLastX = ev.getX();
            mLastY = ev.getY();
        } else if (action == MotionEvent.ACTION_MOVE) {
            //解决蓝边问题
            float distanceY = ev.getY() - interceptDownY;

            distanceY = Math.abs(distanceY);
            if (distanceY > mTouchSlop) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}

