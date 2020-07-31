package com.dc.sudoko.TWidget;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.MotionEvent;

/**
 * Created by XieQian on 2016/11/26.
 */

public class TButton {
    private Rect rectDst,rectSrc;
    private Bitmap bmpBtnBG;
    private Bitmap bmpBtnSF;
    private Paint paint,paintGray;
    private boolean enabled;
    private boolean clickdown;
    private boolean hasClicked;
    private int pointID;

    public TButton(int px, int py, int pw, int ph){
        rectDst=new Rect(px,py,px+pw,py+ph);
        rectSrc=new Rect(0,0,pw,ph);
        paint=new Paint();
        paintGray=new Paint();
        ColorMatrix cm=new ColorMatrix();
        cm.setSaturation(0);
        paintGray.setColorFilter(new ColorMatrixColorFilter(cm));
        bmpBtnBG=null;
        bmpBtnSF=null;
        clickdown=false;
        enabled=true;
        hasClicked=false;
        pointID=-1;
    }

    public void setBmp(Resources res, int bmpBGid, int bmpSFid){
        if(bmpBGid!=0)
            bmpBtnBG = decodeResource(res, bmpBGid);
        else
            bmpBtnBG=null;
        if(bmpSFid!=0)
            bmpBtnSF = decodeResource(res, bmpSFid);
        else
            bmpBtnSF=null;
    }

    public void setEnabled(boolean blnEnabled){
        if(enabled!=blnEnabled) {
            enabled = blnEnabled;
            clickdown = false;
        }
    }

    public boolean isEnabled(){
        return enabled;
    }

    public boolean getHasClicked(){
        if(hasClicked){
            hasClicked=false;
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event){//return true when it's clicked
        if(enabled) {
            if (rectDst.contains((int)event.getX(), (int)event.getY())) {
                if(clickdown) {
                    if(event.getActionMasked()==MotionEvent.ACTION_UP){
                        clickdown=false;
                        hasClicked=true;
                        return true;
                    }
                }else{
                    if(event.getActionMasked()==MotionEvent.ACTION_DOWN){
                        clickdown=true;
                        return false;
                    }
                }
                return false;
            }else{
                if(clickdown && event.getAction()==MotionEvent.ACTION_UP)
                    clickdown=false;
                return false;
            }
        }
        return false;
    }

    public Rect getPosRect(){
        return rectDst;
    }

    public void draw(Canvas canvas) {
        Rect rectSrc_ = new Rect(rectSrc);
        Rect rectDst_ = new Rect(rectDst);
        if (enabled) {
            if (clickdown) {
                rectSrc_.offsetTo(0, rectSrc.height());
                rectDst_.offset(rectDst.height() / 32, rectDst.height() / 32);
            }
            if (bmpBtnBG != null)
                canvas.drawBitmap(bmpBtnBG, rectSrc_, rectDst, paint);
            if (bmpBtnSF != null)
                canvas.drawBitmap(bmpBtnSF, rectSrc, rectDst_, paint);
        } else {
            if (bmpBtnBG != null)
                canvas.drawBitmap(bmpBtnBG, rectSrc_, rectDst, paintGray);
            if (bmpBtnSF != null)
                canvas.drawBitmap(bmpBtnSF, rectSrc, rectDst_, paintGray);
        }
    }

    private Bitmap decodeResource(Resources resources,int id){
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }
}
