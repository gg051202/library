package gg.base.library.widget.download;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import androidx.core.content.FileProvider;

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
class UpdateDownloadUtil {


    private static final String TAG = "DownloadUtil";

    private Activity mActivity;
    private volatile long mTotalSize = 0;
    private File mDownloadFile;
    private HttpURLConnection mConnection;
    private Subscriber<Integer> mSubscriber;
    private LoadingRandomAccessFile mRandomAccessFile;


    public UpdateDownloadUtil(Activity activity) {
        this.mActivity = activity;
    }


    public void startDownload(final String fileName, final String downloadUrl) {
        Log.i(TAG, "start download");
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
                String msg = "下载失败";
                if (e instanceof DownLoadError) {
                    msg = ((DownLoadError) e).getErrString();
                }
                if (mDownloadFile != null && mDownloadFile.exists()) {
                    boolean delete = mDownloadFile.delete();
                    Log.i(TAG, "下载失败，删除文件，result：" + delete);
                }
                if (onDownloadListener != null) {
                    onDownloadListener.err(msg);
                }
            }

            @Override
            public void onNext(Integer progress) {
                if (progress == 100) {
                    if (onDownloadListener != null) {
                        onDownloadListener.success(mDownloadFile);
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
                .create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        startDownload(subscriber, fileName, downloadUrl);
                    }
                })
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
    }


    public interface OnDownloadListener {
        void start();

        void onProgress(int progress);

        void success(File file);

        void err(String msg);

    }

    private OnDownloadListener onDownloadListener;

    public void setOnDownloadListener(OnDownloadListener l) {
        onDownloadListener = l;
    }


    private void startDownload(Subscriber<? super Integer> subscriber, String fileName, String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            throw new DownLoadError(DownLoadError.DOWNLOAD_URL_ERR);
        }

        mDownloadFile = new File(fileName);

        try {

            mConnection = create(new URL(downloadUrl));

            mConnection.connect();

            checkStatus();

            mTotalSize = mConnection.getContentLength();

            if (mDownloadFile.exists()) {
                //文件已存在，不重新下载
//                if (mDownloadFile.length() == mTotalSize) {
//                    Log.i(TAG, "文件已存在,直接调用，path：" + mDownloadFile.getAbsolutePath());
//                    subscriber.onNext(100);
//                    return;
//                } else {
//                    boolean delete = mDownloadFile.delete();
//                    Log.i(TAG, "文件已存在，但是大小不同，删除重新下载，result：" + delete);
//                }
                boolean delete = mDownloadFile.delete();
                Log.i(TAG, "文件已存在，删除重新下载，删除结果：" + delete);
            }

            clearFile(mDownloadFile);

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
            throw new CheckUpdateManager.DownLoadError(CheckUpdateManager.DownLoadError.DOWNLOAD_HTTP_STATUS, "" + statusCode);
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


    /**
     * 如果文件存在删除并重新创建，如果不存在创建
     */
    public static void clearFile(File file) {
        if (file.isFile() && file.exists()) {
            if (file.delete()) {
                Log.i("", "删除单个文件" + file.getAbsolutePath() + "成功！");
                try {
                    if (file.createNewFile()) {
                        Log.i("", "创建文件成功");
                    } else {
                        Log.i("", "创建文件失败");
                    }
                } catch (IOException e) {
                    Log.i("", "创建文件失败");
                    e.printStackTrace();
                }
            } else {
                Log.i("", "删除单个文件" + file.getAbsolutePath() + "失败！");
            }
        } else {
            try {
                if (file.createNewFile()) {
                    Log.i("", "创建文件成功");
                } else {
                    Log.i("", "创建文件失败");
                }
            } catch (IOException e) {
                Log.i("", "创建文件失败");
                e.printStackTrace();
            }
        }
    }


    public static void install(Activity activity, String authority, File file, boolean force) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri apkUri = FileProvider.getUriForFile(activity, authority, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            try {
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {//修复7.0无法更新
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }

        if (force) {
            System.exit(0);
        }
    }
}
