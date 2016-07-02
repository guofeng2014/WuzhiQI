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
 * 日期:16/7/1
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
    private boolean isCurWhite = true;
    /**
     * 只要5个连续的就是赢了
     */
    private final int WIN_COUNT = 5;
    /**
     * 控制是否可以操作棋盘
     */
    private boolean canOperate = true;
    /**
     * 赢了回调接口
     */
    private OnWinCallBack winCallBack;

    public void setWinCallBack(OnWinCallBack winCallBack) {
        this.winCallBack = winCallBack;
    }

    /**
     * 设置回调必须使用俩个参数的构造方法
     *
     * @param context
     * @param attrs
     */
    public WUZhiQIView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        //监测白棋是否赢了
        if (checkWin(whitePiece)) {
            canOperate = false;
            if (winCallBack != null) winCallBack.winListener(true);
        }
        //监测黑棋是否赢了
        else if (checkWin(blackPiece)) {
            canOperate = false;
            if (winCallBack != null) winCallBack.winListener(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当前棋盘不可操作
                if (!canOperate) break;
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


    /**
     * 监测是否赢了
     *
     * @param list
     * @return
     */
    private boolean checkWin(List<Point> list) {
        for (Point p : list) {
            int x = p.getX();
            int y = p.getY();
            //检测水平方向是否有五个相连的棋子
            boolean win = checkHorizontal(x, y, list);
            if (win) return true;
            //检测垂直方向是否有五个相连的棋子
            win = checkVertical(x, y, list);
            if (win) return true;
            //坚持向左上倾斜是否有五个相连的棋子
            win = checkSkewLeftUp(x, y, list);
            if (win) return true;
            //检测向右上倾斜是否有五个相连的棋子
            win = checkSkewRightUp(x, y, list);
            if (win) return true;
        }
        return false;
    }


    /**
     * 检测向右上角倾斜的棋子是否有五个
     *
     * @param x
     * @param y
     * @param list
     * @return
     */
    private boolean checkSkewRightUp(int x, int y, List<Point> list) {
        int count = 1;
        //检测右上是否有五个相连的棋子
        for (int i = 1; i < WIN_COUNT; i++) {
            if (list.contains(new Point(x + (int) (i * lineHeight), y + (int) (i * lineHeight)))) {
                count++;
            } else {
                break;
            }
        }
        if (count == WIN_COUNT) return true;
        //检测左下是否有五个相连的棋子
        for (int i = 1; i < WIN_COUNT; i++) {
            if (list.contains(new Point(x - (int) (i * lineHeight), y - (int) (i * lineHeight)))) {
                count++;
            } else {
                break;
            }
        }
        if (count == WIN_COUNT) return true;
        return false;
    }

    /**
     * 监测水平方向是否有连续的五个
     *
     * @param list
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> list) {
        int count = 1;
        //检测左边
        for (int i = 1; i < WIN_COUNT; i++) {
            if (list.contains(new Point(x - (int) (i * lineHeight), y))) {
                count++;
            } else {
                break;
            }
        }
        // 检测右边
        if (count == WIN_COUNT) return true;
        for (int i = 1; i < WIN_COUNT; i++) {
            if (list.contains(new Point(x + (int) (i * lineHeight), y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == WIN_COUNT) return true;
        return false;
    }


    /**
     * 检测垂直方向是否有五个连续的
     *
     * @param x
     * @param y
     * @param list
     * @return
     */
    private boolean checkVertical(int x, int y, List<Point> list) {

        int count = 1;
        //检测上边是否有五个相连的棋子
        for (int i = 1; i < WIN_COUNT; i++) {
            if (list.contains(new Point(x, y - (int) (i * lineHeight)))) {
                count++;
            } else {
                break;
            }
        }
        if (count == WIN_COUNT) return true;
        //检测下边是否有五个相连的棋子
        for (int i = 1; i < WIN_COUNT; i++) {
            if (list.contains(new Point(x, y + (int) (lineHeight * i)))) {
                count++;
            } else {
                break;
            }
        }
        if (count == WIN_COUNT) return true;
        return false;
    }

    /**
     * 检测左边向上倾斜
     *
     * @param x
     * @param y
     * @param list
     * @return
     */
    private boolean checkSkewLeftUp(int x, int y, List<Point> list) {
        int count = 1;
        for (int i = 1; i < WIN_COUNT; i++) {
            //检测左边
            if (list.contains(new Point(x - (int) (i * lineHeight), y - (int) (i * lineHeight)))) {
                count++;
            } else {
                break;
            }
            if (count == WIN_COUNT) return true;
            //检测右边
            if (list.contains(new Point(x + (int) (i * lineHeight), y + (int) (i + lineHeight)))) {
                count++;
            } else {
                break;
            }
            if (count == WIN_COUNT) return true;
        }
        return false;
    }

    /**
     * 赢了回调事件
     */
    public interface OnWinCallBack {
        /**
         * @param isWhiteWinner 是否是白方胜利
         */
        void winListener(boolean isWhiteWinner);
    }


    /**
     * 在来一把
     */
    public void onRestart() {
        canOperate = true;
        isCurWhite = true;
        //清空数据
        whitePiece.clear();
        blackPiece.clear();
        invalidate();
    }
}
