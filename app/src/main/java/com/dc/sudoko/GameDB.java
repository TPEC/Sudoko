package com.dc.sudoko;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.ProgressBar;

import java.util.Arrays;

/**
 * Created by XIeQian on 2017/2/10.
 */

public class GameDB {
    public Context context;
    public Resources res;

    public long game_cnt;
    public long game_start_ts;

    public int[] answer;
    public int[] question_mask;
    public int[] user_answer;
    public boolean[] uap;

    public int[] user_answer_saved = null;
    public boolean[] uap_saved = null;

    public boolean[] wrong;
    public int[] showAnswerColor;
    public boolean finished;
    public long game_finish_ts;

    public int selX, selY, selI;

    public String difficulty;

    private GameDB() {
    }

    public void save_answer() {
        user_answer_saved = Arrays.copyOf(user_answer, user_answer.length);
        uap_saved = Arrays.copyOf(uap, uap.length);
        SharedPreferences.Editor editor = context.getSharedPreferences("answer_saved", Activity.MODE_PRIVATE).edit();
        editor.clear();
        StringBuilder s = new StringBuilder();
        for (int b : user_answer_saved) {
            s.append(b);
        }
        editor.putString("ans", s.toString());
        s = new StringBuilder();
        for (boolean b : uap_saved) {
            s.append(b ? '1' : '0');
        }
        editor.putString("uap", s.toString());
        editor.apply();
    }

    public boolean can_restore_answer() {
        if (user_answer_saved == null) {
            SharedPreferences sp = context.getSharedPreferences("answer_saved", Activity.MODE_PRIVATE);
            String s1 = sp.getString("ans", "");
            String s2 = sp.getString("uap", "");
            if (s1 == null || s1.isEmpty() || s2 == null || s2.isEmpty()) {
                return false;
            }
            user_answer_saved = new int[81];
            for (int i = 0; i < s1.length(); i++) {
                user_answer_saved[i] = s1.charAt(i) - '0';
            }
            uap_saved = new boolean[81 * 9];
            for (int i = 0; i < s1.length(); i++) {
                uap_saved[i] = s1.charAt(i) == '1';
            }
        }
        return true;
    }

    public void restore_answer() {
        if (user_answer_saved != null) {
            user_answer = Arrays.copyOf(user_answer_saved, user_answer_saved.length);
            uap = Arrays.copyOf(uap_saved, uap_saved.length);
            judge();
        }
    }

    public void load(Context context) {
        this.context = context;
        this.res = context.getResources();
        SharedPreferences sp = context.getSharedPreferences("sketch", Activity.MODE_PRIVATE);
        game_cnt = sp.getLong("no", 1);
        finished = sp.getBoolean("fin", true);
        if (finished) {
            newGame(0, null);
        } else {
            sp = context.getSharedPreferences("save", Activity.MODE_PRIVATE);
            String s;
            answer = new int[81];
            question_mask = new int[81];
            user_answer = new int[81];
            uap = new boolean[81 * 9];
            wrong = new boolean[81];
            showAnswerColor = new int[81];
            s = sp.getString("ans", "");
            for (int i = 0; i < s.length(); i++) {
                answer[i] = s.charAt(i) - '0';
            }
            s = sp.getString("que", "");
            for (int i = 0; i < s.length(); i++) {
                question_mask[i] = s.charAt(i) - '0';
            }
            s = sp.getString("uan", "");
            for (int i = 0; i < s.length(); i++) {
                user_answer[i] = s.charAt(i) - '0';
            }
            s = sp.getString("uap", "");
            for (int i = 0; i < s.length(); i++) {
                uap[i] = s.charAt(i) == '1';
            }
            for (int i = 0; i < 81; i++) {
                showAnswerColor[i] = Color.BLACK;
            }
            game_start_ts = System.currentTimeMillis() - sp.getLong("tim", 0);
            difficulty = sp.getString("difficulty", "");
            selI = -1;
            selX = -1;
            selY = -1;
            judge();
        }
    }

