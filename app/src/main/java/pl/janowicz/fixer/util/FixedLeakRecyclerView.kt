package pl.janowicz.fixer.util

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class FixedLeakRecyclerView : RecyclerView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (adapter != null) {
            adapter = null
        }
    }
}