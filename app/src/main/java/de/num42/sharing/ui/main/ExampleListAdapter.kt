package de.num42.sharing.ui.main

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.num42.sharing.databinding.ViewExampleItemHomescreenBinding

class ExampleListAdapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items = mutableListOf<String>()
    lateinit var itemList: RecyclerView



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        (holder as ExampleItemViewHolder).bindTo(item)
    }

    override fun getItemCount() = items.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        itemList = recyclerView
    }

    fun setList(list: MutableList<String>){
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleItemViewHolder {
        val binding = ViewExampleItemHomescreenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ExampleItemViewHolder(binding)
    }


    inner class ExampleItemViewHolder(binding: ViewExampleItemHomescreenBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ),
        View.OnClickListener {
        private val itemBinding = binding

        fun bindTo(item: String) {
            itemBinding.itemText.text = item
        }

        override fun onClick(p0: View?) {

        }
    }
}