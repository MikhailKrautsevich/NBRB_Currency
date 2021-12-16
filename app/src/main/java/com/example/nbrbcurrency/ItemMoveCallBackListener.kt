package com.example.nbrbcurrency

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemMoveCallBackListener(private val adapter: SettingsFragment.SettingsAdapter) :
    ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPos: Int = viewHolder.adapterPosition
        val toPos: Int = target.adapterPosition
        if (toPos > 0) {
            adapter.onRowMoved(fromPos, toPos)
        } else adapter.onRowMoved(toPos, fromPos)                                                   //так не падает при попытке поставить на позицию №0
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is SettingsFragment.SettingHolder) {
            adapter.onRowClear(viewHolder)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is SettingsFragment.SettingHolder) {
                adapter.onRowSelected(viewHolder)
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    interface Listener {
        fun onRowMoved(fromPos: Int, toPos: Int)
        fun onRowSelected(itemViewHolder: SettingsFragment.SettingHolder)
        fun onRowClear(itemViewHolder: SettingsFragment.SettingHolder)
    }
}