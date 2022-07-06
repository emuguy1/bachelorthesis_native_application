package de.num42.sharing.ui.message

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import de.num42.sharing.apolloInstance
import de.num42.sharing.convertStringToDate
import de.num42.sharing.data.Message
import de.num42.sharing.databinding.ActivityMessageBinding
import de.num42.sharing.graphql.GetMessagesQuery
import de.num42.sharing.graphql.PostMessageMutation
import de.num42.sharing.toTimeGoneByString
import de.num42.sharing.ui.main.MainActivity
import de.num42.sharing.ui.messages.MessagesActivity
import de.num42.sharing.ui.profile.ProfileActivity

class MessageActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding
    private lateinit var client : ApolloClient
    private var itemList = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var conversationId: String
    private lateinit var meId: String
    private lateinit var sendToId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        client = apolloInstance.get()
        sharedPreferences = getSharedPreferences("SharingSharedPref", MODE_PRIVATE)

        val bundle = savedInstanceState ?: intent.extras
        if (bundle != null) {
            conversationId = bundle.getString(MessageActivity.SELECTED_CONVERSATION).toString()
        }
        if(conversationId.isNullOrEmpty()){
            Toast.makeText(applicationContext,"Konversation konnte nicht gefunden werden. Versuchen sie es nochmal",
                Toast.LENGTH_LONG).show()
            startActivity(Intent(this,MessagesActivity::class.java))
        }

        meId = sharedPreferences.getString("meId","")?:""

        adapter = MessageAdapter()
        binding.messageList.adapter = adapter
        binding.messageList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,true)


        setSupportActionBar(binding.toolbarLoggedIn.toolbar)

        binding.toolbarLoggedIn.toolbarTitle.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.toolbarLoggedIn.toolbarMessagesButton.setOnClickListener{
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

        binding.toolbarLoggedIn.toolbarLogoutButton.setOnClickListener{
            //Logout
            sharedPreferences.edit().remove("authentication").apply()
            sharedPreferences.edit().remove("meId").apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.sendButton.setOnClickListener{
            performSend()
        }


        getConversations(conversationId,null)

    }

    private fun performSend(){
        lifecycleScope.launchWhenResumed {
            val response = try {
                val message = binding.messageInput.text.toString()
                client.mutation(PostMessageMutation(sendToId ,message)).execute()
            }catch (e : ApolloException){
                Toast.makeText(applicationContext, "Message could not be send! Try again later!",Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            }
            val authentication = response.data?.postMessage
            if(authentication == null || response.hasErrors()){
                println(response.errors?.get(0)?.message)
                Toast.makeText(applicationContext, "Message could not be send! Try again later!",Toast.LENGTH_LONG).show()
                return@launchWhenResumed
            } else {
                binding.messageInput.text.clear()
                itemList.clear()
                adapter.notifyDataSetChanged()
                getConversations(conversationId,null)
            }
        }
    }

    private fun getConversations(convId: String, cursor: String?){
        lifecycleScope.launchWhenResumed {
            val response = try {
                client.query(GetMessagesQuery(convId, Optional.presentIfNotNull(cursor))).execute()
            }catch (e : ApolloException){
                //Do something with error
                return@launchWhenResumed
            }
            val messages = response.data?.node?.onConversation?.messages
            if(messages == null || response.hasErrors()){
                println(response.errors?.get(0)?.message)
                return@launchWhenResumed
            } else {
                //save the Conversations in a list
                val conversationPartners = response.data?.node!!.onConversation?.participants

                val convPartner = if(conversationPartners?.first()?.id  == meId){
                    conversationPartners.last()
                }else{
                    conversationPartners?.first()
                }
                val me = if(conversationPartners?.first()?.id  == meId){
                    conversationPartners.first()
                }else{
                    conversationPartners?.last()
                }
                val firstName = convPartner?.firstName.toString()
                val lastName = convPartner?.lastName.toString()
                sendToId = convPartner?.id.toString()

                if(messages.edges != null){
                    messages.edges.forEach { edge->
                        if (edge?.node != null) {
                            val message = edge.node.onMessage
                            val newMessage = Message().apply {
                                this.message = message.message.toString()
                                messagePartner = "$firstName $lastName"
                                sendByMe = message.sender?.id.equals(meId)
                                if(sendByMe){
                                    sender= me?.firstName.toString() + " " + me?.lastName.toString()
                                }else {
                                    sender = messagePartner
                                }
                                sendtime = message.timestamp.toString().convertStringToDate()?.toTimeGoneByString()?:" - "
                            }
                            itemList.add(newMessage)
                        }
                    }
                }

                adapter.submitList(itemList)
                adapter.notifyDataSetChanged()
                if(messages.pageInfo.hasNextPage){
                    getConversations(convId,messages.pageInfo.endCursor)
                }
            }
        }
    }

    companion object {
        const val SELECTED_CONVERSATION = "selected_conversation"

    }
}

