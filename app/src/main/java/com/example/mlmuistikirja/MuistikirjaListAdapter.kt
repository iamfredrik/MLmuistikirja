package com.example.mlmuistikirja

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MuistikirjaListAdapter : ListAdapter<Muistikirja, MuistikirjaListAdapter.MuistikirjaViewHolder>(MuistikirjaComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuistikirjaViewHolder {
        return MuistikirjaViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MuistikirjaViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.muistikirja)
    }

    class MuistikirjaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val muistikirjaItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            muistikirjaItemView.text = text
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