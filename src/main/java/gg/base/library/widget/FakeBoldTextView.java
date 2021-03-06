package gg.base.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import gg.base.library.R;


/**
 * Created by guilinlin on 2017/8/16 10:41.
 * email 973635949@qq.com
 * 纤细的粗体
 */
public class FakeBoldTextView extends AppCompatTextView {

    private int color;
    private float boldSize;

    public FakeBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FakeBoldTextView);
        boldSize = array.getFloat(R.styleable.FakeBoldTextView_fbt_bold_size, 0.3f);
        array.recycle();

        updateString(getText().toString());
    }

    public FakeBoldTextView(Context context) {
        super(context);

        color = 0;
        boldSize = 0.3f;

        updateString(getText().toString());
    }

    private void updateString(String text) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new FakeBoldSpan(boldSize, color), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        super.setText(spannableString);
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        updateString(getText().toString());
    }

    public float getBoldSize() {
        return boldSize;
    }

    public void setBoldSize(float boldSize) {
        this.boldSize = boldSize;
        updateString(getText().toString());
    }

    public void setBoldText(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        updateString(charSequence.toString());
    }


}
