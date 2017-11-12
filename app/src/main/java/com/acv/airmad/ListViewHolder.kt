package com.acv.airmad

import android.view.View
import android.widget.ImageView
import android.widget.TextView


class ListViewHolder(view: View) : ViewHolder<Skill>(view) {
    var tvName: TextView = view.findViewById(R.id.tvName)

    override fun bind(model: Skill) =
            with(model) {
                tvName.text = name
            }
}

data class Skill (
        val id: String,
        val name: String
) : ItemVisitable {
    override fun type() = R.layout.item_skill
}
