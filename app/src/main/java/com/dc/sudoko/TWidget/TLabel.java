package com.dc.sudoko.TWidget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by XieQian on 2016/11/26.
 */

public class TLabel {
    private Paint paint;
    private String text;
    private String[] textline;
    private Rect rectDst;
    private Paint.FontMetrics fontMetrics;
    private int lineleap;//行间距
    private boolean isReady;

    public TLabel(int x, int y, int w, int h) {
        isReady = false;
        rectDst = new Rect(x, y, x + w, y + h);
        text = "";
        paint = new Paint();
    }

    public void offsetTo(int x, int y) {
        rectDst.offsetTo(x, y);
    }

    public void setText(String newtext) {
        text = newtext;
        resetTextLine();
    }

    public void setTextSize(int size) {
        paint.setTextSize(size);
        resetTextLine();
    }

    public void setTextColor(int color) {
        paint.setColor(color);
    }

    private void resetTextLine() {
        fontMetrics = paint.getFontMetrics();
        lineleap = (int) ((fontMetrics.bottom - fontMetrics.top) * 1);
        if (rectDst.height() < lineleap) {
            isReady = false;
            return;
        }
        if (text.equals("")) {
            isReady = false;
            return;
        }
        int[] spliti = new int[rectDst.height() / lineleap * 2 + 1];
        int i, j = 1;
        spliti[0] = 0;
        for (i = 1; i < text.length(); i++) {
            if (text.charAt(i) == '|') {
                i++;
                if (i < text.length() && text.charAt(i) == 'n') {
                    spliti[j * 2 - 1] = i - 1;
                    spliti[j * 2] = i + 1;
                    j++;
                    i++;
                    if (j * 2 > spliti.length)
                        break;
                }
            }
            if (paint.measureText(text.substring(spliti[j * 2 - 2], i)) > rectDst.width()) {
                spliti[j * 2 - 1] = i - 1;
                spliti[j * 2] = i - 1;
                j++;
                i++;
                if (j * 2 > spliti.length)
                    break;
            }
        }
        if (j * 2 <= spliti.length) {
            spliti[j * 2 - 1] = text.length();
            j++;
        }
        textline = new String[j - 1];
        for (i = 0; i < textline.length; i++)
            textline[i] = text.substring(spliti[i * 2], spliti[i * 2 + 1]);
        isReady = true;
    }

    public void draw(Canvas canvas, int length) {
        if (isReady) {
            int i, y, l = 0;
            for (i = 0; i < textline.length; i++) {
                y = i * lineleap + rectDst.top - (int) fontMetrics.top;
                if (l + textline[i].length() <= length) {
                    canvas.drawText(textline[i], rectDst.left, y, paint);
                    l += textline[i].length();
                } else {
                    canvas.drawText(textline[i].substring(0, length - l), rectDst.left, y, paint);
                }
            }
        }
    }

    public void drawLabel(Canvas canvas) {
        if (isReady) {
            int i, y;
            for (i = 0; i < textline.length; i++) {
                y = i * lineleap + rectDst.top - (int) fontMetrics.top;
                canvas.drawText(textline[i], rectDst.left, y, paint);
            }
        }
    }
}
