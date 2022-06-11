package de.num42.sharing.ui.messages
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import de.num42.sharing.apolloInstance
import de.num42.sharing.convertStringToDate
import de.num42.sharing.data.Conversation
import de.num42.sharing.data.Item
import de.num42.sharing.databinding.ActivityMessagesBinding
import de.num42.sharing.databinding.ActivityProfileBinding
import de.num42.sharing.graphql.GetConversationsQuery
import de.num42.sharing.graphql.GetItemsQuery
import de.num42.sharing.graphql.MeQuery
import de.num42.sharing.toTimeGoneByString
import de.num42.sharing.ui.main.MainActivity
import de.num42.sharing.ui.message.MessageActivity
import de.num42.sharing.ui.profile.ItemsAdapter
import de.num42.sharing.ui.profile.ProfileActivity


class MessagesActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMessagesBinding
    private lateinit var client : ApolloClient
    private var itemList = mutableListOf<Conversation>()
    private lateinit var adapter: MessagesAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var firstName = ""
    private var lastName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLoggedIn.toolbar)

        client = apolloInstance.get()
        sharedPreferences = getSharedPreferences("SharingSharedPref", MODE_PRIVATE)

        adapter = MessagesAdapter(this)
        binding.converstionsList.adapter = adapter
        binding.converstionsList.layoutManager = LinearLayoutManager(this)



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

        val meId = sharedPreferences.getString("meId","")?:""

        getConversations(meId,null)

    }

    private fun getConversations(id: String, cursor: String?){
        lifecycleScope.launchWhenResumed {
            val response = try {
                client.query(GetConversationsQuery(id, Optional.presentIfNotNull(cursor))).execute()
            }catch (e : ApolloException){
                //Do something with error
                return@launchWhenResumed
            }
            val conversations = response.data?.node?.onPerson?.conversations
            if(conversations == null || response.hasErrors()){
                println(response.errors?.get(0)?.message)
                return@launchWhenResumed
            } else {
                //save the Conversations in a list
                firstName = response.data?.node!!.onPerson?.firstName.toString()
                lastName = response.data?.node!!.onPerson?.lastName.toString()
                if(conversations.edges != null){
                    conversations.edges.forEach { edge->
                        if (edge?.node != null) {
                            var creatorString = ""
                            edge.node.onConversation.participants?.forEach{
                                if(it?.firstName != firstName){
                                    if(it?.lastName != lastName){
                                        creatorString = it?.firstName.toString() + " " + it?.lastName.toString()
                                    }
                                }
                            }
                            val newConversation = Conversation().apply {
                                lastMessage = edge.node.onConversation.lastMessage.toString()
                                creator = creatorString
                                lastMessageAt = edge.node.onConversation.lastMessageAt.toString().convertStringToDate()?.toTimeGoneByString()?:" - "
                                conversationId = edge.node.onConversation.id
                            }
                            itemList.add(newConversation)
                        }
                    }
                }
                adapter.submitList(itemList)
                if(conversations.pageInfo.hasNextPage){
                    getConversations(id,conversations.pageInfo.endCursor)
                }
            }
        }
    }

}