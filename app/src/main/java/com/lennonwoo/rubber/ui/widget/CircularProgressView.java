package com.lennonwoo.rubber.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lennonwoo.rubber.R;
import com.lennonwoo.rubber.utils.Utils;

public class CircularProgressView extends View {

    private static final int PROGRESS_DELAY = 1000;

    private static final float STROKE_WIDTH = 12.0f;

    private static final float SPACING = 10.0f;

    private Paint paintTime, paintEmptyProgress, paintLoadedProgress;

    private int timeTextColor, timeTextSize;

    private int emptyProgressColor, loadedProgressColor;

    private int songDuration;

    private boolean bePlaying;

    private boolean firstDraw;

    private boolean newSong;

    private RectF rectMusicProgressCircle;

    private Rect rectTimeText;

    private float squareSideLength, squareCenter;

    private int currentProgress;

    private Handler progressHandler;

    private SongOperation songOperation;

    private int touchFlag;

    private Runnable runnableProgress = new Runnable() {
        @Override
        public void run() {
            if (!bePlaying) {
                return;
            }
            if (currentProgress >= songDuration) {
                newSong = true;
                return;
            }
            currentProgress++;
            postInvalidate();
            progressHandler.postDelayed(runnableProgress, PROGRESS_DELAY);
        }
    };

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        squareSideLength = Math.min(width, height);
        squareCenter = squareSideLength / 2;

        rectMusicProgressCircle.set(SPACING, SPACING, squareSideLength - SPACING, squareSideLength - SPACING);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bePlaying || firstDraw) {
            canvas.drawArc(rectMusicProgressCircle, 150, 240, false, paintEmptyProgress);
            if (currentProgress != 0) {
                canvas.drawArc(rectMusicProgressCircle, 150,
                        (float) (240 * currentProgress * 1.0) / songDuration, false, paintLoadedProgress);
            }

            String leftTime = Utils.durationToString(songDuration - currentProgress);
            paintTime.getTextBounds(leftTime, 0, leftTime.length(), rectTimeText);
            canvas.drawText(leftTime,
                    (float) (squareCenter * Math.cos(Math.toRadians(35.0))) + squareSideLength / 2.0f - rectTimeText.width() / 1.5f,
                    (float) (squareCenter * Math.sin(Math.toRadians(35.0))) + squareSideLength / 2.0f + rectTimeText.height() + 15.0f,
                    paintTime);

            String passedTime = Utils.durationToString(currentProgress );
            paintTime.getTextBounds(passedTime, 0, passedTime.length(), rectTimeText);
            canvas.drawText(passedTime,
                    (float) (squareCenter * -Math.cos(Math.toRadians(35.0))) + squareSideLength / 2.0f - rectTimeText.width() / 3f,
                    (float) (squareCenter * Math.sin(Math.toRadians(35.0))) + squareSideLength / 2.0f + rectTimeText.height() + 15.0f,
                    paintTime);
        }
        if (firstDraw) {
            firstDraw = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float downX = event.getX();
        float downY = event.getY();
        float x = downX - squareCenter;
        float y = -(downY - squareCenter);
        Double d = Math.toDegrees(Math.atan2(y, x));
        if (d >= -150 && d <= -30) {
            // TODO try to move this ugly touchFlag and figure out why onTouchEvent will be called twice??
            touchFlag++;
            if (touchFlag == 2) {
                songOperation.startPauseSong();
                touchFlag = 0;
            }
            return true;
        } else if(d >= -180 && d <= -150) {
            d += 180;
            d = 30 - d;
        } else if(d >= 0 && d <= 180) {
            d = 180 - d;
            d += 30;
        } else {
            d = -d;
            d += 210;
        }
        currentProgress = (int) ((d / 240) * songDuration);
        invalidate();
        songOperation.seekSong(currentProgress);
        return true;
    }

    public CircularProgressView setSongOperation(SongOperation songOperation) {
        this.songOperation = songOperation;
        return this;
    }

    public CircularProgressView setTimeTextColor(int timeTextColor) {
        this.timeTextColor = timeTextColor;
        paintTime.setColor(timeTextColor);
        return this;
    }

    public CircularProgressView setEmptyProgressColor(int color) {
        ValueAnimator emptyProgressColorAnim = ValueAnimator.ofArgb(this.emptyProgressColor, color);
        emptyProgressColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                emptyProgressColor = (int) animation.getAnimatedValue();
                paintEmptyProgress.setColor(emptyProgressColor);
                invalidate();
            }
        });
        emptyProgressColorAnim.setDuration(1000);
        emptyProgressColorAnim.start();
        return this;
    }

    public CircularProgressView setLoadedProgressColor(int loadedProgressColor) {
        this.loadedProgressColor = loadedProgressColor;
        paintLoadedProgress.setColor(loadedProgressColor);
        return this;
    }

    public CircularProgressView setSongDuration(int songDuration) {
        this.songDuration = songDuration;
        return this;
    }

    public void setSongPosition(int songPosition) {
        this.currentProgress = songPosition;
    }

    public CircularProgressView begin() {
        if (newSong) {
            progressHandler.postDelayed(runnableProgress, PROGRESS_DELAY);
            newSong = false;
        }
        currentProgress = 0;
        bePlaying = true;
        return this;
    }

    public void start() {
        bePlaying = true;
        progressHandler.postDelayed(runnableProgress, PROGRESS_DELAY);
        invalidate();
    }

    public void pause() {
        bePlaying = false;
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressView);
        timeTextColor = ta.getColor(R.styleable.CircularProgressView_timeTextColor,
                Utils.getColor(context, R.color.black));
        timeTextSize = ta.getDimensionPixelSize(R.styleable.CircularProgressView_timeTextSize,
                getResources().getDimensionPixelSize(R.dimen.circle_progress_text_size));
        emptyProgressColor = ta.getColor(R.styleable.CircularProgressView_progressEmptyColor,
                Utils.getColor(context, R.color.gray));
        loadedProgressColor = ta.getColor(R.styleable.CircularProgressView_progressLoadedColor,
                Utils.getColor(context, R.color.colorAccent));
        ta.recycle();

        initPaint();

        rectMusicProgressCircle = new RectF();
        rectTimeText = new Rect();

        progressHandler = new Handler();

        bePlaying = true;
        firstDraw = true;
        newSong = true;
        //initialize value to initialize onDraw()
        currentProgress = 0;
        songDuration = 1;
    }

    private void initPaint() {
        paintTime = new Paint();
        paintTime.setColor(timeTextColor);
        paintTime.setAntiAlias(true);
        paintTime.setTextSize(timeTextSize);

        paintEmptyProgress = new Paint();
        paintEmptyProgress.setColor(emptyProgressColor);
        paintEmptyProgress.setAntiAlias(true);
        paintEmptyProgress.setStyle(Paint.Style.STROKE);
        paintEmptyProgress.setStrokeWidth(STROKE_WIDTH);

        paintLoadedProgress = new Paint();
        paintLoadedProgress.setColor(loadedProgressColor);
        paintLoadedProgress.setAntiAlias(true);
        paintLoadedProgress.setStyle(Paint.Style.STROKE);
        paintLoadedProgress.setStrokeWidth(STROKE_WIDTH);
    }

    public interface SongOperation {

        void seekSong(int progress);

        void startPauseSong();

    }
}
