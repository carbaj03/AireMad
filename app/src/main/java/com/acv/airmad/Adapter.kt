package com.acv.airmad

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup


typealias SkillAdapter = Adapter<ListViewHolder, Skill>

abstract class ViewHolder<in M>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(model: M)
}

interface ItemVisitable {
    fun type(): Int
}

open class Adapter<VH : ViewHolder<M>, in M : ItemVisitable>(
        private var items: MutableList<M> = mutableListOf(),
        val holder: (view: View) -> VH,
        private val listener: (M) -> Unit
) : RecyclerView.Adapter<VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            holder(parent.inflate(viewType))

    override fun onBindViewHolder(holder: VH, position: Int) = with(holder) {
        bind(items[position])
        itemView.setOnClickListener { listener(items[position]) }
    }

    override fun getItemViewType(position: Int) =
            items[position].type()

    override fun getItemCount() =
            items.size

    fun add(l: List<M>) = with(items) {
        clear()
        addAll(l)
        notifyDataSetChanged()
    }
}
