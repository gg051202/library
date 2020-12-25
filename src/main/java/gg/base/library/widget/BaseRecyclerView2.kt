package gg.base.library.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import gg.base.library.util.AutoSizeTool
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import gg.base.library.R

/**
 * Created by sss on 2020/8/20 17:39.
 * email jkjkjk.com
 */
class BaseRecyclerView2 : SmartRefreshLayout {
    enum class RefreshingStatus {
        STATUS_REFRESHING,
        STATUS_LOADINGMORE,
        STATUS_COMPLETE,
    }

    private var mContext: Context
    lateinit var mAdapter: BaseQuickAdapter<*, *>
    lateinit var mRefreshingStatus: RefreshingStatus
    var mNeedShowNoMoreFooter = true
    var mNeedShowNodataView = true
    var mNeedShowErrView = true
    var mPageSize = 20
    private var mPageIndex = 1

    var mNoMoreFooterView: View? = null
    var mNoDataPlaceView: View? = null
    var mErrPlaceView: View? = null
    var mNoDataString = ""
    var mNoMoreString = ""
    var mRecyclerView: RecyclerView

    constructor(context: Context) : this(context, attributeSet = null)


    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        mContext = context
        mRecyclerView = RecyclerView(mContext)
        mRecyclerView.overScrollMode = OVER_SCROLL_NEVER
        addView(mRecyclerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    fun start(adapter: BaseQuickAdapter<*, *>, pageSize: Int = 20, layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
        context), nodataString: String = "暂无数据", noDataPlaceView: View? = null, noMoreString: String = "没有更多了", needShowNoMoreFooter: Boolean = true, func: (pageIndex: Int, pageSize: Int) -> Unit) {
        mNoDataString = nodataString
        mNoMoreString = noMoreString
        mNeedShowNoMoreFooter = needShowNoMoreFooter
        mPageSize = pageSize
        mAdapter = adapter
        mRefreshingStatus = RefreshingStatus.STATUS_REFRESHING
        mRecyclerView.adapter = adapter
        mRecyclerView.layoutManager = layoutManager
        mAdapter.animationEnable = true
        mNoDataPlaceView = noDataPlaceView

        setEnableLoadMoreWhenContentNotFull(false)

        //refreshLayout.isNestedScrollingEnabled = true //自布局嵌套滚动
        setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                mRefreshingStatus = RefreshingStatus.STATUS_REFRESHING
                mPageIndex = 1


                val isAdded = mAdapter.footerLayout != null && mAdapter.footerLayout!!.indexOfChild(getNoMoreFooterView()) >= 0
                if (mNeedShowNoMoreFooter && isAdded) {
                    mAdapter.removeFooterView(getNoMoreFooterView())
                }

                func(mPageIndex, mPageSize)
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mRefreshingStatus = RefreshingStatus.STATUS_LOADINGMORE
                func(mPageIndex, mPageSize)
            }
        })

        mAdapter.setEmptyView(getLoadingLayout())
        func(1, mPageSize)
    }

    fun callRefreshListener() {
        //        mLoadingStatus = LoadingStatus.STATUS_REFRESHING
        mAdapter.data.clear()
        mAdapter.notifyDataSetChanged()
        mAdapter.setEmptyView(getLoadingLayout())
        autoRefresh(1, 300, 1f, false)
    }

    /**
     * 请确保list中的数据类型一致
     */
    fun onLoadDataComplete(data: List<*>? = null) {
        if (!this::mAdapter.isInitialized) {
            return
        }
        finishRefresh()
        finishLoadMore()
        mRefreshingStatus = RefreshingStatus.STATUS_COMPLETE

        data?.let {
            if (mRefreshingStatus == RefreshingStatus.STATUS_REFRESHING || mPageIndex == 1) {
                mAdapter.setList(data as List<Nothing>)
            } else {
                mAdapter.addData(data as List<Nothing>)
            }
        }
        if (mPageIndex == 1 && mAdapter.data.isNotEmpty()) {
            post {
                mRecyclerView.smoothScrollToPosition(0)
            }
        }


        showNoDataPlaceView()

        if (data != null && data.size >= mPageSize) {
            setEnableLoadMore(true)
            setEnableAutoLoadMore(true)
        } else {
            setEnableLoadMore(false)
            setEnableAutoLoadMore(false)


            val notAdded = mAdapter.footerLayout == null || mAdapter.footerLayout!!.indexOfChild(getNoMoreFooterView()) < 0
            if (mNeedShowNoMoreFooter && mAdapter.data.isNotEmpty() && notAdded) {
                postDelayed({
                                mAdapter.addFooterView(getNoMoreFooterView())
                            }, 300)
            }


        }
        mPageIndex++
    }

    fun onLoadDataCompleteErr(errText: String = "加载失败") {
        if (isRefreshing || mPageIndex == 1) {
            mAdapter.data.clear()
            mAdapter.notifyDataSetChanged()
        }
        if (mAdapter.data.isEmpty()) {
            showErrPlaceView(errText)
        } else {
            ToastUtils.showShort(errText)
        }

        finishRefresh()
        finishLoadMore()
        setEnableLoadMore(true)
        mRefreshingStatus = RefreshingStatus.STATUS_COMPLETE
    }

    private fun showNoDataPlaceView() {
        if (mAdapter.data.isEmpty() && mNeedShowNodataView) {
            getNoDataPlaceView().placeViewDefaultOperation(mNoDataString)
        }
    }

    private fun showErrPlaceView(errString: String) {
        if (mAdapter.data.isEmpty() && mNeedShowErrView) {
            getErrPlaceView().placeViewDefaultOperation(errString)

        }
    }

    private fun View.placeViewDefaultOperation(tvString: String) {
        findViewById<TextView>(R.id.tv)?.text = tvString
        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mAdapter.setEmptyView(this)
        findViewById<View>(R.id.refresh)?.apply {
            if (!hasOnClickListeners()) {
                setOnClickListener { callRefreshListener() }
            }
        }
    }

    private fun getLoadingLayout(): FrameLayout {
        val loadingViewLayout = FrameLayout(mContext)
        loadingViewLayout.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        val layoutParams = FrameLayout.LayoutParams(AutoSizeTool.dp2px(130), AutoSizeTool.dp2px(130))
        layoutParams.gravity = Gravity.CENTER

        val lineLoadingView = LottieAnimationView(mContext)
        lineLoadingView.setAnimation("frame_anim_loading_line.json")
        lineLoadingView.repeatCount = LottieDrawable.INFINITE
        lineLoadingView.playAnimation()
        loadingViewLayout.addView(lineLoadingView, layoutParams)
        return loadingViewLayout
    }


    private fun getNoMoreFooterView(): View {
        if (mNoMoreFooterView == null) {
            mNoMoreFooterView = inflate(mContext, R.layout.frame_custome_layout_default_no_more_footer_view, null)
            val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mNoMoreFooterView?.layoutParams = layoutParams
        }
        if (mNoMoreFooterView is TextView) {
            (mNoMoreFooterView as TextView).text = mNoMoreString
        }
        return mNoMoreFooterView as View
    }

    private fun getNoDataPlaceView(): View {
        if (mNoDataPlaceView == null) {
            mNoDataPlaceView = inflate(mContext, R.layout.frame_custome_layout_default_no_data_place_view, null)
        }
        return mNoDataPlaceView as View
    }


    private fun getErrPlaceView(): View {
        if (mErrPlaceView == null) {
            mErrPlaceView = inflate(mContext, R.layout.frame_custome_layout_default_err_place_view, null)
        }
        return mErrPlaceView as View
    }

    fun isLoadingData(): Boolean {
        if (!this::mRefreshingStatus.isInitialized) {
            return false
        }
        return mRefreshingStatus == RefreshingStatus.STATUS_LOADINGMORE || mRefreshingStatus == RefreshingStatus.STATUS_REFRESHING
    }

}