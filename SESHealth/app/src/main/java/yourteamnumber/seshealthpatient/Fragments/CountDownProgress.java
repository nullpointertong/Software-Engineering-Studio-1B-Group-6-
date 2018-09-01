package yourteamnumber.seshealthpatient.Fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import yourteamnumber.seshealthpatient.R;


public class CountDownProgress extends View {


    private static final int DEFAULT_CIRCLE_SOLIDE_COLOR = Color.parseColor("#FFFFFF");
    private static final int DEFAULT_CIRCLE_STROKE_COLOR = Color.parseColor("#D1D1D1");
    private static final int DEFAULT_CIRCLE_STROKE_WIDTH = 5;
    private static final int DEFAULT_CIRCLE_RADIUS =100;

    private static final int PROGRESS_COLOR = Color.parseColor("#F76E6B");
    private static final int PROGRESS_WIDTH = 5;

    private static final int SMALL_CIRCLE_SOLIDE_COLOR = Color.parseColor("#FFFFFF");
    private static final int SMALL_CIRCLE_STROKE_COLOR = Color.parseColor("#F76E6B");
    private static final int SMALL_CIRCLE_STROKE_WIDTH = 2;
    private static final int SMALL_CIRCLE_RADIUS = 6;

    private static final int TEXT_COLOR = Color.parseColor("#F76E6B");
    private static final int TEXT_SIZE = 40;


    private int defaultCircleSolideColor = DEFAULT_CIRCLE_SOLIDE_COLOR;
    private int defaultCircleStrokeColor = DEFAULT_CIRCLE_STROKE_COLOR;
    private int defaultCircleStrokeWidth = dp2px(DEFAULT_CIRCLE_STROKE_WIDTH);
    private int defaultCircleRadius = dp2px(DEFAULT_CIRCLE_RADIUS);//半径

    private int progressColor = PROGRESS_COLOR;
    private int progressWidth = dp2px(PROGRESS_WIDTH);

    private int smallCircleSolideColor = SMALL_CIRCLE_SOLIDE_COLOR;
    private int smallCircleStrokeColor = SMALL_CIRCLE_STROKE_COLOR;
    private int smallCircleStrokeWidth = dp2px(SMALL_CIRCLE_STROKE_WIDTH);
    private int smallCircleRadius = dp2px(SMALL_CIRCLE_RADIUS);

    private int textColor = TEXT_COLOR;
    private int textSize = sp2px(TEXT_SIZE);


    private Paint defaultCriclePaint;
    private Paint progressPaint;
    private Paint smallCirclePaint;
    private Paint textPaint;
    private Paint smallCircleSolidePaint;


    private float mStartSweepValue = -90;

    private float currentAngle;

    private long countdownTime;

    private String textDesc;

    private Path mPath;


    private float extraDistance = 0.7F;


    public CountDownProgress(Context context) {
        this(context,null);
    }

