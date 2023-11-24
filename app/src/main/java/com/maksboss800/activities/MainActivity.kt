package com.maksboss800.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.maksboss800.R
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.maksboss800.billing.BillingManager
import com.maksboss800.databinding.ActivityMainBinding
import com.maksboss800.dialogs.NewListDialog
import com.maksboss800.fragments.FragmentManager
import com.maksboss800.fragments.NoteFragment
import com.maksboss800.fragments.ShoppingListNamesFragment
import com.maksboss800.settings.SettingsActivity

class MainActivity() : AppCompatActivity(),NewListDialog.Listener, Parcelable {
    lateinit var binding:ActivityMainBinding
    private var currentMenuItemId=R.id.shop_list
    private lateinit var defPref:SharedPreferences
    private var currentTheme=""

    private var iAd:InterstitialAd?=null
    private var adShowCounter=0
    private var adShowCounterMax=10

    private lateinit var pref:SharedPreferences

    constructor(parcel: Parcel) : this() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        defPref=PreferenceManager.getDefaultSharedPreferences(this)
        currentTheme=defPref.getString("theme_key","blue").toString()
        setTheme(getSelectedTheme())

        super.onCreate(savedInstanceState)

        pref=getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)

        binding=ActivityMainBinding.inflate(layoutInflater)
        //перед setContentView щоб після настройок одразу змінилося
        setContentView(binding.root)
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        FragmentManager.setFragment(ShoppingListNamesFragment.newInstance(),this)
        setBottomNavListener()

        if(!pref.getBoolean(BillingManager.REMOVE_ADS_KEY,false)){
            loadInterAd()
        }

    }

    private fun loadInterAd(){
        val request=AdRequest.Builder().build()
        InterstitialAd.load(this,
            getString(R.string.inter_ad_id),
            request,object:InterstitialAdLoadCallback(){
                override fun onAdLoaded(ad: InterstitialAd) {
                    iAd=ad
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    iAd=null
                }
            })

    }

    private fun showInterAdd(adListener:AdListener){
        if(iAd!=null && adShowCounter>adShowCounterMax){
            iAd?.fullScreenContentCallback=object:FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    iAd = null
                    loadInterAd()
                    adListener.onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    iAd = null
                    loadInterAd()
                }

                override fun onAdShowedFullScreenContent() {
                    iAd = null
                    loadInterAd()
                }
            }
            adShowCounter=0
            iAd?.show(this)

        }else{
            adShowCounter++
            adListener.onFinish()

        }
    }



    private fun setBottomNavListener(){
        binding.bottomNV.setOnItemSelectedListener {
            when(it.itemId){
                R.id.settings->{
                    showInterAdd(object:AdListener{
                        override fun onFinish() {
                            startActivity(Intent(this@MainActivity,SettingsActivity::class.java))
                        }
                    })

                }
                R.id.notes->{
                    showInterAdd(object:AdListener{
                        override fun onFinish() {
                            currentMenuItemId=R.id.notes
                            FragmentManager.setFragment(NoteFragment.newInstance(),this@MainActivity)
                        }
                    })

                }
                R.id.shop_list->{
                    showInterAdd(object:AdListener{
                        override fun onFinish() {
                            currentMenuItemId=R.id.shop_list
                            FragmentManager.setFragment(ShoppingListNamesFragment.newInstance(),this@MainActivity)
                        }
                    })

                }
                R.id.new_item->{

                    showInterAdd(object:AdListener{
                        override fun onFinish() {
                            FragmentManager.currentFrag?.onClickNew()
                        }
                    })


                }
            }
            true
        }
    }

    private fun getSelectedTheme():Int{
        return if(defPref.getString("theme_key","blue")=="blue"){
            R.style.Base_Theme_ShoppingListBlue
        }else{
            R.style.Base_Theme_ShoppingLisRed
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNV.selectedItemId=currentMenuItemId

        if(defPref.getString("theme_key","blue")!=currentTheme)recreate()


    }



    override fun onCLick(name: String) {
        Log.d("MyLog","Name of list = $name")
    }

    //////////for dialog
    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }
    ////////////////////

    interface AdListener{
        fun onFinish()
    }
}