package gg.base.library.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import gg.base.library.R;
import gg.base.library.widget.MyDialog;
import kotlin.Unit;

/**
 * Created by guilinlin on 16/8/30 14:09.
 * email 973635949@qq.com
 */
public class DialogFactory {


    public static AlertDialog show(Activity activity, CharSequence title, CharSequence message,
                                   CharSequence negative, DialogInterface.OnClickListener negativeListener,
                                   CharSequence positive, DialogInterface.OnClickListener positiveListener) {

        MyDialog myDialog = new MyDialog(activity, true, Gravity.CENTER, title, message, () -> {
            if (positiveListener != null) {
                positiveListener.onClick(null, 0);
            }
            return Unit.INSTANCE;
        }, () -> {
            if (negativeListener != null) {
                negativeListener.onClick(null, 0);
            }
            return Unit.INSTANCE;
        }, positive, negative).show();

//        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.FrameDefaultDialogStyle).setTitle(title)
//                .setMessage(message)
//                .setNegativeButton(negative, negativeListener)
//                .setPositiveButton(positive, positiveListener).create();
//        alertDialog.show();
        return myDialog.getAlertDialog();
    }

    public static AlertDialog show(Activity activity, CharSequence title, CharSequence message,
                                   CharSequence positive, DialogInterface.OnClickListener positiveListener) {

        MyDialog myDialog = new MyDialog(activity, false, Gravity.CENTER, title, message, () -> {
            if (positiveListener != null) {
                positiveListener.onClick(null, 0);
            }
            return Unit.INSTANCE;
        }, () -> Unit.INSTANCE, positive, "").show();
        return myDialog.getAlertDialog();
//        AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.FrameDefaultDialogStyle).setTitle(title)
//                .setMessage(message)
//                .setPositiveButton(positive, positiveListener).create();
//
//        alertDialog.show();
//        return alertDialog;
    }

    /**
     * 进度框dialog
     */
    public static AlertDialog getProgressDialog(Context context, boolean iscancel) {

        return new AlertDialog.Builder(context, R.style.FrameDefaultDialogStyle)
                .setView(R.layout.frame_layout_progressbar)
                .setCancelable(iscancel)
                .create();
    }

    public static AlertDialog showProgress(Context context, String msg, boolean iscancel) {
        AlertDialog dialog = getProgressDialog(context, iscancel);
        dialog.show();
        TextView textView = dialog.findViewById(R.id.text);
        if (textView != null) {
            textView.setText(msg);
        }
        return dialog;
    }

}
