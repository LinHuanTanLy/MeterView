package com.ly.qr.meter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;

import com.ly.qr.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.PI;

/**
 * 表盘
 */
public class MeterView extends View {
    // 通用画笔
    private Paint mPaintForComment;
    // 画圈圈的画笔
    private Paint mPaintForCircle;
    private Paint mPaintForText;
    // 阴影得画笔  其实这个画笔可以复用
    private Paint mPaintForShader;
    // 弧圈的配置属性
    private float mWidthForCircle = 30;
    private int mColorForCircle = Color.parseColor("#F9F9F9");
    //进度条渐变色
    private int mColorProgressStart = Color.parseColor("#97e0fb");
    private int mColorProgressEnd = Color.parseColor("#97f6e5");

    //字体颜色--普通情况下的字体颜色
    private int mColorForText = Color.parseColor("#63BAFF");
    //字体颜色--有背景圆圈情况下的字体颜色
    private int mColorForTextWithTarget = Color.parseColor("#ffffff");

    // 弧度的配置属性
    private float mScaleFontSize;

    /**
     * 标志卡的位置
     */
    private int mDefSignIndex = -1;

    private int mTargetIndex = -1;
    // 中心
    private int mDx, mDy;
    // 半径
    private float mRadius;
    // 默认padding
    private float mPadding;
    // 默认大小
    private static final int DEF_SIZE = 300;
    // 默认圆环的宽度
    private float mDefNumberCircleRadius = 4;
    // 阴影圆环的宽度
    private int mDefOutSizeCircleWidth = 70;
    // 标记打勾的图片大小
    private float mDefTargetImgSize = 14;
    // 默认的中心圆图片
    private int mResIdForCup = R.mipmap.icon_kongbei;
    // 打卡图片
    private Bitmap mTargetBitmap;
    /**
     * 是否打卡的list
     */
    private SparseBooleanArray mPunchList = new SparseBooleanArray();

    /**
     * 显示的数据
     */
    private List<String> mScaleMsgList = new ArrayList<>();


    private static final String TAG = "lht";
    private PaintFlagsDrawFilter pfd;

    private int mStartPointX = -360, mStartPointY = -360, mEndPointX = 360, mEndPointY = 360;

    public MeterView(Context context) {
        super(context);
        init();
    }

    public MeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initUserAttrs(attrs); //获取自定义的属性
    }

    public MeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initUserAttrs(attrs); //获取自定义的属性
    }

    private void initUserAttrs(AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.MeterView);
            mPadding = array.getDimension(R.styleable.MeterView_def_padding, dip2px(getContext(), 10));
            mScaleFontSize = array.getDimension(R.styleable.MeterView_def_font_size, dip2px(getContext(), 8));
            mColorForText = array.getColor(R.styleable.MeterView_def_font_color, Color.parseColor("#64BAFF"));
            mColorForCircle = array.getColor(R.styleable.MeterView_def_circle_color, Color.parseColor("#F9F9F9"));
            mWidthForCircle = array.getDimension(R.styleable.MeterView_def_circle_width, 30);
            mColorProgressStart = array.getColor(R.styleable.MeterView_def_progress_gradient_start, Color.parseColor("#97e0fb"));
            mColorProgressEnd = array.getColor(R.styleable.MeterView_def_progress_gradient_end, Color.parseColor("#97f6e5"));
            mDefNumberCircleRadius = array.getDimension(R.styleable.MeterView_def_number_circle_radius, dip2px(getContext(), 10));
            mDefTargetImgSize = array.getDimension(R.styleable.MeterView_def_target_img_size, dip2px(getContext(), 20));
        } catch (Exception e) {
            mScaleFontSize = dip2px(getContext(), 8);
            mPadding = dip2px(getContext(), 10);
            e.printStackTrace();
        }
        if (array != null) {
            array.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDx = w / 2;
        mDy = h / 2;
        mDefOutSizeCircleWidth = (int) (mDx * 0.15);
        mWidthForCircle = (float) (mDefOutSizeCircleWidth * 0.5);
        mRadius = (h - mWidthForCircle * 2) / 2 - mDefOutSizeCircleWidth;

    }

    private void init() {
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        // 添加测试数据   true表示有打卡 false表示没
        mPunchList.put(1, false);
        mPunchList.put(2, false);
        mPunchList.put(3, false);
        mPunchList.put(4, false);
        mPunchList.put(5, false);
        mPunchList.put(6, false);
        mPunchList.put(7, false);
        mPunchList.put(8, false);

        // 默认弧度数据
        mScaleMsgList.add("8");
        mScaleMsgList.add("1");
        mScaleMsgList.add("2");
        mScaleMsgList.add("3");
        mScaleMsgList.add("4");
        mScaleMsgList.add("5");
        mScaleMsgList.add("6");
        mScaleMsgList.add("7");

        mPaintForComment = new Paint();
        mPaintForComment.setAntiAlias(true);
        mPaintForCircle = new Paint();
        mPaintForCircle.setAntiAlias(true);
        mPaintForText = new Paint();
        mPaintForText.setAntiAlias(true);
        mPaintForShader = new Paint();
        mPaintForShader.setAntiAlias(true);
        mPaintForText.setStrokeWidth(SizeUtil.Dp2Px(getContext(), 1));

        mPaintForText.setAntiAlias(true);
        mPaintForText.setDither(true);
    }


