package de.num42.sharing.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import de.num42.sharing.SharingApplication
import de.num42.sharing.apolloInstance
import de.num42.sharing.database.entities.Item as DBItem
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
    private val itemViewModel: ItemViewModel by viewModels {
        ItemViewModelFactory((application as SharingApplication).repository)
    }


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

        itemViewModel.allItems.observe(this) { items ->
            // Update the cached copy of the words in the adapter.
            items.let {
                val list=mutableListOf<Item>()
                for(item in items){
                    val transceivedItem = Item()
                    transceivedItem.apply {
                        itemId= item.itemId
                        description = item.description
                        name = item.name
                    }
                    list.add(transceivedItem)
                }
                adapter.submitList(list)
            }
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
                    Toast.makeText(applicationContext,"Session expired. Pleas log in again to access your data.",Toast.LENGTH_LONG).show()
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
                if(items.edges != null){
                    items.edges.forEach { edge->
                        if (edge?.node != null) {
                            val newItem = Item().apply {
                                name = edge.node.onItem.name.toString()
                                description = edge.node.onItem.description.toString()
                                itemId = edge.node.onItem.id
                            }
                            itemViewModel.insertItem(DBItem(newItem.name,newItem.description,newItem.itemId))
                            itemList.add(newItem)
                        }
                    }
                }
                //adapter.submitList(itemList)
                if(items.pageInfo.hasNextPage){
                    getItems(id,items.pageInfo.endCursor)
                }
            }
        }
    }

}