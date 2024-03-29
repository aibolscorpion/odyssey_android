package kz.divtech.odyssey.rotation.common.utils

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kz.divtech.odyssey.rotation.R

object RecyclerViewUtil {

    fun RecyclerView.addItemDecorationWithoutLastDivider() {

        if (layoutManager !is LinearLayoutManager)
            return

        addItemDecoration(object : RecyclerView.ItemDecoration() {
            private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.divider)!!
            override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val dividerLeft = parent.paddingLeft
                val dividerRight = parent.width - parent.paddingRight
                val childCount = parent.childCount
                for (i in 0..childCount - 2) {
                    val child = parent.getChildAt(i)
                    val params = child.layoutParams as RecyclerView.LayoutParams
                    val dividerTop = child.bottom + params.bottomMargin
                    val dividerBottom = dividerTop + mDivider.intrinsicHeight
                    mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                    mDivider.draw(canvas)
                }
            }
        })
    }
}