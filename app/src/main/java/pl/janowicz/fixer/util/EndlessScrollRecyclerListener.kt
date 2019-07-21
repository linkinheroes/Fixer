package pl.janowicz.fixer.util

import androidx.recyclerview.widget.RecyclerView

class EndlessScrollRecyclerListener(private val leftItemsToLoadMore: Int, private val loadMore: () -> Unit) :
    RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0) {
            val layoutManager = recyclerView.layoutManager
            val adapter = recyclerView.adapter
            if (layoutManager != null && adapter != null && layoutManager.childCount > 0) {
                val indexOfLastItemViewVisible = layoutManager.childCount - 1
                layoutManager.getChildAt(indexOfLastItemViewVisible)?.let { lastItemViewVisible ->
                    val adapterPosition = layoutManager.getPosition(lastItemViewVisible)
                    val itemsToEnd = adapter.itemCount - adapterPosition
                    if (itemsToEnd <= leftItemsToLoadMore) {
                        loadMore()
                    }
                }
            }
        }
    }
}