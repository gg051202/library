package gg.base.library.widget;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gg.base.library.R;
import gg.base.library.adapter.adapter.CommonListDialogAdapter;

/**
 * 统一风格的dialog
 */
public class CommonListDialog implements View.OnClickListener {


    private AlertDialog mAlertDialog;
    private Activity    mActivity;

    private FakeBoldTextView mTitleTextView;
    private RecyclerView     mRecyclerView;
    private TextView         mCancelTextView;
    private TextView mSubmitTextView;

    private List<CommonListDialog.Data> mList;
    private String mTitleName = "";
    private String mcancleName = "取消";
    private String mSubmitName = "确定";
    private boolean showButtonLayout = true;
    private boolean mShowCheckBox = true;

    private OnDialogCancleListener mDialogCancleListener;
    private OnDialogSubmitListener mDialogSubmitListener;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private CommonListDialogAdapter mTypeAdapter;
    private String mCurrentKey = "";

    public CommonListDialog(Activity activity) {
        this.mActivity = activity;
        mList = new ArrayList<>();
    }

    public CommonListDialog(Activity activity, String titleName, OnDialogSubmitListener dialogSubmitListener) {
        this.mActivity = activity;
        mList = new ArrayList<>();
        mTitleName = titleName;
        mSubmitName = "确定";
        mcancleName = "取消";
        mDialogSubmitListener = dialogSubmitListener;
    }

    public CommonListDialog addData(String key, String name) {
        mList.add(new CommonListDialogData(key, name, 0));
        return this;
    }


    public void show() {
        if (mList == null || mList.size() == 0) {
            throw new RuntimeException("CommonListDialog 请传入list");
        }
        mList.get(0).setSelected(true);
        if (mAlertDialog == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_cartoon_list, null);
            mRecyclerView = view.findViewById(R.id.recyclerView);
            mTitleTextView = view.findViewById(R.id.titleTextView);
            mCancelTextView = view.findViewById(R.id.cancelTextView);
            mSubmitTextView = view.findViewById(R.id.submitTextView);

            mTitleTextView.setBoldText(mTitleName);
            if (TextUtils.isEmpty(mTitleName)) {
                mTitleTextView.setVisibility(View.GONE);
            }
            mCancelTextView.setText(mcancleName);
            mSubmitTextView.setText(mSubmitName);
            mTypeAdapter = new CommonListDialogAdapter(mList, mShowCheckBox);
            for (Data data : mList) {
                if (data.getKey().equals(mCurrentKey)) {
                    data.setSelected(true);
                } else {
                    data.setSelected(false);
                }
            }

            mRecyclerView.setAdapter(mTypeAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            mTypeAdapter.setOnItemClickListener((adapter, view1, position) -> {
                for (Data item : mTypeAdapter.getData()) {
                    item.setSelected(false);
                }
                mTypeAdapter.getData().get(position).setSelected(true);
                mTypeAdapter.notifyDataSetChanged();
                if (!showButtonLayout) {
                    onClick(mSubmitTextView);
                }
            });

            mSubmitTextView.setVisibility(showButtonLayout ? View.VISIBLE : View.GONE);
            mCancelTextView.setVisibility(showButtonLayout ? View.VISIBLE : View.GONE);
            mCancelTextView.setOnClickListener(this);
            mSubmitTextView.setOnClickListener(this);

            mAlertDialog = new AlertDialog.Builder(mActivity, R.style.MultiDialogStyle)
                    .setView(view)
                    .show();
            if (mOnDismissListener != null) {
                mAlertDialog.setOnDismissListener(mOnDismissListener);
            }
            Window window = mAlertDialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
            }

        }
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }

    public CommonListDialog setTitleName(String titleName) {
        if (!TextUtils.isEmpty(titleName)) {
            mTitleName = titleName;
        }
        return this;
    }

    public CommonListDialog setCancleName(String cancleName) {
        if (!TextUtils.isEmpty(cancleName)) {
            this.mcancleName = cancleName;
        }
        return this;
    }

    public CommonListDialog setSubmitName(String submitName) {
        if (!TextUtils.isEmpty(submitName)) {
            mSubmitName = submitName;
        }
        return this;
    }

    public CommonListDialog setDialogCancleListener(OnDialogCancleListener dialogCancleListener) {
        mDialogCancleListener = dialogCancleListener;
        return this;
    }

    public CommonListDialog setDialogSubmitListener(OnDialogSubmitListener dialogSubmitListener) {
        mDialogSubmitListener = dialogSubmitListener;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitTextView) {
            if (mList == null || mTypeAdapter == null) {
                return;
            }
            int position = -1;
            for (int i = 0; i < mTypeAdapter.getData().size(); i++) {
                if (mTypeAdapter.getData().get(i).isSelected()) {
                    position = i;
                }
            }
            if (mDialogSubmitListener != null) {
                mDialogSubmitListener.submit(mList.get(position).getKey(), mList.get(position).getName());
            }
            if (mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
        } else if (v.getId() == R.id.cancelTextView) {
            if (mDialogCancleListener != null) {
                mDialogCancleListener.cancle();
            }
            if (mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
        }
    }

    public AlertDialog getAlertDialog() {
        return mAlertDialog;
    }

    public void setAlertDialog(AlertDialog alertDialog) {
        mAlertDialog = alertDialog;
    }

    public List<CommonListDialog.Data> getList() {
        return mList;
    }

    public void setList(List<CommonListDialog.Data> list) {
        mList = list;
    }

    public CommonListDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return this;
    }

    public interface OnDialogSubmitListener {
        void submit(String key, String value);
    }

    public interface OnDialogCancleListener {
        void cancle();
    }

    public boolean isShowButtonLayout() {
        return showButtonLayout;
    }

    public CommonListDialog setShowButtonLayout(boolean showButtonLayout) {
        this.showButtonLayout = showButtonLayout;
        return this;
    }

    public CommonListDialog setShowCheckBox(boolean showCheckBox) {
        mShowCheckBox = showCheckBox;
        return this;
    }

    public interface Data {

        boolean isSelected();

        void setSelected(boolean isSeclted);

        String getName();

        String getKey();

        int getResId();

    }

    public String getCurrentKey() {
        return mCurrentKey;
    }

    public CommonListDialog setCurrentKey(String currentKey) {
        mCurrentKey = currentKey;
        return this;
    }
}