    public void record() {
        SharedPreferences.Editor editor = context.getSharedPreferences("sketch", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.putLong("no", game_cnt);
        editor.putBoolean("fin", finished);
        editor.apply();
    }

    public void save() {
        record();
        SharedPreferences.Editor editor = context.getSharedPreferences("save", Activity.MODE_PRIVATE).edit();
        editor.clear();
        StringBuilder s = new StringBuilder();
        for (int an : answer) {
            s.append(an);
        }
        editor.putString("ans", s.toString());
        s = new StringBuilder();
        for (int b : question_mask) {
            s.append(b);
        }
        editor.putString("que", s.toString());
        s = new StringBuilder();
        for (int value : user_answer) {
            s.append(value);
        }
        editor.putString("uan", s.toString());
        s = new StringBuilder();
        for (boolean b : uap) {
            s.append(b ? "1" : "0");
        }
        editor.putString("uap", s.toString());
        editor.putLong("tim", System.currentTimeMillis() - game_start_ts);
        editor.putString("difficulty", difficulty);
        editor.apply();
    }

    public void newGame(int level, ProgressBar pb) {
        user_answer_saved = null;
        uap_saved = null;
        SharedPreferences.Editor editor = context.getSharedPreferences("answer_saved", Activity.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        int p1, p2;
        switch (level) {
            case 1: // medium
                p1 = 81;
                p2 = 6;
                break;
            case 2: // hard
                p1 = 81;
                p2 = 12;
                break;
            case 3: // extreme
                p1 = 81;
                p2 = 48;
                break;
            default: // easy
                p1 = 81;
                p2 = 3;
        }

        answer = SudokuGenerator.create(128);
        question_mask = SudokuGenerator.mask(answer, p1, p2);
        difficulty = SudokuGenerator.getLastDifficulty();

        selI = -1;
        selX = -1;
        selY = -1;
        finished = false;
        user_answer = new int[81];
        uap = new boolean[729];
        wrong = new boolean[81];
        showAnswerColor = new int[81];
        for (int i = 0; i < 81; i++) {
            user_answer[i] = 0;
            wrong[i] = false;
            showAnswerColor[i] = Color.BLACK;
            for (int j = 0; j < 9; j++) {
                uap[i * 9 + j] = false;
            }
        }
        game_start_ts = System.currentTimeMillis();
    }

    public void clearAll() {
        for (int i = 0; i < 81; i++) {
            user_answer[i] = 0;
            wrong[i] = false;
            showAnswerColor[i] = Color.BLACK;
            for (int j = 0; j < 9; j++) {
                uap[i * 9 + j] = false;
            }
        }
    }

    public void showTips() {
        for (int i = 0; i < 81; i++)
            if (wrong[i])
                return;
        for (int i = 0; i < 81; i++) {
            int x = i % 9, y = i / 9;
            if (question_mask[i] == 0 && user_answer[i] == 0) {
                for (int j = 0; j < 9; j++)
                    uap[i * 9 + j] = true;
                for (int j = 0; j < 9; j++) {
                    if (getValue(y * 9 + j) > 0)
                        uap[i * 9 + getValue(y * 9 + j) - 1] = false;
                    if (getValue(j * 9 + x) > 0)
                        uap[i * 9 + getValue(j * 9 + x) - 1] = false;
                    int tx = j % 3 - x % 3, ty = j / 3 - y % 3;
                    if (getValue((y + ty) * 9 + x + tx) > 0)
                        uap[i * 9 + getValue((y + ty) * 9 + x + tx) - 1] = false;
                }
            }
        }
    }

    public void showAnswer() {
        for (int i = 0; i < 81; i++) {
            if (question_mask[i] == 0) {
                showAnswerColor[i] = Color.BLACK;
                if (uap[i] && user_answer[i] != answer[i]) {
                    showAnswerColor[i] = Color.RED;
                }
                user_answer[i] = answer[i];
            }
        }
    }

    public boolean judge() {
        for (int i = 0; i < 81; i++)
            wrong[i] = false;
        for (int i = 0; i < 81; i++) {
            if (!wrong[i] && getValue(i) > 0) {
                int x = i % 9, y = i / 9;
                for (int j = 0; j < 9; j++) {
                    if (j != x && getValue(y * 9 + j) == getValue(i)) {
                        wrong[i] = true;
                        wrong[y * 9 + j] = true;
                    }
                    if (j != y && getValue(j * 9 + x) == getValue(i)) {
                        wrong[i] = true;
                        wrong[j * 9 + x] = true;
                    }
                    int tx = j % 3 - x % 3, ty = j / 3 - y % 3;
                    int i1 = (y + ty) * 9 + x + tx;
                    if (i1 != i && getValue(i1) == getValue(i)) {
                        wrong[i] = true;
                        wrong[i1] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 81; i++) {
            if (wrong[i] || getValue(i) == 0) {
                return false;
            }
        }
        finished = true;
        game_finish_ts = System.currentTimeMillis();
        game_cnt++;
        for (int i = 0; i < 81; i++) {
            if (question_mask[i] == 0) {
                showAnswerColor[i] = Color.GREEN;
            }
        }
        record();
        return true;
    }

    private int getValue(int index) {
        return question_mask[index] > 0 ? answer[index] : user_answer[index];
    }


    private static final GameDB GAME_DB = new GameDB();

    public static GameDB getInstance() {
        return GAME_DB;
    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }
}
