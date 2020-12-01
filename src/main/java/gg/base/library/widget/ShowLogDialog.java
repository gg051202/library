package gg.base.library.widget;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import com.blankj.utilcode.util.ToastUtils;

import java.io.File;

import gg.base.library.R;
import gg.base.library.util.LocalLogUtil;

public class ShowLogDialog implements View.OnClickListener {

    private AlertDialog mAlertDialog;
    private Activity mActivity;
    private String mLogString;

    public ShowLogDialog(Activity activity) {
        this.mActivity = activity;

    }

    public void show() {
        if (mAlertDialog == null) {
            @SuppressLint("InflateParams")
            View view = LayoutInflater.from(mActivity).inflate(R.layout.frame_custom_layout_log, null);
            TextView descTextView = view.findViewById(R.id.descTextView);
            NestedScrollView scrollView = view.findViewById(R.id.scrollView);

            mLogString = LocalLogUtil.getLogString(mActivity.getApplicationContext());
            descTextView.setText(mLogString);
            descTextView.postDelayed(() -> scrollView.smoothScrollTo(0, 10000000), 500);

            view.findViewById(R.id.cancelTextView).setOnClickListener(this);
            view.findViewById(R.id.submitTextView).setOnClickListener(this);
            view.findViewById(R.id.clearImageView).setOnClickListener(this);

            mAlertDialog = new AlertDialog.Builder(mActivity, R.style.FrameDialogStyle)
                    .setView(view)
                    .show();
            Window window = mAlertDialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
            }

        }
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitTextView) {
            dismissDialog();

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            intentShareFile.setType("application/pdf");
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".myfileprovider", new File(LocalLogUtil.getLogPath(mActivity.getApplicationContext())));
            } else {
                uri = Uri.fromFile(new File(LocalLogUtil.getLogPath(mActivity.getApplicationContext())));
            }

            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "发送日志给开发者（建议企业微信）");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "发送日志给开发者");

            mActivity.startActivity(Intent.createChooser(intentShareFile, "发送日志给开发者（建议企业微信）"));

        } else if (v.getId() == R.id.cancelTextView) {
            dismissDialog();
        } else if (v.getId() == R.id.clearImageView) {
            dismissDialog();
            LocalLogUtil.clearLogFile(mActivity.getApplicationContext());
            ToastUtils.showShort("已清空日志");
        }
    }

    private void dismissDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

}

