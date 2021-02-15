package com.dc.sudoko;

import android.content.Context;
import android.widget.ProgressBar;

public class NewGameProgressBar extends ProgressBar implements Runnable {
    private int mLevel;
    private OnFinishListener onFinishListener = null;

    public NewGameProgressBar(Context context) {
        super(context);
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public void setOnFinishListener(OnFinishListener listener) {
        onFinishListener = listener;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        GameDB.getInstance().newGame(mLevel, this);
        if (onFinishListener != null) {
            onFinishListener.onFinish();
        }
    }

    public interface OnFinishListener {
        void onFinish();
    }
}
