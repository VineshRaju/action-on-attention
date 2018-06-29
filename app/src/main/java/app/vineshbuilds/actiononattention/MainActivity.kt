package app.vineshbuilds.actiononattention

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Tracker.OnTrackedListener {
    private val tracker: Tracker by lazy { Tracker(this) }
    private val adapter: ListAdapter by lazy { ListAdapter(10) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter
        tracker.startTracking(rvList)
    }

    override fun userIsGivingAttentionTo(these: List<Int>) {
        Log.d("TRACKING", these.toString())
        if (these.isNotEmpty()) {
            adapter.notifyItemRangeChanged(these[0], these.size)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tracker.destroy()
    }

}
