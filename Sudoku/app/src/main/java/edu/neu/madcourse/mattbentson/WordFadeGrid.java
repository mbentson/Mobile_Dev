package edu.neu.madcourse.mattbentson;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

public class WordFadeGrid extends View {

    Paint p;
    Paint p2;
    private LogicalBoard logicalBoard;
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    private final WordFade wordfade;

    public WordFadeGrid(Context context) {
        super(context);
        wordfade = (WordFade) context;
        p = new Paint();
        p2 = new Paint();
        p.setColor(Color.WHITE);
        p2.setColor(Color.BLACK);
        p.setStyle(Paint.Style.STROKE);
        p.setTextAlign(Paint.Align.CENTER);
        p2.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(30);
        p2.setTextSize(30);
    }

    int height;
    int width;

    Bitmap bitmap;
    Canvas canvas;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        height = 931;
        width = 931;

        setMeasuredDimension(width, height);

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        drawBoard();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, p);
    }

    float x;
    float y;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            }
            case MotionEvent.ACTION_UP: {
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if(clickDuration < MAX_CLICK_DURATION)
                {
                    x = event.getX();
                    y = event.getY();
                    if(!logicalBoard.checkFill(x,y))
                    {
                        wordfade.showKeypad("");
                    }else{
                        logicalBoard.getPositionToFill(x, y, "");
                    }
                }
            }
        }
        return true;
    }

    private void drawBoard()
    {
        int squareLength = (width-1)/31;

        for(int i = 0; i <= 31; i ++)
        {
            canvas.drawLine(0, i * squareLength, width, i * squareLength, p);
            canvas.drawLine(i * squareLength, 0, i * squareLength, height, p);
        }

        invalidate();

        logicalBoard = new LogicalBoard(squareLength, squareLength,this);
    }
    public void setSelectedTile(CharSequence letter)
    {
        RectF position;
        position = logicalBoard.getPositionToFill(x,y,letter);
        if (position != null) {
            canvas.drawText(letter, 0, 1, (position.left + position.right) / 2, position.bottom - 2, p);
            invalidate();
            wordfade.removeFromRack(letter);
        }
    }

    public void clearSelectedTile(RectF position,CharSequence letter)
    {
        if (position != null) {
            canvas.drawText(letter, 0, 1, (position.left + position.right) / 2, position.bottom - 2, p2);
            invalidate();
        }

        wordfade.addToRack(letter);
    }

    public String callSubmit()
    {
        return logicalBoard.submit();
    }

    public void setScore(String word)
    {
        wordfade.setScore(word);
    }

    public void reRack()
    {
        wordfade.reRack();
    }

    public void beep()
    {
        wordfade.beep();
    }

    public boolean checkWord(String word)
    {
        return wordfade.checkWord(word);
    }

    public void dump(CharSequence letter)
    {
        wordfade.dump(letter);
    }

    public void showMessage(String message)
    {
        wordfade.showMessage(message);
    }

    public void gameOver()
    {
        wordfade.gameOver();
    }

    public void restartTimer()
    {
        wordfade.restartTimer();
    }
}