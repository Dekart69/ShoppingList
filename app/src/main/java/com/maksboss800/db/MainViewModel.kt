package com.maksboss800.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.maksboss800.entities.LibraryItem
import com.maksboss800.entities.NoteItem
import com.maksboss800.entities.ShopListItem
import com.maksboss800.entities.ShopListNameItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainViewModel(database:MainDataBase): ViewModel() {
    val dao=database.getDao()
    val libraryItems= MutableLiveData<List<LibraryItem>>()
    val allNotes:LiveData<List<NoteItem>> =dao.getAllNotes().asLiveData()
    val allShopListsNames:LiveData<List<ShopListNameItem>> =dao.getAllShoppingListNames().asLiveData()

    fun getAllItemsFromList(listId:Int):LiveData<List<ShopListItem>>{
        return dao.getAllShopListItems(listId).asLiveData()
    }
    fun getAllLibraryItems(partOfWord:String)=viewModelScope.launch{
        libraryItems.postValue(dao.getAllLibraryItems(partOfWord))
    }
    fun insertNote(note:NoteItem)=viewModelScope.launch{
        dao.insertNote(note)
    }
    fun insertShoppingListName(listname:ShopListNameItem)=viewModelScope.launch{
        dao.insertShopListName(listname)
    }
    fun insertShopItem(shopListItem: ShopListItem)=viewModelScope.launch{
        dao.insertItem(shopListItem)
        if(!isLibraryItemExists(shopListItem.name))dao.insertLibraryItem(LibraryItem(null,(shopListItem.name)))

    }
    fun updateListItem(item:ShopListItem)=viewModelScope.launch{
        dao.updateListItem(item)

    }

    fun updateNote(note:NoteItem)=viewModelScope.launch{
        dao.updateNote(note)
    }
    fun updateLibraryItem(item:LibraryItem)=viewModelScope.launch{
        dao.updateLibraryItem(item)
    }
    fun updateShopListName(shopListName:ShopListNameItem)=viewModelScope.launch{
        dao.updateListName(shopListName)
    }
    fun deleteNote(id:Int)=viewModelScope.launch{
        dao.deleteNote(id)
    }
    fun deleteLibraryItem(id:Int)=viewModelScope.launch{
        dao.deleteLibraryItem(id)
    }
    fun deleteShopList(id:Int)=viewModelScope.launch{
        dao.deleteShopListName(id)
        dao.deleteShopListItemsByListId(id)
    }
    fun clearShopList(id:Int)=viewModelScope.launch{
        dao.deleteShopListItemsByListId(id)
    }

    private suspend fun isLibraryItemExists(name:String):Boolean{

        return dao.getAllLibraryItems(name).isNotEmpty()
    }

    class MainViewModelFactory( val dataBase:MainDataBase): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(dataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}