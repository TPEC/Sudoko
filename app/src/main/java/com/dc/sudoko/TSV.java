package com.dc.sudoko;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dc.sudoko.TWidget.TLabel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by XIeQian on 2016/12/23.
 */

class TSV extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final float SIZE_WIDTH = 720;
    private static final float SIZE_HEIGHT = 1280;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.CHINA);

    public enum SceneStateEnum {sseMain}

    private SceneStateEnum sse;
    private SurfaceHolder sh;
    private Thread th;
    private boolean flag;
    private Canvas canvas;
    private Paint paint, paintBlack;
    private int FPS;
    private boolean scaleToWidth;
    private float scaleRate, scaleTrans;
    private TLabel tlTest;
    private TLabel tlTime;

    private SceneMain sceneMain;

    private GameDB gameDB;

    public TSV(Context context) {
        super(context);
        sh = this.getHolder();
        sh.addCallback(this);
        scaleRate = 1;
        scaleTrans = 0;
        scaleToWidth = true;
        paintBlack = new Paint();
        paint = new Paint();
        paint.setTextSize(32);
        paint.setColor(Color.WHITE);

        tlTest = new TLabel(0, 0, 720, 72);
        tlTest.setTextSize(30);
        tlTest.setTextColor(Color.WHITE);

        tlTime = new TLabel(600, 0, 120, 72);
        tlTime.setTextSize(36);
        tlTime.setTextColor(Color.WHITE);

        gameDB = GameDB.getInstance();
        gameDB.load(getContext());

        sceneMain = new SceneMain(this);
        sse = SceneStateEnum.sseMain;
    }

    public void drawSV() {
        try {
            canvas = sh.lockCanvas();
            if (canvas != null) {
                canvas.save();
                canvas.scale(scaleRate, scaleRate);
                if (scaleToWidth)
                    canvas.translate(0, scaleTrans);
                else
                    canvas.translate(scaleTrans, 0);

                canvas.drawColor(Color.BLACK);

                sceneMain.draw(canvas);

                String sb = "No." + gameDB.game_cnt + "    \t" +
                        "Time:" + ((gameDB.finished ? gameDB.game_finish_ts - gameDB.game_start_ts : System.currentTimeMillis() - gameDB.game_start_ts) / 1000) + "s    \t" +
                        "D:" + gameDB.difficulty;
                tlTest.setText(sb);
                tlTest.drawLabel(canvas);

                tlTime.setText(DATE_FORMAT.format(new Date()));
                tlTime.drawLabel(canvas);

//                Paint p2=new Paint();
//                p2.setColor(Color.RED);
//                canvas.drawRect(0,0,720,1280,p2);
//                p2.setColor(Color.WHITE);
//                canvas.drawRect(10,10,710,1270,p2);

                if (scaleToWidth) {
                    canvas.drawRect(0, -scaleTrans, SIZE_WIDTH, 0, paintBlack);
                    canvas.drawRect(0, SIZE_HEIGHT, SIZE_WIDTH, SIZE_HEIGHT + scaleTrans, paintBlack);
                } else {
                    canvas.drawRect(-scaleTrans, 0, 0, SIZE_HEIGHT, paintBlack);
                    canvas.drawRect(SIZE_WIDTH, 0, SIZE_WIDTH + scaleTrans, SIZE_HEIGHT, paintBlack);
                }
                canvas.restore();
            }
        } catch (Exception e) {

        } finally {
            if (canvas != null)
                sh.unlockCanvasAndPost(canvas);
        }
    }

    private void logicSV() {
        switch (sse) {
            case sseMain:
                sceneMain.logic();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (scaleToWidth) {
            event.setLocation(event.getX() / scaleRate, event.getY() / scaleRate - scaleTrans);
            if (event.getY() < 0 || event.getY() >= SIZE_HEIGHT) {
                return false;
            }
        } else {
            event.setLocation(event.getX() / scaleRate - scaleTrans, event.getY() / scaleRate);
            if (event.getX() < 0 || event.getX() >= SIZE_WIDTH) {
                return false;
            }
        }
        if (sse == SceneStateEnum.sseMain) {
            if (sceneMain.onTouchEvent(event)) {
                drawSV();
                return true;
            }
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        flag = true;
//        th = new Thread(this);
//        th.start();
        drawSV();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        float srw, srh;
        srw = (float) width / SIZE_WIDTH;
        srh = (float) height / SIZE_HEIGHT;
        if (srw >= srh) {
            scaleRate = srh;
            scaleToWidth = false;
            scaleTrans = (width / srh - SIZE_WIDTH) / 2;
        } else {
            scaleRate = srw;
            scaleToWidth = true;
            scaleTrans = (height / srw - SIZE_HEIGHT) / 2;
        }
        drawSV();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (sse == SceneStateEnum.sseMain) {
            return sceneMain.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public void run() {
        while (flag) {
            try {
                long tinterval = System.currentTimeMillis();
                logicSV();
                drawSV();
                tinterval = System.currentTimeMillis() - tinterval;
                if (tinterval < 33) {
                    FPS = 30;
                    Thread.sleep(33 - tinterval);
                } else {
                    FPS = (int) (1000 / tinterval);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}