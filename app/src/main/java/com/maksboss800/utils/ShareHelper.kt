package com.maksboss800.utils

import android.content.Intent
import com.maksboss800.entities.ShopListItem

object ShareHelper {
    fun shareShopList(shopList:List<ShopListItem>,listName:String): Intent {
        val intent=Intent(Intent.ACTION_SEND)
        intent.type="text/plain"
        intent.apply{
            putExtra(Intent.EXTRA_TEXT, makeShareText(shopList,listName))
        }
        return intent
    }
    private fun makeShareText(shopList:List<ShopListItem>,listName:String):String{
        val sBuilder=StringBuilder()
        sBuilder.append("<<$listName>>\n")
        var counter=1
        shopList.forEach {
            sBuilder.append("${counter++} - ${it.name}  (${it.itemInfo})\n")
        }
        return sBuilder.toString()
    }
}