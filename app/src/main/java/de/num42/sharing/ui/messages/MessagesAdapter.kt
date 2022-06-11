package de.num42.sharing.ui.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.num42.sharing.data.Conversation
import de.num42.sharing.databinding.ViewConversationBinding





class MessagesAdapter : ListAdapter<Conversation, MessagesAdapter.ConversationViewHolder>(ConversationComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ViewConversationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

    }


    inner class ConversationViewHolder(binding: ViewConversationBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ),
        View.OnClickListener {
        private val itemBinding = binding

        fun bind(item: Conversation) {
            //reset item to Default
            itemBinding.conversationName.text = item.creator
            itemBinding.conversationTime.text = item.lastMessageAt
            itemBinding.conversationText.text = item.lastMessage

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

    class ConversationComparator : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.lastMessage == newItem.lastMessage
        }
    }


}