    public CountDownProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CountDownProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownProgress);
        int indexCount = typedArray.getIndexCount();
        for(int i=0;i<indexCount;i++){
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.CountDownProgress_default_circle_solide_color:
                    defaultCircleSolideColor = typedArray.getColor(attr, defaultCircleSolideColor);
                    break;
                case R.styleable.CountDownProgress_default_circle_stroke_color:
                    defaultCircleStrokeColor = typedArray.getColor(attr, defaultCircleStrokeColor);
                    break;
                case R.styleable.CountDownProgress_default_circle_stroke_width:
                    defaultCircleStrokeWidth = (int) typedArray.getDimension(attr, defaultCircleStrokeWidth);
                    break;
                case R.styleable.CountDownProgress_default_circle_radius:
                    defaultCircleRadius = (int) typedArray.getDimension(attr, defaultCircleRadius);
                    break;
                case R.styleable.CountDownProgress_progress_color:
                    progressColor = typedArray.getColor(attr, progressColor);
                    break;
                case R.styleable.CountDownProgress_progress_width:
                    progressWidth = (int) typedArray.getDimension(attr, progressWidth);
                    break;
                case R.styleable.CountDownProgress_small_circle_solide_color:
                    smallCircleSolideColor = typedArray.getColor(attr, smallCircleSolideColor);
                    break;
                case R.styleable.CountDownProgress_small_circle_stroke_color:
                    smallCircleStrokeColor = typedArray.getColor(attr, smallCircleStrokeColor);
                    break;
                case R.styleable.CountDownProgress_small_circle_stroke_width:
                    smallCircleStrokeWidth = (int) typedArray.getDimension(attr, smallCircleStrokeWidth);
                    break;
                case R.styleable.CountDownProgress_small_circle_radius:
                    smallCircleRadius = (int) typedArray.getDimension(attr, smallCircleRadius);
                    break;
                case R.styleable.CountDownProgress_text_color:
                    textColor = typedArray.getColor(attr, textColor);
                    break;
                case R.styleable.CountDownProgress_text_size:
                    textSize = (int) typedArray.getDimension(attr, textSize);
                    break;
            }
        }

        typedArray.recycle();

        setPaint();
    }

    private void setPaint() {

        defaultCriclePaint = new Paint();
        defaultCriclePaint.setAntiAlias(true);
        defaultCriclePaint.setDither(true);
        defaultCriclePaint.setStyle(Paint.Style.STROKE);
        defaultCriclePaint.setStrokeWidth(defaultCircleStrokeWidth);
        defaultCriclePaint.setColor(defaultCircleStrokeColor);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setDither(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        smallCirclePaint = new Paint();
        smallCirclePaint.setAntiAlias(true);
        smallCirclePaint.setDither(true);
        smallCirclePaint.setStyle(Paint.Style.STROKE);
        smallCirclePaint.setStrokeWidth(smallCircleStrokeWidth);
        smallCirclePaint.setColor(smallCircleStrokeColor);

        smallCircleSolidePaint = new Paint();
        smallCircleSolidePaint.setAntiAlias(true);
        smallCircleSolidePaint.setDither(true);
        smallCircleSolidePaint.setStyle(Paint.Style.FILL);
        smallCircleSolidePaint.setColor(smallCircleSolideColor);


        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    /**
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize;
        int heightSize;
        int strokeWidth = Math.max(defaultCircleStrokeWidth, progressWidth);
        if(widthMode != MeasureSpec.EXACTLY){
            widthSize = getPaddingLeft() + defaultCircleRadius*2 + strokeWidth + getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if(heightMode != MeasureSpec.EXACTLY){
            heightSize = getPaddingTop() + defaultCircleRadius*2 + strokeWidth + getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        canvas.drawCircle(defaultCircleRadius,defaultCircleRadius,defaultCircleRadius,defaultCriclePaint);



        canvas.drawArc(new RectF(0,0,defaultCircleRadius*2,defaultCircleRadius*2),mStartSweepValue, 360*currentAngle,false,progressPaint);



        float textWidth = textPaint.measureText(textDesc);
        float textHeight = (textPaint.descent() + textPaint.ascent()) / 2;
        canvas.drawText(textDesc, defaultCircleRadius-textWidth/2, defaultCircleRadius-textHeight, textPaint);


        float currentDegreeFlag = 360*currentAngle + extraDistance;
        float smallCircleX = 0,smallCircleY = 0;
        float hudu = (float) Math.abs(Math.PI * currentDegreeFlag / 180);
        smallCircleX = (float) Math.abs(Math.sin(hudu) * defaultCircleRadius + defaultCircleRadius);
        smallCircleY = (float) Math.abs(defaultCircleRadius - Math.cos(hudu) * defaultCircleRadius);
        canvas.drawCircle(smallCircleX, smallCircleY, smallCircleRadius, smallCirclePaint);
        canvas.drawCircle(smallCircleX, smallCircleY, smallCircleRadius - smallCircleStrokeWidth, smallCircleSolidePaint);//画小圆的实心

        canvas.restore();

    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }


    public void setCountdownTime(long countdownTime){
        this.countdownTime = countdownTime;
        textDesc ="";
    }

    public void startCountDownTime(final OnCountdownFinishListener countdownFinishListener){
        setClickable(false);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0f);

        animator.setDuration(countdownTime+1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(0);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                currentAngle = (float) animation.getAnimatedValue();

                invalidate();
            }
        });

        animator.start();

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if(countdownFinishListener != null){
                    countdownFinishListener.countdownFinished();
                }
                if(countdownTime > 0){
                    setClickable(true);
                }else{
                    setClickable(false);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        countdownMethod();
    }


    private void countdownMethod(){
        new CountDownTimer(countdownTime+1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                countdownTime = countdownTime-1000;
                textDesc = "";

                Log.e("time",countdownTime+"");

                invalidate();
            }
            @Override
            public void onFinish() {

                textDesc = " ";

                smallCirclePaint.setColor(getResources().getColor(android.R.color.transparent));
                smallCircleSolidePaint.setColor(getResources().getColor(android.R.color.transparent));

                invalidate();
            }
        }.start();
    }


    public interface OnCountdownFinishListener{
        void countdownFinished();
    }

}
