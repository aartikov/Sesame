package me.aartikov.androidarchitecture.movies.ui

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import me.aartikov.androidarchitecture.R


class LoadingItem : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) { }

    override fun getLayout() = R.layout.item_load
}