package com.maksboss800.dialogs

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.maksboss800.R
import com.maksboss800.databinding.EditListItemDialogBinding
import com.maksboss800.databinding.NewListDialogBinding
import com.maksboss800.entities.ShopListItem

object EditListItemDialog {
    fun showDialog(context:Context,item:ShopListItem,listener:Listener){
        var dialog:AlertDialog?=null
        val builder=AlertDialog.Builder(context)
        val binding=EditListItemDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            edName.setText(item.name)
            edDescription.setText(item.itemInfo)
            if(item.itemType==1)edDescription.visibility= View.GONE

            bUpdate.setOnClickListener {
                if(edName.text.toString().isNotEmpty()) {
                    listener.onCLick(item.copy(
                        name = edName.text.toString(),
                        itemInfo = edDescription.text.toString()))
                }
                dialog?.dismiss()
            }
        }
            dialog=builder.create()
            dialog.window?.setBackgroundDrawable(null)
            dialog.show()
    }
    interface Listener{
        fun onCLick(item:ShopListItem)
    }

}