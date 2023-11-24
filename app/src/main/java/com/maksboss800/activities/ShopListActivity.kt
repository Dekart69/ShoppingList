package com.maksboss800.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.maksboss800.R
import com.maksboss800.databinding.ActivityShopListBinding
import com.maksboss800.db.MainViewModel
import com.maksboss800.db.ShopListItemAdapter
import com.maksboss800.dialogs.EditListItemDialog
import com.maksboss800.entities.LibraryItem
import com.maksboss800.entities.ShopListItem
import com.maksboss800.entities.ShopListNameItem
import com.maksboss800.utils.ShareHelper

//for onBackPressed
import androidx.activity.OnBackPressedCallback
import androidx.activity.ComponentActivity
import androidx.preference.PreferenceManager

class ShopListActivity : AppCompatActivity(),ShopListItemAdapter.Listener {
    private lateinit var binding:ActivityShopListBinding
    private var shopListNameItem:ShopListNameItem?=null
    private lateinit var saveItem:MenuItem
    private var edItem:EditText?=null
    private var adapter:ShopListItemAdapter?=null
    private lateinit var textWatcher:TextWatcher

    private val mainViewModel:MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).dataBase)
    }
    private lateinit var defPref: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        defPref= PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())

        super.onCreate(savedInstanceState)
        binding=ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar3
        setSupportActionBar(toolbar)

        init()

        initRcView()
        listItemObserver()

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveItemCount()

                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }

    private fun init(){
        shopListNameItem=intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem

    }
    private fun initRcView()=with(binding){
        rcView.layoutManager=LinearLayoutManager(this@ShopListActivity)
        adapter=ShopListItemAdapter(this@ShopListActivity)
        rcView.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu,menu)

        saveItem=menu?.findItem(R.id.save_item)!!
        val newItem=menu.findItem(R.id.new_item)
        edItem=newItem.actionView?.findViewById(R.id.edNewShopItem) as EditText
        newItem.setOnActionExpandListener(expandOptionView())
        saveItem.isVisible=false
        textWatcher=initTextWatcher()

        return true
    }
    private fun initTextWatcher():TextWatcher{
        return object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("MyLog","On text changed: $p0")
                mainViewModel.getAllLibraryItems("%$p0%")

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_item -> {
                addNewShopItem(edItem?.text.toString())
            }
            R.id.delete_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!)
                finish()
            }
            R.id.clear_list -> {
                mainViewModel.clearShopList(shopListNameItem?.id!!)
            }
            R.id.share_list -> {
                startActivity(
                    Intent.createChooser(
                        ShareHelper.shareShopList(adapter?.currentList!!,shopListNameItem?.name!!),
                            "Share by"
                    ))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(name:String){
        if(name.isEmpty())return
        val item=ShopListItem(
            null,
            name,
            "",
            false,
            shopListNameItem?.id!!,
            0
        )
        edItem?.setText("")
        mainViewModel.insertShopItem(item)

    }

    private fun listItemObserver(){
        mainViewModel.getAllItemsFromList(shopListNameItem?.id!!)
            .observe(this,{
                adapter?.submitList(it)
                binding.tvEmpty.visibility=if(it.isEmpty()){
                    View.VISIBLE
                }else{
                    View.GONE
                }
            })
    }

    private fun libraryItemObserver(){
        mainViewModel.libraryItems.observe(this){
            val tempShopList=ArrayList<ShopListItem>()
            it.forEach {item->
                val shopItem=ShopListItem(
                    item.id,
                    item.name,
                    "",
                    true,
                    0,
                    1
                )
                tempShopList.add(shopItem)
            }
            adapter?.submitList(tempShopList)
            binding.tvEmpty.visibility=if(it.isEmpty()){
                View.VISIBLE
            }else{
                View.GONE
            }
        }

    }

    private fun expandOptionView():MenuItem.OnActionExpandListener{

        return object:MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                saveItem.isVisible=true
                edItem?.addTextChangedListener(textWatcher)
                mainViewModel.getAllLibraryItems("%%")
                libraryItemObserver()
                mainViewModel.getAllItemsFromList(shopListNameItem?.id!!)
                    .removeObservers(this@ShopListActivity)
                //mainViewModel.getAllLibraryItems("%%")
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                //saveItem.isVisible=false
                edItem?.removeTextChangedListener(textWatcher)
                invalidateMenu()
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
                //edItem?.setText("")
                listItemObserver()
                return true
            }

        }

    }

    companion object{
        const val SHOP_LIST_NAME="shop_list_name"
    }

    override fun onClickItem(shopListItem: ShopListItem,state:Int) {
        when(state){
            ShopListItemAdapter.CHECKED_BOX->mainViewModel.updateListItem(shopListItem)
            ShopListItemAdapter.EDIT->editListItem(shopListItem)
            ShopListItemAdapter.EDIT_LIBRARY_ITEM->editLibraryItem((shopListItem))
            ShopListItemAdapter.DELETE_LIBRARY_ITEM->{
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
            ShopListItemAdapter.ADD->addNewShopItem(shopListItem.name)
        }
    }

    private fun editListItem(item:ShopListItem){
        EditListItemDialog.showDialog(this,item,object:EditListItemDialog.Listener{
            override fun onCLick(item: ShopListItem) {
                mainViewModel.updateListItem(item)
            }
        })
    }

    private fun editLibraryItem(item:ShopListItem){
        EditListItemDialog.showDialog(this,item,object:EditListItemDialog.Listener{
            override fun onCLick(item: ShopListItem) {
                mainViewModel.updateLibraryItem(LibraryItem(item.id,item.name))
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
        })
    }

    private fun saveItemCount(){
        var checkedItemCounter=0
        adapter?.currentList?.forEach {
            if(it.itemChecked)checkedItemCounter++
        }
        val tempShopListNameItem=shopListNameItem?.copy(
            allItemCounter = adapter?.itemCount!!,
            checkedItemsCounter = checkedItemCounter,
        )
        mainViewModel.updateShopListName(tempShopListNameItem!!)
    }

    private fun getSelectedTheme():Int{
        return if(defPref.getString("theme_key","blue")=="blue"){
            R.style.Base_Theme_ShoppingListBlue
        }else{
            R.style.Base_Theme_ShoppingLisRed
        }
    }




}