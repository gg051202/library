package gg.base.library.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by guilinlin on 2018/8/6 11:32.
 * email 973635949@qq.com
 */
public class DownloadUtil {


    private static final String TAG = "DownloadUtil";

    private Activity mActivity;
    private volatile long mTotalSize = 0;
    private File mDownloadFile;
    private HttpURLConnection mConnection;
    private Subscriber<Integer> mSubscriber;
    private LoadingRandomAccessFile mRandomAccessFile;


    public DownloadUtil(Activity activity) {
        this.mActivity = activity;
    }

    public void startDownload(final String downloadLocalFilePath, final String downloadRemoteUrl) {
        Log.i(TAG, "start download " + downloadRemoteUrl);
        mSubscriber = new Subscriber<Integer>() {
            @Override
            public void onStart() {
                if (onDownloadListener != null) {
                    onDownloadListener.start();
                }
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (mDownloadFile != null && mDownloadFile.exists()) {
                    boolean delete = mDownloadFile.delete();
                    Log.i(TAG, "下载失败，删除文件，result：" + delete);
                }
                if (onDownloadListener != null) {
                    onDownloadListener.err(e.toString());
                }
            }

            @Override
            public void onNext(Integer progress) {
                if (progress == 100) {
                    if (onDownloadListener != null) {
                        onDownloadListener.success(mDownloadFile);
                        mDownloadFile = null;//下载成功后清空文件，因为如果取消下载，会删除已有的文件。
                    }
                } else {
                    if (progress >= 1) {
                        if (onDownloadListener != null) {
                            onDownloadListener.onProgress(progress);
                        }
                    }
                }
            }
        };
        Observable
                .create((Observable.OnSubscribe<Integer>) subscriber -> startDownload(subscriber, downloadLocalFilePath, downloadRemoteUrl))
                .sample(1, TimeUnit.SECONDS)//过滤 1秒只能更新一次
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidScheduler.mainThread())
                .subscribe(mSubscriber);
    }

    public void cancel() {
        if (mSubscriber != null && !mSubscriber.isUnsubscribed()) {
            mSubscriber.unsubscribe();
        }
        if (mRandomAccessFile != null) {
            Log.i(TAG, "关闭下载");
            try {
                mRandomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mDownloadFile != null && mDownloadFile.exists()) {
            boolean delete = mDownloadFile.delete();
            LL.i(TAG, "删除文件" + delete);
        }
    }


    public interface OnDownloadListener {
        void start();

        void onProgress(int progress);

        void success(@NotNull File file);

        void err(@NotNull String msg);

    }

    private OnDownloadListener onDownloadListener;

    public DownloadUtil setOnDownloadListener(OnDownloadListener l) {
        onDownloadListener = l;
        return this;
    }


    private void startDownload(Subscriber<? super Integer> subscriber, final String downloadLocalFilePath, final String downloadRemoteUrl) {
        if (TextUtils.isEmpty(downloadRemoteUrl)) {
            throw new DownLoadError(DownLoadError.DOWNLOAD_URL_ERR);
        }

        mDownloadFile = new File(downloadLocalFilePath);
        if (mDownloadFile.exists()) {
            Log.i(TAG, "文件已存在,直接调用，path：" + mDownloadFile.getAbsolutePath());
            subscriber.onNext(100);
            return;
        }

        try {

            mConnection = create(new URL(downloadRemoteUrl));

            mConnection.connect();

            if (mConnection.getResponseCode() == 301 || mConnection.getResponseCode() == 302) {
                //得到重定向的地址
                String location = mConnection.getHeaderField("Location");
                startDownload(subscriber, downloadLocalFilePath, location);
                return;
            }


            checkStatus();

            mTotalSize = mConnection.getContentLength();

            CommonUtils.clearFile(mDownloadFile);

            Log.i(TAG, "开始下载，将要保存到的文件路径：" + mDownloadFile.getAbsolutePath());

            mRandomAccessFile = new LoadingRandomAccessFile(subscriber, mDownloadFile);
            int bytesCopied = copy(mConnection.getInputStream(), mRandomAccessFile);

            if (bytesCopied != mTotalSize && mTotalSize != -1) {
                throw new DownLoadError(DownLoadError.DOWNLOAD_INCOMPLETE);
            }
            subscriber.onNext(100);
        } catch (IOException e) {
            e.printStackTrace();
            throw new DownLoadError(DownLoadError.DOWNLOAD_NETWORK_IO);
        }
    }

    private void checkNetwork() throws CheckUpdateManager.DownLoadError {
        if (!checkNetwork(mActivity)) {
            throw new CheckUpdateManager.DownLoadError(CheckUpdateManager.DownLoadError.DOWNLOAD_NETWORK_BLOCKED);
        }
    }

    private static boolean checkNetwork(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void checkStatus() throws IOException, CheckUpdateManager.DownLoadError {
        int statusCode = mConnection.getResponseCode();
        if (statusCode != 200 && statusCode != 206) {
            LL.i(TAG, "statusCode:" + statusCode);
            throw new CheckUpdateManager.DownLoadError(CheckUpdateManager.DownLoadError.DOWNLOAD_HTTP_STATUS, String.format(" statusCode:%s %s", statusCode, mConnection.getURL().toString()));
        }
    }

    private int copy(InputStream in, RandomAccessFile out) {

        int BUFFER_SIZE = 1024;
        BufferedInputStream bis = new BufferedInputStream(in, BUFFER_SIZE);
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            out.seek(out.length());
            int bytes = 0;
            while (true) {
                int n = bis.read(buffer, 0, BUFFER_SIZE);
                if (n == -1) {
                    break;
                }
                out.write(buffer, 0, n);
                bytes += n;
                checkNetwork();
            }
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CheckUpdateManager.DownLoadError(CheckUpdateManager.DownLoadError.DOWNLOAD_DISK_IO);
        } finally {
            try {
                out.close();
                bis.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private HttpURLConnection create(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "application/*");
        connection.setConnectTimeout(10000);
        return connection;
    }


    public static class DownLoadError extends RuntimeException {

        public final int code;

        DownLoadError(int code) {
            this(code, null);
        }

        DownLoadError(int code, String message) {
            super(make(code, message));
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public String getErrString() {
            return messages.get(code);
        }

        private static String make(int code, String message) {
            String m = messages.get(code);
            if (m == null) {
                return message;
            }
            if (message == null) {
                return m;
            }
            return m + "(" + message + ")";
        }


        static final int CHECK_UNKNOWN = 2001;
        static final int CHECK_NO_WIFI = 2002;
        static final int CHECK_NO_NETWORK = 2003;
        static final int CHECK_NETWORK_IO = 2004;
        static final int CHECK_HTTP_STATUS = 2005;
        static final int CHECK_PARSE = 2006;


        static final int DOWNLOAD_UNKNOWN = 3001;
        static final int DOWNLOAD_CANCELLED = 3002;
        static final int DOWNLOAD_DISK_NO_SPACE = 3003;
        static final int DOWNLOAD_DISK_IO = 3004;
        static final int DOWNLOAD_NETWORK_IO = 3005;
        static final int DOWNLOAD_NETWORK_BLOCKED = 3006;
        static final int DOWNLOAD_NETWORK_TIMEOUT = 3007;
        static final int DOWNLOAD_HTTP_STATUS = 3008;
        static final int DOWNLOAD_INCOMPLETE = 3009;
        static final int DOWNLOAD_VERIFY = 3010;
        static final int DOWNLOAD_URL_ERR = 3011;

        static final SparseArray<String> messages = new SparseArray<>();

        static {

            messages.append(CHECK_UNKNOWN, "查询更新失败：未知错误");
            messages.append(CHECK_NO_WIFI, "查询更新失败：没有 WIFI");
            messages.append(CHECK_NO_NETWORK, "查询更新失败：没有网络");
            messages.append(CHECK_NETWORK_IO, "查询更新失败：网络异常");
            messages.append(CHECK_HTTP_STATUS, "查询更新失败：错误的HTTP状态");
            messages.append(CHECK_PARSE, "查询更新失败：解析错误");

            messages.append(DOWNLOAD_UNKNOWN, "下载失败：未知错误");
            messages.append(DOWNLOAD_CANCELLED, "下载失败：下载被取消");
            messages.append(DOWNLOAD_DISK_NO_SPACE, "下载失败：磁盘空间不足");
            messages.append(DOWNLOAD_DISK_IO, "下载失败：磁盘读写错误");
            messages.append(DOWNLOAD_NETWORK_IO, "下载失败：网络异常");
            messages.append(DOWNLOAD_NETWORK_BLOCKED, "下载失败：网络中断");
            messages.append(DOWNLOAD_NETWORK_TIMEOUT, "下载失败：网络超时");
            messages.append(DOWNLOAD_HTTP_STATUS, "下载失败：错误的HTTP状态");
            messages.append(DOWNLOAD_INCOMPLETE, "下载失败：下载不完整");
            messages.append(DOWNLOAD_VERIFY, "下载失败：校验错误");
            messages.append(DOWNLOAD_URL_ERR, "下载失败：下载地址错误");
        }
    }


    private final class LoadingRandomAccessFile extends RandomAccessFile {

        private Subscriber<? super Integer> subscriber;
        private int mBytesLoaded = 0;
        private int oldProgress;

        LoadingRandomAccessFile(Subscriber<? super Integer> subscriber, File file) throws FileNotFoundException {
            super(file, "rw");
            this.subscriber = subscriber;
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);
            mBytesLoaded += count;
            if (mBytesLoaded < mTotalSize) {
                int progress = (int) ((float) (mBytesLoaded) / mTotalSize * 100);
                if (oldProgress != progress) {
                    oldProgress = progress;
                    subscriber.onNext(progress);
                    Log.i(TAG, "progress:" + progress);
                }
            }
        }
    }

}
