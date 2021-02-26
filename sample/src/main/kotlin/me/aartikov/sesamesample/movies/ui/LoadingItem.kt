package me.aartikov.sesamesample.movies.ui

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import me.aartikov.sesamesample.R
import me.aartikov.sesamesample.databinding.ItemLoadingBinding


class LoadingItem : BindableItem<ItemLoadingBinding>() {

    override fun getLayout() = R.layout.item_loading

    override fun initializeViewBinding(view: View): ItemLoadingBinding {
        return ItemLoadingBinding.bind(view)
    }

    override fun bind(binding: ItemLoadingBinding, position: Int) {}

}