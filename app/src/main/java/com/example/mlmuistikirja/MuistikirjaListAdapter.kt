package com.example.mlmuistikirja

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MuistikirjaListAdapter(private val updateCallbackInterface:UpdateCallbackInterface, private val viewModel: MuistikirjaViewModel) : ListAdapter<Muistikirja, MuistikirjaListAdapter.MuistikirjaViewHolder>(MuistikirjaComparator()) {

    interface UpdateCallbackInterface {
        fun updateCallback(muistikirja: Muistikirja)
    }

    fun deleteItem(position: Int) {
        val current = getItem(position)
        viewModel.delete(current)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuistikirjaViewHolder {
        return MuistikirjaViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MuistikirjaViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        val statusCheckBox = holder.itemView.findViewById<CheckBox>(R.id.readStatus)

        statusCheckBox.setOnCheckedChangeListener(null)
        statusCheckBox.isChecked = current.read_status

        // Merkkaa luetuksi toiminto
        statusCheckBox.setOnCheckedChangeListener{ _, isChecked ->
            current.read_status = isChecked
            updateCallbackInterface.updateCallback(current)
        }
    }

    class MuistikirjaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val muistikirjaItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(muistikirja: Muistikirja) {
            // teksti jota näytetään recyclerview:ssä
            muistikirjaItemView.text = muistikirja.muistikirja

            // Lisää yliviiva jos teksti on merkattu luetuksi
            if (muistikirja.read_status) {
                muistikirjaItemView.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = muistikirja.muistikirja
                }
            } else {
                muistikirjaItemView.apply {
                    paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    text = muistikirja.muistikirja
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): MuistikirjaViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return MuistikirjaViewHolder(view)
            }
        }
    }

    class MuistikirjaComparator : DiffUtil.ItemCallback<Muistikirja>() {
        override fun areItemsTheSame(oldItem: Muistikirja, newItem: Muistikirja): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Muistikirja, newItem: Muistikirja): Boolean {
            return oldItem.muistikirja == newItem.muistikirja
        }
    }
}