package gg.base.library.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import gg.base.library.R;
import gg.base.library.widget.MyDialog;
import kotlin.Unit;
import rx.Observable;
import rx.schedulers.Schedulers;

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
     * @param list map必须有  key和value两组键值对
     */
    public static <T extends ChoiceData> AlertDialog showSingle(Context context, final List<T> list, String selectKey, final OnDialogSelectedListener<T> listener) {
        CharSequence[] scList = new CharSequence[list.size()];
        int select = -1;
        for (int i = 0; i < list.size(); i++) {
            scList[i] = list.get(i).getDesc();
            if (select == -1 && !TextUtils.isEmpty(selectKey) && selectKey.equals(list.get(i).getKey())) {
                select = i;
            }
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.FrameDefaultDialogStyle)
                .setSingleChoiceItems(scList, select, (dialog, which) -> {
                    listener.onSelect(list.get(which), null);
                    Observable.just(1)
                            .subscribeOn(Schedulers.io())
                            .map(integer -> {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            })
                            .observeOn(AndroidScheduler.mainThread())
                            .subscribe(o -> dialog.dismiss());

                }).create();
        alertDialog.show();
        return alertDialog;

    }


    /**
     * 多选的dialog
     *
     * @param list map必须有  key和value两组键值对
     */
    public static <T extends ChoiceData> AlertDialog showMulti(Context context, final List<T> list, final OnDialogSelectedListener<T> listener) {
        CharSequence[] valueList = new CharSequence[list.size()];
        boolean[] checkedItems = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            valueList[i] = list.get(i).getDesc();
            checkedItems[i] = list.get(i).isSelected();
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.FrameDefaultDialogStyle)
                .setMultiChoiceItems(valueList, checkedItems, (dialog, which, isChecked) -> list.get(which).setSelected(isChecked))
                .setPositiveButton("确定", (dialog, which) -> listener.onSelect(null, list))
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
        return alertDialog;

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

    public static AlertDialog getProgress(Context context, String msg, boolean iscancel) {
        AlertDialog dialog = getProgressDialog(context, iscancel);
        TextView textView = dialog.findViewById(R.id.text);
        if (textView != null) {
            textView.setText(msg);
        }
        return dialog;
    }


    public interface OnDialogSelectedListener<T extends ChoiceData> {
        /**
         * 如果是单选key就是一个值，不然是逗号分隔
         */
        void onSelect(T data, List<T> list);
    }

    public interface ChoiceData {

        String getKey();

        CharSequence getDesc();

        boolean isSelected();

        void setSelected(boolean selected);

    }

    public static class SimpleChoiceData implements ChoiceData {
        private String key;
        private CharSequence desc;
        private boolean isSelected;

        public SimpleChoiceData(String key, CharSequence desc) {
            this.key = key;
            this.desc = desc;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setDesc(CharSequence desc) {
            this.desc = desc;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public CharSequence getDesc() {
            return desc;
        }

        @Override
        public boolean isSelected() {
            return isSelected;
        }

        @Override
        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

}
