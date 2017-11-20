package com.acv.airmad.ui.search

import android.view.View
import android.widget.TextView
import com.acv.airmad.ui.common.ItemVisitable
import com.acv.airmad.R
import com.acv.airmad.ui.common.ViewHolder


class ListViewHolder(view: View) : ViewHolder<Station>(view) {
    var tvName: TextView = view.findViewById(R.id.tvName)

    override fun bind(model: Station) =
            with(model) {
                tvName.text = name
            }
}

data class Station (
        val id: String,
        val name: String,
        val latitude: Float,
        val longitude: Float
) : ItemVisitable {
    override fun type() = R.layout.item_station
}
