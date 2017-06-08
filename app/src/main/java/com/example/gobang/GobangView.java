package com.example.gobang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 雪无痕 on 2017/6/8.
 */

public class GobangView extends View {


    public static int NO_WIN =2 ;
    public static  int WHITE_WIN =0 ;
    public static  int BLACK_WIN = 1;
    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;
    private int MAX_COUNT_IN_LINE = 5;

    private float piecesSize = 3 * 1.0f / 4;

    private Paint mPaint = new Paint();
    //棋子
    private Bitmap mWhitePieces;
    private Bitmap mBlackPieces;

    //棋子的集合
    private boolean mIsWhitePieces = true;
    private ArrayList<Point> mWhiteList = new ArrayList<>();
    private ArrayList<Point> mBlackList = new ArrayList<>();

    //判断游戏是否结束
    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;


    public GobangView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mPaint.setColor(Color.GRAY);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        mWhitePieces = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPieces = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);
    }

    //尺寸大小
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = (mPanelWidth * 1.0f / MAX_LINE);

        int piecesWidth = (int) (mLineHeight * piecesSize);
        mWhitePieces = Bitmap.createScaledBitmap(mWhitePieces, piecesWidth, piecesWidth, false);
        mBlackPieces = Bitmap.createScaledBitmap(mBlackPieces, piecesWidth, piecesWidth, false);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            Point point = getValidPoint(x, y);
            if (mWhiteList.contains(point) || mBlackList.contains(point)) {
                return false;
            }
            if (mIsWhitePieces) {
                mWhiteList.add(point);
            } else {
                mBlackList.add(point);
            }
            invalidate();
            mIsWhitePieces = !mIsWhitePieces;
        }

        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPanelBorad(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    // 用于回调的接口
    public interface onGameListener {
        void onGameOver(int i);
    }

    private onGameListener onGameListener;

    //自定义接口，用于显示dialog
    public void setOnGameListener(GobangView.onGameListener onGameListener) {
        this.onGameListener = onGameListener;
    }

    private void checkGameOver() {

        boolean whiteWin = checkFindInLine(mWhiteList);
        boolean blackWin = checkFindInLine(mBlackList);
        boolean  noWin = checkNoWin(whiteWin, blackWin);
        if (whiteWin || blackWin ||noWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;
            if (onGameListener != null) {

                if(whiteWin){
                    onGameListener.onGameOver(WHITE_WIN);
                }else if(blackWin){
                    onGameListener.onGameOver(BLACK_WIN);
                }else if(noWin){
                    onGameListener.onGameOver(NO_WIN);
                }
                //onGameListener.onGameOver(whiteWin ? WHITE_WIN : BLACK_WIN);
                }
            /*String text = mIsWhiteWinner ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();*/
        }

    }
    private boolean checkNoWin(boolean whiteWin, boolean blackWin) {
        if (whiteWin || blackWin) {
            return false;
        }
        int max = MAX_LINE * MAX_LINE;
        //如果白棋和黑棋的总数等于棋盘格子数,说明和棋
        if (mWhiteList.size() + mBlackList.size() == max) {
            return true;
        }
        return false;
    }
    private boolean checkFindInLine(List<Point> points) {

        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x, y, points);
            if (win) {
                return true;
            }
            win = checkVertical(x, y, points);
            if (win) {
                return true;
            }
            win = checkLeftOblique(x, y, points);
            if (win) {
                return true;
            }

            win = checkRightOblique(x, y, points);
            if (win) {
                return true;
            }

        }


        return false;
    }

    //判断x,y棋子的位置  是否横向相邻5个一致
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //左
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }

        //右
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }

        return false;
    }

    private boolean checkLeftOblique(int x, int y, List<Point> points) {
        int count = 1;
        //左上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //左下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }

        return false;
    }

    private boolean checkRightOblique(int x, int y, List<Point> points) {
        int count = 1;
        //右上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - 1, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //右下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }

        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        //上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }

        return false;
    }

    private void drawPieces(Canvas canvas) {
        for (int i = 0, n = mWhiteList.size(); i < n; i++) {
            Point pointWhite = mWhiteList.get(i);
            canvas.drawBitmap(mWhitePieces, (pointWhite.x + (1 - piecesSize) / 2) * mLineHeight,
                    (pointWhite.y + (1 - piecesSize) / 2) * mLineHeight, null);
        }

        for (int i = 0, n = mBlackList.size(); i < n; i++) {
            Point pointBlack = mBlackList.get(i);
            canvas.drawBitmap(mBlackPieces, (pointBlack.x + (1 - piecesSize) / 2) * mLineHeight,
                    (pointBlack.y + (1 - piecesSize) / 2) * mLineHeight, null);
        }

    }

    private void drawPanelBorad(Canvas canvas) {

        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }

    }

    public void start() {
        mBlackList.clear();
        mWhiteList.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_LIST = "instance_white_list";
    private static final String INSTANCE_BLACK_LIST = "instance_black_list";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_LIST, mWhiteList);
        bundle.putParcelableArrayList(INSTANCE_BLACK_LIST, mBlackList);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteList = bundle.getParcelableArrayList(INSTANCE_WHITE_LIST);
            mBlackList = bundle.getParcelableArrayList(INSTANCE_BLACK_LIST);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }

        super.onRestoreInstanceState(state);
    }
}