//    mColorForCircle = array.getColor(R.styleable.MeterView_def_circle_color, Color.parseColor("#F9F9F9"));
//    mWidthForCircle = array.getDimension(R.styleable.MeterView_def_circle_width, 30);
//    mColorProgressStart = array.getColor(R.styleable.MeterView_def_progress_gradient_start, Color.parseColor("#97e0fb"));
//    mColorProgressEnd =

    /**
     * 修改字体大小
     *
     * @param fontSize
     */
    public void setScaleFontSize(float fontSize) {
        mScaleFontSize = dip2px(getContext(), fontSize);
        invalidate();
    }

    /**
     * 修改字体颜色
     *
     * @param color
     */
    public void setScaleFontColor(int color) {
        mColorForText = color;
        invalidate();
    }

    /**
     * 修改默认底色
     *
     * @param color
     */
    public void setDefCircleColor(int color) {
        mColorForCircle = color;
        invalidate();
    }

    /**
     * 修改弧度宽度
     *
     * @param width
     */
    public void setDefCircleWidth(int width) {
        mWidthForCircle = width;
        invalidate();
    }

    /**
     * 设置圆弧渐变色起始色 -起色
     *
     * @param colorStart
     */
    public void setProgressStart(int colorStart) {
        mColorProgressStart = colorStart;
        invalidate();
    }

    /**
     * 设置圆弧渐变色起始色 -尾色
     *
     * @param colorEnd
     */
    public void setProgressEnd(int colorEnd) {
        mColorProgressEnd = colorEnd;
        invalidate();
    }

    /**
     * 更新数据
     *
     * @param punchList 对应一个长度为8的list，泛型为bool ，true表示已经打卡 false表示未打卡
     */
    public void updatePunchListWithBool(List<Boolean> punchList) {
        if (punchList != null && punchList.size() == 8) {
            mPunchList.clear();
//            mPunchList.addAll(punchList);
            invalidate();
        } else {
            Log.d("lht", "updatePunchList: 数据非法");
        }
    }

    /**
     * 更新数据
     *
     * @param punchList
     */
    public void updatePunchList(List<Integer> punchList) {
        if (Empty.check(punchList)) {
            // 传入一个空集 说明一点打卡都没
            resetToDef();
        } else {
            resetToDef();
            for (int index :
                    punchList) {
                mPunchList.put(index, true);
            }
            updateSign(Collections.max(punchList));

        }
        invalidate();
    }

    /**
     * 设置 下一个目标的方法
     *
     * @param index
     */
    public void updateTargetIndex(int index) {
//        index = 6;
//        if (index==mPunchList.keyAt(mPunchList.size()-1)){
//            return;
//        }
        if (index == 8) {
            mTargetIndex = 0;
        } else {
            mTargetIndex = index;
        }
        invalidate();
    }

    public void updateCenterImg(int resId) {
        if (resId != -1) {
            mResIdForCup = resId;
            invalidate();
        }
    }

    /**
     * 重置为默认数据
     */
    private void resetToDef() {
        mPunchList.put(1, false);
        mPunchList.put(2, false);
        mPunchList.put(3, false);
        mPunchList.put(4, false);
        mPunchList.put(5, false);
        mPunchList.put(6, false);
        mPunchList.put(7, false);
        mPunchList.put(8, false);
        mDefSignIndex = -1;
        mTargetIndex = -1;
    }

    /**
     * 更新刻度文案 也就是 1-8那几个
     *
     * @param scaleList 对应一个长度为8的list，泛型为String
     */
    public void updateScaleMsgList(List<String> scaleList) {
        if (scaleList != null && scaleList.size() == 8) {
            mScaleMsgList.clear();
            mScaleMsgList.addAll(scaleList);
            invalidate();
        } else {
            Log.d("lht", "updatePunchList: 数据非法");
        }
    }

    /**
     * 标志打卡的位置   也就是显示打勾的那个地方
     */
    public void updateSign(int signIndex) {
        if (signIndex >= 0 && signIndex <= 7) {
            mDefSignIndex = signIndex - 1;

            if (mDefSignIndex + 2 < 8) {
            }
        } else {
            mDefSignIndex = -1;
        }
//        updateTargetIndex(tempUpdateTargetIndex);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(pfd);
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(mDx, mDy);
//        drawSystem(canvas);
        drawShader(canvas);
        drawCenterImg(canvas);
        drawCircle(canvas);
        drawNumbers(canvas);
        drawProgress(canvas);
        drawScaleImg(canvas);
        canvas.restore();
    }

    /**
     * 画阴影
     *
     * @param canvas
     */
    private void drawShader(Canvas canvas) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaintForShader.setShadowLayer(20, 1, 1, Color.parseColor("#3363BAFF"));
        mPaintForShader.setAntiAlias(true);
        mPaintForShader.setColor(Color.WHITE);
        mPaintForShader.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, mRadius + mDefOutSizeCircleWidth, mPaintForShader);
    }

    /**
     * 画辅助坐标系
     *
     * @param canvas
     */
    private void drawSystem(Canvas canvas) {
        canvas.drawLine(-mDx, 0, mDx, 0, mPaintForComment);
        canvas.drawLine(0, -mDy, 0, mDy, mPaintForComment);
    }

    /**
     * 画进度条到了哪天打卡
     *
     * @param canvas
     */
    private void drawScaleImg(Canvas canvas) {
        canvas.save();
        Bitmap scaleImg = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_tick);
        int width = (int) (Math.min(scaleImg.getWidth(), mDefTargetImgSize) * 0.90);
        int height = (int) (Math.min(scaleImg.getHeight(), mDefTargetImgSize) * 0.90);
        mTargetBitmap = Bitmap.createScaledBitmap(scaleImg, width, height, true);
        scaleImg.recycle();
        int allNumbers = mPunchList.size();
        int single = (360 / allNumbers);
        if (mDefSignIndex >= 0 && mDefSignIndex <= 7 && mTargetBitmap != null) {
            double radian = 2 * PI / 360 * (360 - single * (1 - mDefSignIndex));
            int xD = (int) (Math.cos(radian) * mRadius);
            int yD = (int) (Math.sin(radian) * mRadius);
            Rect rect = new Rect(xD - width / 2, yD - height / 2, xD + width / 2, yD + height / 2);
            mPaintForComment.setAntiAlias(true);
            canvas.drawBitmap(mTargetBitmap, null, rect, mPaintForComment);
        }
        canvas.restore();
    }

    /**
     * 画居中的图片
     *
     * @param canvas
     */
    private void drawCenterImg(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mResIdForCup);
        int width = (bitmap.getWidth());
        int height = (bitmap.getHeight());
        int realW = (int) ((width * mDx / height) * 0.9);
        Rect rect = new Rect(-realW * 2 / 3, (int) (-mRadius * 2 / 3), realW * 2 / 3, (int) (mRadius * 2 / 3));
        canvas.drawBitmap(bitmap, null, rect, mPaintForComment);
    }


    /**
     * 画进度条
     *
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        int[] colors = {mColorProgressStart, mColorProgressEnd};
        LinearGradient linearGradient = new LinearGradient(-mStartPointX, mStartPointY, mEndPointX, mEndPointY,
                colors,
                null, Shader.TileMode.REPEAT);
//        mPaintForComment.setShader(linearGradient);
        mPaintForComment.setAntiAlias(true);
        mPaintForComment.setStrokeWidth(mWidthForCircle);
        mPaintForComment.setStyle(Paint.Style.STROKE);
        mPaintForComment.setStrokeCap(Paint.Cap.ROUND);

        RectF f = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        int angle = 360 / mPunchList.size();
        for (int i = 1; i <= mPunchList.size(); i++) {
            if (mPunchList.get(i)) {
                mPaintForComment.setShader(linearGradient);
                mPaintForComment.setStrokeCap(Paint.Cap.ROUND);
                canvas.drawArc(f, (i - 3) * angle, angle, false, mPaintForComment);
            }
        }
    }

    /**
     * 画基础的圆形 也就是默认的没打卡的点
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        mPaintForCircle.setAntiAlias(true);
        mPaintForCircle.setStrokeWidth(mWidthForCircle);
        mPaintForCircle.setColor(mColorForCircle);
        mPaintForCircle.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(0, 0, mRadius, mPaintForCircle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidth = DEF_SIZE;
        int measureHeight = DEF_SIZE;
        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    /**
     * 绘制进度刻度
     *
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {
        int singleAngle = 360 / mPunchList.size();
        for (int i = 0; i < mScaleMsgList.size(); i++) {
            mPaintForText.setTextSize(mScaleFontSize);
            String text = mScaleMsgList.get(i);
            Rect textBound = new Rect();
            mPaintForText.getTextBounds(text, 0, text.length(), textBound);
            canvas.save();
            canvas.translate(0, -mRadius + dip2px(getContext(), 2) + mPadding + ((textBound.bottom - textBound.top) >> 1));
            canvas.rotate(-singleAngle * i);
            if (i == mTargetIndex) {
                mPaintForCircle.setColor(mColorForText);
                mPaintForText.setColor(mColorForTextWithTarget);
                mPaintForCircle.setStyle(Paint.Style.FILL);
                mPaintForCircle.setAntiAlias(true);
                canvas.drawCircle(0, 0, mDefNumberCircleRadius * 0.95f, mPaintForCircle);
            } else {
                mPaintForText.setColor(mColorForText);
            }
            canvas.drawText(text, ((float) (textBound.right + textBound.left) / -2), ((float) -(textBound.bottom + textBound.top) / 2), mPaintForText);
            canvas.restore();
            canvas.rotate(singleAngle);
        }
    }

    /**
     * 25、描述：根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    /**
     * 26、描述：根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
