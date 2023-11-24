package com.maksboss800.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.maksboss800.entities.LibraryItem
import com.maksboss800.entities.NoteItem
import com.maksboss800.entities.ShopListItem
import com.maksboss800.entities.ShopListNameItem
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT*FROM note_list")
    fun getAllNotes(): Flow<List<NoteItem>>
    @Query("SELECT*FROM shopping_list_names")
    fun getAllShoppingListNames(): Flow<List<ShopListNameItem>>

    @Query("SELECT*FROM shop_List_Item WHERE listId LIKE:listClickedId")
    fun getAllShopListItems(listClickedId:Int): Flow<List<ShopListItem>>

    @Query("SELECT*FROM library WHERE name LIKE:Name")
    suspend fun getAllLibraryItems(Name:String): List<LibraryItem>

    @Query("DELETE FROM note_list WHERE id IS:id")
    suspend fun deleteNote(id:Int)
    @Query("DELETE FROM library WHERE id IS:id")
    suspend fun deleteLibraryItem(id:Int)
    @Query("DELETE FROM shopping_list_names WHERE id IS:id")
    suspend fun deleteShopListName(id:Int)
    @Insert
    suspend fun insertNote(note:NoteItem)
    @Insert
    suspend fun insertItem(shopListItem: ShopListItem)

    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)

    @Insert
    suspend fun insertShopListName(name:ShopListNameItem)
    @Update
    suspend fun updateNote(note:NoteItem)
    @Update
    suspend fun updateLibraryItem(item:LibraryItem)
    @Update
    suspend fun updateListName(shopListName:ShopListNameItem)

    @Update
    suspend fun updateListItem(shopListItem:ShopListItem)
    @Query("DELETE FROM shop_List_Item WHERE listId LIKE:listid")
    suspend fun deleteShopListItemsByListId(listid:Int)


}