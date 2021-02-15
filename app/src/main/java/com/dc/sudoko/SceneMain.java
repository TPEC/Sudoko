package com.dc.sudoko;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dc.sudoko.TWidget.TButton;

/**
 * Created by XIeQian on 2017/2/10.
 */

public class SceneMain {
    private final TSV tsv;
    private GameDB gameDB;
    private TButton[] btnNum;
    private TButton btnInputMod;
    private TButton btnMore;
    private TButton btnTips;
    private TButton btnCls;
    private TButton[] btnNewGames;
    private TButton btnAnswer;

    private TButton btnSave;
    private TButton btnRestore;

    private boolean touched;
    private boolean inputMod;//false-input,true-guess
    private boolean more;
    private Paint paintTxt1, paintTxt2;

    public SceneMain(TSV tsv) {
        this.tsv = tsv;
        gameDB = GameDB.getInstance();
        btnNum = new TButton[10];
        btnNum[0] = new TButton(3 * 166 + 31, 800, 160, 160);
        for (int i = 0; i < 9; i++) {
            int tx = i % 3, ty = i / 3;
            btnNum[i + 1] = new TButton(tx * 166 + 31, ty * 160 + 800, 160, 160);
        }
        for (int i = 0; i < 10; i++) {
            btnNum[i].setBmp(gameDB.res, R.drawable.btnbg, 0);
        }
        btnInputMod = new TButton(3 * 166 + 31, 1 * 160 + 800, 160, 160);
        btnInputMod.setBmp(gameDB.res, R.drawable.btnbg, 0);
        btnMore = new TButton(3 * 166 + 31, 2 * 160 + 800, 160, 160);
        btnMore.setBmp(gameDB.res, R.drawable.btnbg, 0);
        btnTips = new TButton(0 * 166 + 31, 0 * 160 + 800, 160, 160);
        btnTips.setBmp(gameDB.res, R.drawable.btnbg, 0);
        btnCls = new TButton(1 * 166 + 31, 0 * 160 + 800, 160, 160);
        btnCls.setBmp(gameDB.res, R.drawable.btnbg, 0);
        btnNewGames = new TButton[4];
        for (int i = 0; i < 4; i++) {
            btnNewGames[i] = new TButton(i * 166 + 31, 1 * 160 + 800, 160, 160);
            btnNewGames[i].setBmp(gameDB.res, R.drawable.btnbg, 0);
        }
        btnAnswer = new TButton(3 * 166 + 31, 0 * 160 + 800, 160, 160);
        btnAnswer.setBmp(gameDB.res, R.drawable.btnbg, 0);

        btnSave = new TButton(0 * 166 + 31, 2 * 160 + 800, 160, 160);
        btnSave.setBmp(gameDB.res, R.drawable.btnbg, 0);
        btnRestore = new TButton(1 * 166 + 31, 2 * 160 + 800, 160, 160);
        btnRestore.setBmp(gameDB.res, R.drawable.btnbg, 0);

        touched = false;
        inputMod = false;
        more = false;
        paintTxt1 = new Paint();
        paintTxt1.setTextSize(90);
        paintTxt1.setFakeBoldText(true);
        paintTxt2 = new Paint();
        paintTxt2.setTextSize(50);
        paintTxt2.setFakeBoldText(true);
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        Rect recDst = new Rect(0, 0, 80, 80);
        int x, y;
        for (int i = 0; i < 81; i++) {
            x = i % 9;
            y = i / 9;
            recDst.offsetTo(x * 80, y * 80 + 72);
            if (gameDB.question_mask[i] > 0) {
                if (i == gameDB.selI)
                    paint.setColor(Color.rgb(31, 91, 223));
                else if (touched && (x == gameDB.selX || y == gameDB.selY))
                    paint.setColor(Color.rgb(95, 125, 192));
                else
                    paint.setColor(Color.rgb(159, 159, 159));
            } else {
                if (i == gameDB.selI)
                    paint.setColor(Color.rgb(159, 191, 255));
                else if (touched && (x == gameDB.selX || y == gameDB.selY))
                    paint.setColor(Color.rgb(207, 223, 255));
                else
                    paint.setColor(Color.WHITE);
            }
            canvas.drawRect(recDst, paint);
            paint.setColor(Color.BLACK);
            String sn = "";
            if (gameDB.question_mask[i] > 0) {
                sn = String.valueOf(gameDB.answer[i]);
            } else if (gameDB.user_answer[i] > 0) {
                sn = String.valueOf(gameDB.user_answer[i]);
                paint.setColor(gameDB.showAnswerColor[i]);
            }
            if (!sn.isEmpty()) {
                paint.setTextSize(50);
                paint.setFakeBoldText(true);
                if (gameDB.wrong[i]) {
                    paint.setColor(Color.RED);
                }
                canvas.drawText(sn, recDst.left + 25, recDst.bottom - 20, paint);
            } else {
                paint.setTextSize(25);
                paint.setFakeBoldText(false);
                for (int j = 0; j < 9; j++) {
                    if (gameDB.uap[i * 9 + j]) {
                        int tx = j % 3, ty = j / 3;
                        canvas.drawText(String.valueOf(j + 1), recDst.left + tx * 25 + 7, recDst.top + ty * 25 + 25, paint);
                    }
                }
            }
        }
        paint.setColor(Color.BLACK);
        for (int i = 0; i <= 9; i++) {
            if (i % 3 == 0)
                paint.setStrokeWidth(6);
            else
                paint.setStrokeWidth(2);
            canvas.drawLine(i * 80, 72, i * 80, 720 + 72, paint);
            canvas.drawLine(0, i * 80 + 72, 720, i * 80 + 72, paint);
        }

        if (!more) {
            for (int i = 0; i < 10; i++)
                btnNum[i].draw(canvas);
            btnInputMod.draw(canvas);
            btnMore.draw(canvas);

            for (int i = 1; i < 10; i++) {
                canvas.drawText(String.valueOf(i), btnNum[i].getPosRect().left + 55, btnNum[i].getPosRect().bottom - 50, paintTxt1);
            }
            canvas.drawText("C", btnNum[0].getPosRect().left + 50, btnNum[0].getPosRect().bottom - 50, paintTxt1);
            canvas.drawText(">", btnMore.getPosRect().left + 50, btnMore.getPosRect().bottom - 50, paintTxt1);
            canvas.drawText(inputMod ? "草稿" : "答题", btnInputMod.getPosRect().left + 30, btnInputMod.getPosRect().bottom - 65, paintTxt2);
        } else {
            btnTips.draw(canvas);
            btnCls.draw(canvas);
            for (int i = 0; i < 4; i++) {
                btnNewGames[i].draw(canvas);
            }
            btnAnswer.draw(canvas);
            btnMore.draw(canvas);
            btnSave.draw(canvas);
            btnRestore.draw(canvas);
            canvas.drawText("<", btnMore.getPosRect().left + 50, btnMore.getPosRect().bottom - 50, paintTxt1);
            canvas.drawText("提示", btnTips.getPosRect().left + 30, btnTips.getPosRect().bottom - 65, paintTxt2);
            canvas.drawText("清除", btnCls.getPosRect().left + 30, btnCls.getPosRect().bottom - 65, paintTxt2);
            canvas.drawText("答案", btnAnswer.getPosRect().left + 30, btnAnswer.getPosRect().bottom - 65, paintTxt2);
            canvas.drawText("简单", btnNewGames[0].getPosRect().left + 30, btnNewGames[0].getPosRect().bottom - 65, paintTxt2);
            canvas.drawText("中等", btnNewGames[1].getPosRect().left + 30, btnNewGames[1].getPosRect().bottom - 65, paintTxt2);
            canvas.drawText("困难", btnNewGames[2].getPosRect().left + 30, btnNewGames[2].getPosRect().bottom - 65, paintTxt2);
            canvas.drawText("地狱", btnNewGames[3].getPosRect().left + 30, btnNewGames[3].getPosRect().bottom - 65, paintTxt2);
            canvas.drawText("快照", btnSave.getPosRect().left + 30, btnSave.getPosRect().bottom - 65, paintTxt2);
            canvas.drawText("恢复", btnRestore.getPosRect().left + 30, btnRestore.getPosRect().bottom - 65, paintTxt2);
        }
    }

