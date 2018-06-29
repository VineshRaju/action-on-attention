package app.vineshbuilds.actiononattention

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Tracker constructor() {
    private val alreadyTracked = mutableListOf<Int>()
    private val disposable = CompositeDisposable()
    private var listener: OnTrackedListener? = null

    constructor(listener: OnTrackedListener) : this() {
        this.listener = listener
    }

    fun startTracking(rv: RecyclerView) {
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> disposable.clear()
                    RecyclerView.SCROLL_STATE_IDLE -> disposable.add(
                            trackIfNotPreviouslyTracked((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition(), recyclerView.childCount)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe { nowTracking ->
                                        listener?.userIsGivingAttentionTo(nowTracking)
                                    }
                    )
                }
            }
        })
    }

    fun trackIfNotPreviouslyTracked(start: Int, count: Int): Single<MutableList<Int>> =
            Observable.range(start, count)
                    .filter {
                        !alreadyTracked.contains(it)
                    }
                    .delay(300, TimeUnit.MILLISECONDS)
                    .collect(
                            { mutableListOf<Int>() },
                            { list, item -> list.add(item) }
                    )
                    .doAfterSuccess { nowTracking ->
                        alreadyTracked.clear()
                        alreadyTracked.addAll(nowTracking)
                    }

    fun destroy() {
        disposable.clear()
    }

    interface OnTrackedListener {
        fun userIsGivingAttentionTo(these: List<Int>)
    }
}