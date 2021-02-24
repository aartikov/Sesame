package me.aartikov.sesamesample.movies.ui

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.databinding.ItemLoadBinding


class LoadingItem : BindableItem<ItemLoadBinding>() {

    override fun getLayout() = R.layout.item_load

    override fun initializeViewBinding(view: View): ItemLoadBinding {
        return ItemLoadBinding.bind(view)
    }

    override fun bind(binding: ItemLoadBinding, position: Int) {}

}