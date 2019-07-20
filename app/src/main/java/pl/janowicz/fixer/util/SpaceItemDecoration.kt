package pl.janowicz.fixer.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val verticalSpace: Int, private val horizontalSpace: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.apply {
            bottom = verticalSpace
            left = horizontalSpace
            right = horizontalSpace
        }
    }
}
