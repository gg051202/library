package gg.base.library.activity;

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.dongjin.mylibrary.R
import gg.base.library.base.BaseActivity
import gg.base.library.base.others.InitConfigData
import gg.base.library.util.AutoSizeTool
import gg.base.library.vm.ImageBrowserActivityViewModel
import kotlinx.android.synthetic.main.frame_image_browser.*

class ImageBrowserActivity : BaseActivity() {

    companion object {
        fun go(baseActivity: BaseActivity, url: String, urlList: ArrayList<String> = ArrayList()) {
            baseActivity.goActivity(ImageBrowserActivity::class.java,
                                    bundleOf(Pair("url", url), Pair("urlList", urlList)))
            baseActivity.overridePendingTransition(R.anim.activity_fade_in_500, 0)
        }
    }

    private var urlList = ArrayList<String>()
    private var mPointImageViewList = ArrayList<ImageView>()

    lateinit var mViewModel: ImageBrowserActivityViewModel

    override fun getActivityInitConfig(): InitConfigData {
        mViewModel = getActivityViewModel()
        return InitConfigData(layoutId = R.layout.frame_image_browser,
                              viewModel = mViewModel,
                              showActionBar = false,
                              onClickProxy = OnClickProxy())
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun init() {
        val url = intent.getStringExtra("url")
        urlList = intent.getStringArrayListExtra("urlList")
        if (urlList.isEmpty()) {
            urlList.add(url)
        }

        for (s in urlList) {
            val pointImageView = ImageView(mActivity)
            val dp6: Int = AutoSizeTool.dp2px(6)
            val dp5 = dp6 / 2
            val params = MarginLayoutParams(dp6, dp6)
            params.setMargins(dp5, dp5, dp5, dp5)
            pointImageView.layoutParams = params
            pointImageView.setImageResource(R.mipmap.frame_point111)
            mPointImageViewList.add(pointImageView)
            pointLayout.addView(pointImageView)
        }

        viewPager.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val imageView = SubsamplingScaleImageView(mActivity)
                imageView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.MATCH_PARENT)
                imageView.setOnClickListener {
                    finish()
                    overridePendingTransition()
                }
                return BaseViewHolder(imageView)

            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                Glide.with(mActivity).load(urlList[position]).into(object : SimpleTarget<Drawable?>() {

                    override fun onResourceReady(resource: Drawable,
                                                 transition: com.bumptech.glide.request.transition.Transition<in Drawable?>?) {
                        val bitmapDrawable = resource as BitmapDrawable
                        (holder.itemView as SubsamplingScaleImageView).setImage(ImageSource.bitmap(
                            bitmapDrawable.bitmap))
                    }
                })
            }

            override fun getItemCount(): Int {
                return urlList.size
            }
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentPoint(position)
            }
        })
        viewPager.offscreenPageLimit = 3
        val item: Int = urlList.indexOf(url)
        viewPager.currentItem = item
        setCurrentPoint(item)
    }

    private fun setCurrentPoint(position: Int) {
        if (urlList.isEmpty()) {
            return
        }
        for (imageView in mPointImageViewList) {
            imageView.setImageResource(R.mipmap.frame_point111)
        }
        mPointImageViewList[position].setImageResource(R.mipmap.frame_point222)
    }

    private fun overridePendingTransition() {
        overridePendingTransition(0, R.anim.activity_fade_out_500)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            overridePendingTransition()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    inner class OnClickProxy {
        fun closeLog() {

        }
    }

}