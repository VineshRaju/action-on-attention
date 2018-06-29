package app.vineshbuilds.actiononattention

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val disposable = CompositeDisposable()
    val alreadyTracked = mutableListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = ListAdapter(10)


        rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> disposable.clear()
                    RecyclerView.SCROLL_STATE_IDLE -> disposable.add(
                            Observable.range((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition(), recyclerView.childCount)
                                    .skipWhile {
                                        alreadyTracked.contains(it)
                                    }
                                    .delay(300, TimeUnit.MILLISECONDS)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .collect(
                                            { mutableListOf<Int>() },
                                            { list, item -> list.add(item) }
                                    ).subscribe { nowTracking ->
                                        alreadyTracked.clear()
                                        alreadyTracked.addAll(nowTracking)
                                        Log.e("Tracking", nowTracking.toString())
                                    }
                    )
                }
            }
        })
    }

}
