package br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallback<T> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem == newItem
}