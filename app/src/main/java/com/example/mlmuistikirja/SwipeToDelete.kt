package com.example.mlmuistikirja

import android.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

// https://www.youtube.com/watch?v=LNL97As-wP4
class SwipeToDelete(private val adapter: MuistikirjaListAdapter) : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
    override fun onMove(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      target: RecyclerView.ViewHolder
    ): Boolean {
      TODO("not implemented")
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        val builder = AlertDialog.Builder(viewHolder.itemView.context)
        builder.setMessage("Haluatko varmasti poistaa?")
            .setCancelable(false)
            .setPositiveButton("Kyllä") { _, _ ->
                adapter.deleteItem(position)
            }
            .setNegativeButton("Ei") {dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}