package de.num42.sharing.ui.profile

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
import de.num42.sharing.data.Item
import de.num42.sharing.databinding.ActivityProfileBinding
import de.num42.sharing.graphql.GetItemsQuery
import de.num42.sharing.graphql.MeQuery
import de.num42.sharing.ui.main.MainActivity
import de.num42.sharing.ui.messages.MessagesActivity

class ProfileActivity: AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var client : ApolloClient
    private var itemList = mutableListOf<Item>()
    private lateinit var adapter: ItemsAdapter
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        client = apolloInstance.get()
        sharedPreferences = getSharedPreferences("SharingSharedPref", MODE_PRIVATE)

        adapter = ItemsAdapter()
        binding.itemList.adapter = adapter
        binding.itemList.layoutManager = LinearLayoutManager(this)


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


        getPersonData()

    }
    private fun returnToMainPage(){
        startActivity(Intent(this,MainActivity::class.java))
    }

    private fun getPersonData(){
        lifecycleScope.launchWhenResumed {
            val response = try {
                client.query(MeQuery()).execute()
            }catch (e : ApolloException){
                //Do something with error
                return@launchWhenResumed
            }
            val me = response.data?.me
            if(me == null || response.hasErrors()){
                println(response.errors?.get(0)?.message)
                if(response.errors?.get(0)?.message == "Nuter nicht gefunden."){
                    sharedPreferences.edit().remove("authorization")
                    client = apolloInstance.resetAuthorization()
                    returnToMainPage()
                }
                return@launchWhenResumed
            } else {
                binding.firstNameText.text = me.firstName
                binding.lastNameText.text = me.lastName
                getItems(me.id, null)
                sharedPreferences.edit().putString("meId",me.id).apply()
            }
        }
    }
    private fun getItems(id: String, cursor: String?){
        lifecycleScope.launchWhenResumed {
            val response = try {
                client.query(GetItemsQuery(id, Optional.presentIfNotNull(cursor))).execute()
            }catch (e : ApolloException){
                //Do something with error
                return@launchWhenResumed
            }
            val items = response.data?.node?.onPerson?.items
            if(items == null || response.hasErrors()){
                println(response.errors?.get(0)?.message)
                return@launchWhenResumed
            } else {
                //save the items in a list

                if(items.pageInfo.hasNextPage){
                    getItems(id,items.pageInfo.endCursor)
                }
                if(items.edges != null){
                    items.edges.forEach { edge->
                        if (edge?.node != null) {
                            val newItem = Item().apply {
                                name = edge.node.onItem.name.toString()
                                description = edge.node.onItem.description.toString()
                                itemId = edge.node.onItem.id
                            }
                            itemList.add(newItem)
                        }
                    }
                }
                adapter.submitList(itemList)
            }
        }
    }

}