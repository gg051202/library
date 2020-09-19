package gg.base.library.widget.download;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class AndroidScheduler implements Executor {

    private static AndroidScheduler instance;

    private final Scheduler mMainScheduler;
    private final Handler mHandler;

    private AndroidScheduler() {
        mHandler = new Handler(Looper.myLooper());
        mMainScheduler = Schedulers.from(this);
    }

    public static synchronized Scheduler mainThread() {
        if (instance == null) {
            instance = new AndroidScheduler();
        }
        return instance.mMainScheduler;
    }

    @Override
    public void execute(Runnable command) {
        mHandler.post(command);
    }

}
