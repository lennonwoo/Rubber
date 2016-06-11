package com.lennonwoo.rubber.ui.widget;

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

public class CircleProgressView extends View {

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

    private RectF rectCircle;

    private Rect rectText;

    private float squareSide, squareCenter;

    private int currentProgress;

    private Handler progressHandler;

    private SongOperation songOperation;

    private Runnable runnableProgress = new Runnable() {
        @Override
        public void run() {
            if (!bePlaying) {
                return;
            }
            currentProgress++;
            if (currentProgress >= songDuration) {
                newSong = true;
                return;
            }
            postInvalidate();
            progressHandler.postDelayed(runnableProgress, PROGRESS_DELAY);
        }
    };

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        squareSide = Math.min(width, height);
        squareCenter = squareSide / 2;

        rectCircle.set(SPACING, SPACING, squareSide - SPACING, squareSide - SPACING);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bePlaying || firstDraw) {
            canvas.drawArc(rectCircle, 150, 240, false, paintEmptyProgress);
            if (currentProgress == 0) {
                canvas.drawArc(rectCircle, 150,
                        (float) (0.0001), false, paintEmptyProgress);
            } else {
                canvas.drawArc(rectCircle, 150,
                        (float) (240 * currentProgress * 1.0) / songDuration, false, paintLoadedProgress);
            }

            String passedTime = Utils.durationToString(currentProgress );
            paintTime.getTextBounds(passedTime, 0, passedTime.length(), rectText);
            canvas.drawText(passedTime,
                    (float) (squareCenter * Math.cos(Math.toRadians(35.0))) + squareSide / 2.0f - rectText.width() / 1.5f,
                    (float) (squareCenter * Math.sin(Math.toRadians(35.0))) + squareSide / 2.0f + rectText.height() + 15.0f,
                    paintTime);

            String leftTime = Utils.durationToString(songDuration - currentProgress);
            paintTime.getTextBounds(leftTime, 0, leftTime.length(), rectText);
            canvas.drawText(leftTime,
                    (float) (squareCenter * -Math.cos(Math.toRadians(35.0))) + squareSide / 2.0f - rectText.width() / 3f,
                    (float) (squareCenter * Math.sin(Math.toRadians(35.0))) + squareSide / 2.0f + rectText.height() + 15.0f,
                    paintTime);
        }
        if (firstDraw) {
            firstDraw = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                float downY = event.getY();
                float x = downX - squareCenter;
                float y = downY - squareCenter;
                Double d = Math.toDegrees(Math.atan2(y, x));
                if (d >= -150 && d <= -30) {
                    break;
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
                songOperation.seekSong(currentProgress);
                break;
        }
        return super.onTouchEvent(event);
    }

    public CircleProgressView setSongOperation(SongOperation songOperation) {
        this.songOperation = songOperation;
        return this;
    }

    public CircleProgressView setTimeTextColor(int timeTextColor) {
        this.timeTextColor = timeTextColor;
        return this;
    }

    public CircleProgressView setEmptyProgressColor(int emptyProgressColor) {
        this.emptyProgressColor = emptyProgressColor;
        return this;
    }

    public CircleProgressView setLoadedProgressColor(int loadedProgressColor) {
        this.loadedProgressColor = loadedProgressColor;
        return this;
    }

    public CircleProgressView setSongDuration(int songDuration) {
        this.songDuration = songDuration;
        return this;
    }

    public CircleProgressView begin() {
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
    }

    public void pause() {
        bePlaying = false;
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        //TODO -- getColor warning
        timeTextColor = ta.getColor(R.styleable.CircleProgressView_timeTextColor,
                getResources().getColor(R.color.black));
        timeTextSize = ta.getDimensionPixelSize(R.styleable.CircleProgressView_timeTextSize,
                getResources().getDimensionPixelSize(R.dimen.circle_progress_text_size));
        emptyProgressColor = ta.getColor(R.styleable.CircleProgressView_progressEmptyColor,
                getResources().getColor(R.color.gray));
        loadedProgressColor = ta.getColor(R.styleable.CircleProgressView_progressLoadedColor,
                getResources().getColor(R.color.colorAccent));
        ta.recycle();

        initPaint();

        rectCircle = new RectF();
        rectText = new Rect();

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

    }
}
