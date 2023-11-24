package com.maksboss800.db

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


import com.maksboss800.R
import com.maksboss800.databinding.ListNameItemBinding
import com.maksboss800.entities.ShopListItem
import com.maksboss800.entities.ShopListNameItem
import com.maksboss800.utils.TimeManager

class ShopListNameAdapter(private val listener:Listener,private val defPref: SharedPreferences): ListAdapter<ShopListNameItem, ShopListNameAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position),listener,defPref)
    }

    class ItemHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding=ListNameItemBinding.bind(view)

        fun setData(shopListNameItem:ShopListNameItem, listener:Listener,defPref: SharedPreferences)=with(binding){
            tvListName.text=shopListNameItem.name
            //tvTime.text=shopListNameItem.time
            tvTime.text= TimeManager.getTimeFormat(shopListNameItem.time,defPref)

            pBar.max=shopListNameItem.allItemCounter
            pBar.progress=shopListNameItem.checkedItemsCounter

            val colorState=ColorStateList.valueOf(getProgressColorState(shopListNameItem,binding.root.context))
            pBar.progressTintList=colorState
            counterCard.backgroundTintList=colorState

            val counterText="${shopListNameItem.checkedItemsCounter}/${shopListNameItem.allItemCounter}"
            tvCounter.text=counterText

            itemView.setOnClickListener {
                listener.onClickItem(shopListNameItem)
            }
            ibDelete.setOnClickListener {
                listener.deleteItem(shopListNameItem.id!!)
            }
            ibEdit.setOnClickListener {
                listener.editItem(shopListNameItem)
            }
        }

        private fun getProgressColorState(item:ShopListNameItem,context: Context):Int{
            return if(item.checkedItemsCounter==item.allItemCounter){
                ContextCompat.getColor(context,R.color.green_progress)
            }else{
                ContextCompat.getColor(context,R.color.red_progress)
            }

        }
        companion object{
            fun create(parent: ViewGroup):ItemHolder{

                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_name_item,parent,false))
            }
        }
    }

    class ItemComparator:DiffUtil.ItemCallback<ShopListNameItem>(){
        override fun areItemsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
            return oldItem==newItem
        }

    }

    interface Listener{
        fun deleteItem(id:Int)
        fun editItem(shopListName:ShopListNameItem)
        fun onClickItem(shopListName:ShopListNameItem)
    }



}