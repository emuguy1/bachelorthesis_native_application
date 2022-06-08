package de.num42.sharing.ui.profile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.num42.sharing.R
import de.num42.sharing.data.Item
import de.num42.sharing.databinding.ViewItemBinding


class ItemsAdapter : ListAdapter<Item, ItemsAdapter.ItemViewHolder>(PlantsComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ViewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

    }


    inner class ItemViewHolder(binding: ViewItemBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ),
        View.OnClickListener {
        private val itemBinding = binding

        fun bind(item: Item) {
            //reset item to Default
            itemBinding.itemTitle.text = item.name
            itemBinding.itemDesc.text = item.description
        }

        override fun onClick(p0: View?) {
//            if (p0 != null) {
//                if (p0.id == R.id.item_card) {
//                    val context = p0.context
//                    val intent = Intent(context, PlantDetailActivity::class.java).putExtra(
//                        PlantActivity.SELECTED_PLANT,
//                        currentList[layoutPosition].id
//                    )
//                    context.startActivity(intent)
//                }
//            }
        }
    }

    class PlantsComparator : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.name == newItem.name
        }
    }


}
