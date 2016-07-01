package com.wuzhiqi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wuzhiqi.Point;
import com.wuzhiqi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：guofeng
 * ＊ 日期:16/7/1
 */
public class WUZhiQIView extends View {
    /**
     * 控件的宽度
     */
    private int width;
    /**
     * 每行的高度
     */
    private float lineHeight;
    /**
     * 每个行距一半高度
     */
    private float halfLineHeight;
    /**
     * 行数
     */
    private int lineCount = 10;
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 黑棋子的集合
     */
    private List<Point> blackPiece = new ArrayList<>();
    /**
     * 白棋子的集合
     */
    private List<Point> whitePiece = new ArrayList<>();
    /**
     * 白棋子的bitmap对象
     */
    private Bitmap whiteBitmap;
    /**
     * 黑棋子的bitmap对象
     */
    private Bitmap blackBitmap;
    /**
     * 当前是否白棋走
     */
    private boolean isCurWhite;

    public WUZhiQIView(Context context) {
        this(context, null);
    }

    public WUZhiQIView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public WUZhiQIView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#FF0000"));
        paint.setDither(true);
        whiteBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_white_piece);
        blackBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_black_piece);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.lineHeight = width * 1.0f / lineCount;
        this.halfLineHeight = lineHeight / 2;
        whiteBitmap = Bitmap.createScaledBitmap(whiteBitmap, (int) halfLineHeight, (int) halfLineHeight, true);
        blackBitmap = Bitmap.createScaledBitmap(blackBitmap, (int) halfLineHeight, (int) halfLineHeight, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = height;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            height = width;
        }
        int realWidth = Math.min(width, height);
        setMeasuredDimension(realWidth, realWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPiece(canvas);
    }

    private void drawPiece(Canvas canvas) {
        //绘制面板
        for (int i = 0; i < lineCount; i++) {
            //绘制列
            canvas.drawLine(halfLineHeight + lineHeight * i, halfLineHeight, halfLineHeight + lineHeight * i, width - halfLineHeight, paint);
            //绘制行
            canvas.drawLine(halfLineHeight, halfLineHeight + lineHeight * i, width - halfLineHeight, halfLineHeight + lineHeight * i, paint);
        }
        //绘制白棋子
        for (Point p : whitePiece) {
            canvas.drawBitmap(whiteBitmap, p.getX(), p.getY(), paint);
        }
        //绘制黑棋子
        for (Point p : blackPiece) {
            canvas.drawBitmap(blackBitmap, p.getX(), p.getY(), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                int xCount = Math.round(x / halfLineHeight);
                int yCount = Math.round(y / halfLineHeight);
                //X点击到一个格子的中间
                if (xCount % 2 == 0) {
                    xCount--;
                }
                //Y点击到一个格子的中间
                if (yCount % 2 == 0) {
                    yCount--;
                }
                //根据坐标转换格数减去棋子的一半，这样图片就在圆心
                int xP = (int) (xCount * halfLineHeight) - (int) (halfLineHeight / 2);
                int yP = (int) (yCount * halfLineHeight) - (int) (halfLineHeight / 2);
                Point point = new Point(xP, yP);
                //监测是否已经绘制
                if (checkPoint(point)) break;
                //当前是白棋
                if (isCurWhite) {
                    whitePiece.add(point);
                }
                //当前是黑棋
                else {
                    blackPiece.add(point);
                }
                isCurWhite = !isCurWhite;
                invalidate();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 检查该位置是否已经绘制
     *
     * @param point
     * @return
     */
    private boolean checkPoint(Point point) {
        if (whitePiece.contains(point)) return true;
        if (blackPiece.contains(point)) return true;
        return false;
    }

//    /**
//     * 监测是否赢了
//     *
//     * @param list
//     * @return
//     */
//    private boolean checkWin(List<Point> list) {
//        int winSize = 5;
//        int size = list.size();
//        if (size < winSize) return false;
//        int count = 0;
//        for (int i = 0; i < size; i++) {
//
//        }
//    }
}
