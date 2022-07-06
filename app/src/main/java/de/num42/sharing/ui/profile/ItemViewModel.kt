package de.num42.sharing.ui.profile

import androidx.lifecycle.*
import de.num42.sharing.database.Repository
import de.num42.sharing.database.entities.Item
import kotlinx.coroutines.launch

class ItemViewModel(private val repository: Repository) : ViewModel(){

    val allItems: LiveData<List<Item>> = repository.allItems.asLiveData()

    fun deleteAllItems() = viewModelScope.launch {
        repository.deleteAllItems()
    }

    fun insertItem(item: Item) = viewModelScope.launch{
        repository.insertItem(item)
    }

}

class ItemViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}