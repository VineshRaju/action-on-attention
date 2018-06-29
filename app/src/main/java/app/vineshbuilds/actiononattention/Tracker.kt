package app.vineshbuilds.actiononattention

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Tracker constructor(private val listener: OnTrackedListener) {
    private val alreadyTracked = mutableListOf<Int>()
    private val disposable = CompositeDisposable()

    fun startTracking(rv: RecyclerView) {
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> disposable.clear()
                    RecyclerView.SCROLL_STATE_IDLE -> disposable.add(
                            Observable.range((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition(), recyclerView.childCount)
                                    .filter {
                                        !alreadyTracked.contains(it)
                                    }
                                    .delay(300, TimeUnit.MILLISECONDS)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .collect(
                                            { mutableListOf<Int>() },
                                            { list, item -> list.add(item) }
                                    ).subscribe { nowTracking ->
                                        alreadyTracked.clear()
                                        alreadyTracked.addAll(nowTracking)
                                        listener.userIsGivingAttentionTo(nowTracking)
                                    }
                    )
                }
            }
        })
    }

    fun destroy() {
        disposable.clear()
    }

    interface OnTrackedListener {
        fun userIsGivingAttentionTo(these: List<Int>)
    }
}