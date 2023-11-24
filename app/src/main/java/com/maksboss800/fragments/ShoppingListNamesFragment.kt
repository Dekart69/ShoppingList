package com.maksboss800.fragments



import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.maksboss800.activities.MainApp
import com.maksboss800.activities.ShopListActivity
import com.maksboss800.databinding.FragmentShoppingListNamesBinding
import com.maksboss800.db.MainViewModel
import com.maksboss800.db.ShopListNameAdapter
import com.maksboss800.dialogs.DeleteDialog
import com.maksboss800.dialogs.NewListDialog
import com.maksboss800.entities.ShopListNameItem
import com.maksboss800.utils.TimeManager

class ShoppingListNamesFragment : BaseFragment(),ShopListNameAdapter.Listener{
    private lateinit var binding:FragmentShoppingListNamesBinding
    private val mainViewModel:MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).dataBase)
    }
    private lateinit var adapter:ShopListNameAdapter
    private lateinit var defPref: SharedPreferences


    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity,object: NewListDialog.Listener{
            override fun onCLick(name: String) {
                val shopListName=ShopListNameItem(
                    null,
                    name,
                    TimeManager.getCurrentTime(),
                    0,
                    0,
                    ""
                )
                mainViewModel.insertShoppingListName(shopListName)
            }
        },"")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentShoppingListNamesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }
    private fun observer(){
        mainViewModel.allShopListsNames.observe(viewLifecycleOwner,{
            adapter.submitList(it)
        })
    }


    private fun initRcView()=with(binding){
        rcView.layoutManager=LinearLayoutManager(activity)
        defPref= activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }!!
        adapter=ShopListNameAdapter(this@ShoppingListNamesFragment, defPref)
        rcView.adapter=adapter
    }





    companion object {
        @JvmStatic
        fun newInstance() = ShoppingListNamesFragment()
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity,object:DeleteDialog.Listener{
            override fun onCLick() {
                mainViewModel.deleteShopList(id)
            }

        })

    }

    override fun editItem(shopListName: ShopListNameItem) {
        NewListDialog.showDialog(activity as AppCompatActivity,object: NewListDialog.Listener{
            override fun onCLick(name: String) {

                mainViewModel.updateShopListName(shopListName.copy(name=name))
            }
        },shopListName.name)
    }

    override fun onClickItem(shopListNamItem: ShopListNameItem) {
        val i= Intent(activity,ShopListActivity::class.java).apply{
            putExtra(ShopListActivity.SHOP_LIST_NAME,shopListNamItem)
        }
        startActivity(i)
    }

}