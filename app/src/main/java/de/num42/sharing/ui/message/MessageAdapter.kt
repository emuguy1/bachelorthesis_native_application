package de.num42.sharing.ui.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.num42.sharing.data.Message
import de.num42.sharing.databinding.ViewMessageBinding


class MessageAdapter  : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ViewMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

    }


    inner class MessageViewHolder(binding: ViewMessageBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ),
        View.OnClickListener {
        private val itemBinding = binding

        fun bind(item: Message) {
            //reset item to Default
            itemBinding.backPlaceholder.visibility = View.VISIBLE
            itemBinding.frontPlaceholder.visibility = View.VISIBLE

            itemBinding.messageTime.text = item.sender + " - " + item.sendtime
            itemBinding.messageText.text = item.message
            if(item.sendByMe){
                itemBinding.backPlaceholder.visibility = View.GONE
            }else{
                itemBinding.frontPlaceholder.visibility = View.GONE
            }
        }

        override fun onClick(p0: View?) {
            //Do nothing
        }
    }

    class MessageComparator : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.message == newItem.message
        }
    }


}
