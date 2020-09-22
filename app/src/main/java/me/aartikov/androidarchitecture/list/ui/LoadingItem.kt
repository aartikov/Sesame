package me.aartikov.androidarchitecture.list.ui

import android.util.Log
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import me.aartikov.androidarchitecture.R


class LoadingItem : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Log.d("MovieLoader", "LoadingItem.bind() invoked")
    }

    override fun getLayout() = R.layout.item_load
}