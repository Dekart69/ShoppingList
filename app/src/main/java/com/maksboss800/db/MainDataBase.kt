package com.maksboss800.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksboss800.entities.LibraryItem
import com.maksboss800.entities.NoteItem
import com.maksboss800.entities.ShopListItem
import com.maksboss800.entities.ShopListNameItem
import com.maksboss800.entities.TestItem

@Database(entities = [LibraryItem::class,NoteItem::class,
    ShopListItem::class,ShopListNameItem::class, TestItem::class],version=1
)
abstract class MainDataBase:RoomDatabase() {
    abstract fun getDao():Dao

    companion object{
        @Volatile
        private var ISTANCE:MainDataBase?=null

        fun getDataBase(context: Context):MainDataBase{
            return ISTANCE?:synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,MainDataBase::class.java,
                    "shopping_list.db").build()//контекст весь додаток
            instance
            }

        }
    }
}