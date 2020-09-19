package gg.base.library.widget

import android.content.Context
import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.dongjin.mylibrary.R


/**
 * Created by guilinlin on 2017/8/16 10:41.
 * email 973635949@qq.com
 * 纤细的粗体
 */
class FakeBoldTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : AppCompatTextView(
    context,
    attrs) {
    private var mColor: Int
    private var mBoldSize: Float
    private var mText: String

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.FakeBoldTextView)
        mColor = currentTextColor
        mBoldSize = array.getFloat(R.styleable.FakeBoldTextView_fbt_bold_size, 1f)
        mText = text.toString()
        array.recycle()
        update()
    }


    private fun update() {
        val spannableString = SpannableString(mText)
        spannableString.setSpan(FakeBoldSpan(mBoldSize, mColor),
                                0,
                                mText.length,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        super.setText(spannableString)
    }

    fun setColor(color: Int) {
        mColor = color
        update()
    }

    fun setBoldSize(boldSize: Float) {
        mBoldSize = boldSize
        update()
    }

    fun setBoldText(text: String) {
        mText = text
        update()
    }

    class FakeBoldSpan : CharacterStyle {
        private var boldSize = 1f
        private var color = 0

        constructor(boldSize: Float) {
            this.boldSize = boldSize
        }

        constructor() {}
        constructor(boldSize: Float, color: Int) {
            this.boldSize = boldSize
            this.color = color
        }

        override fun updateDrawState(tp: TextPaint) {
            tp.style = Paint.Style.FILL_AND_STROKE
            tp.strokeWidth = boldSize //控制字体加粗的程度
            if (color != 0) {
                tp.color = color
            }
        }

        fun setBold(bold: Float) {
            boldSize = bold
        }

        fun setColor(color: Int) {
            this.color = color
        }
    }


}