    public void logic() {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            gameDB.save();
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getY() >= 72 && event.getY() < 720 + 72) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                touched = true;
                gameDB.selX = (int) (event.getX() / 80);
                gameDB.selY = (int) ((event.getY() - 72) / 80);
                gameDB.selI = gameDB.selY * 9 + gameDB.selX;
            } else {
                touched = false;
                if (gameDB.question_mask[gameDB.selI] > 0) {
                    gameDB.selI = -1;
                    gameDB.selX = -1;
                    gameDB.selY = -1;
                }
            }
            return true;
        } else {
            if (!more) {
                for (int i = 0; i < 10; i++) {
                    if (btnNum[i].onTouchEvent(event)) {
                        if (gameDB.selI >= 0) {
                            if (i == 0) {
                                gameDB.user_answer[gameDB.selI] = 0;
                                for (int j = 0; j < 9; j++)
                                    gameDB.uap[gameDB.selI * 9 + j] = false;
                            } else {
                                if (!inputMod) {
                                    gameDB.user_answer[gameDB.selI] = i;
                                } else {
                                    gameDB.user_answer[gameDB.selI] = 0;
                                    gameDB.uap[gameDB.selI * 9 + i - 1] = !gameDB.uap[gameDB.selI * 9 + i - 1];
                                }
                            }
                        }
                        if (gameDB.judge()) {

                        }
                        return true;
                    }
                }
                if (btnInputMod.onTouchEvent(event)) {
                    inputMod = !inputMod;
                    return true;
                }
            } else {
                if (btnTips.onTouchEvent(event)) {
                    gameDB.showTips();
                    return true;
                }
                if (btnCls.onTouchEvent(event)) {
                    new AlertDialog.Builder(tsv.getContext())
                            .setTitle("Clear All")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gameDB.clearAll();
                                    tsv.drawSV();
                                }
                            })
                            .setNegativeButton("No", null)
                            .create()
                            .show();
                    return true;
                }
                for (int i = 0; i < 4; i++) {
                    if (btnNewGames[i].onTouchEvent(event)) {
                        final int level = i;
                        new AlertDialog.Builder(tsv.getContext())
                                .setTitle("New Game")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        NewGameProgressBar pb = new NewGameProgressBar(tsv.getContext());
                                        pb.setLevel(level);
                                        final AlertDialog ad = new AlertDialog.Builder(tsv.getContext()).setTitle("Loading...").setView(pb).setCancelable(false).create();
                                        pb.setOnFinishListener(new NewGameProgressBar.OnFinishListener() {
                                            @Override
                                            public void onFinish() {
                                                tsv.drawSV();
                                                ad.dismiss();
                                            }
                                        });
                                        pb.start();
                                        ad.show();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .create()
                                .show();
                        return true;
                    }
                }
                if (btnAnswer.onTouchEvent(event)) {
                    new AlertDialog.Builder(tsv.getContext())
                            .setTitle("Show Answer")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gameDB.showAnswer();
                                    tsv.drawSV();
                                }
                            })
                            .setNegativeButton("No", null)
                            .create()
                            .show();
                    return true;
                }
                if (btnSave.onTouchEvent(event)) {
                    gameDB.save_answer();
                    Toast.makeText(tsv.getContext(), "快照已保存", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (btnRestore.onTouchEvent(event)) {
                    if (!gameDB.can_restore_answer()) {
                        Toast.makeText(tsv.getContext(), "这一局未保存快照", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    new AlertDialog.Builder(tsv.getContext())
                            .setTitle("恢复快照")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gameDB.restore_answer();
                                    tsv.drawSV();
                                }
                            })
                            .setNegativeButton("No", null)
                            .create()
                            .show();
                    return true;
                }
            }
            if (btnMore.onTouchEvent(event)) {
                more = !more;
                return true;
            }
        }
        return true;
    }

}
