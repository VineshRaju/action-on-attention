package app.vineshbuilds.actiononattention

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_layout.view.*

class ListAdapter(private val itemsCount: Int) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p: Int) = ListViewHolder(LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_layout, viewGroup, false))

    override fun getItemCount() = itemsCount

    override fun onBindViewHolder(vh: ListViewHolder, p: Int) = vh.bind(p)


    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            private val randomColorValues = arrayOf(-7396033, -12341070, -2290072, -1756757, -4325382, -3459802, -10892574, -9786444, -7883409, -2469112)
        }

        fun bind(pos: Int) {
            itemView.setBackgroundColor(randomColorValues[pos % randomColorValues.size])
            itemView.tvText.text = "$pos"
        }
    }

}