package com.maksboss800.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.maksboss800.R
import com.maksboss800.databinding.NewListDialogBinding

object NewListDialog {
    fun showDialog(context:Context,listener:Listener,name:String){
        var dialog:AlertDialog?=null
        val builder=AlertDialog.Builder(context)
        val binding=NewListDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply{
            edNewListName.setText(name)
            if(name.isNotEmpty()){
                bCreate.setText(context.getString(R.string.update))
                tvTitleTask.setText(context.getString(R.string.update_item))
            }
            bCreate.setOnClickListener {
                val listName=edNewListName.text.toString()
                if(listName.isNotEmpty()){
                    listener.onCLick(listName)
                }
                dialog?.dismiss()
            }
            dialog=builder.create()
            dialog!!.window?.setBackgroundDrawable(null)
            dialog!!.show()
        }

    }
    interface Listener{
        fun onCLick(name:String)
    }
}