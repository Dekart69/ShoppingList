package com.maksboss800.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.maksboss800.R
import com.maksboss800.databinding.ActivityNewNoteBinding
import com.maksboss800.entities.NoteItem
import com.maksboss800.fragments.NoteFragment
import com.maksboss800.utils.HtmlManager
import com.maksboss800.utils.MyTouchListener
import com.maksboss800.utils.TimeManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding:ActivityNewNoteBinding
    private var note:NoteItem?=null
    private var pref:SharedPreferences?=null
    private lateinit var defPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref= PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())

        super.onCreate(savedInstanceState)
        binding=ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBarSettings()
        getNote()
        initColorListener()
        setTextSize()
        onClickColorPicker()
        actionMenuCallBack()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initColorListener(){
        binding.colorPicker.setOnTouchListener(MyTouchListener())
        pref=PreferenceManager.getDefaultSharedPreferences(this)
    }
    private fun onClickColorPicker()=with(binding){
        ibRed.setOnClickListener {
            setColorForSelectedText(R.color.picker_red)
        }
        ibGreen.setOnClickListener {
            setColorForSelectedText(R.color.picker_green)
        }
        ibYellow.setOnClickListener {
            setColorForSelectedText(R.color.picker_yellow)
        }
        ibOrange.setOnClickListener {
            setColorForSelectedText(R.color.picker_orange)
        }
        ibBlue.setOnClickListener {
            setColorForSelectedText(R.color.picker_blue)
        }
        ibBrown.setOnClickListener {
            setColorForSelectedText(R.color.picker_brown)
        }
        ibBlack.setOnClickListener {
            setColorForSelectedText(R.color.picker_black)
        }
        ibPurple.setOnClickListener {
            setColorForSelectedText(R.color.picker_purple)
        }
    }


    private fun setMainResult(){
        var editState="new"
        val tempNote:NoteItem? = if(note==null){
            createNewNote()
        }else{
            editState="update"
            updateNote()
        }
        val i= Intent().apply{
            putExtra(NoteFragment.NEW_NOTE_KEY,tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY,editState)
        }
        setResult(RESULT_OK,i)
        finish()
    }

    private fun updateNote():NoteItem?=with(binding){
        return note?.copy(
            title=edTitle.text.toString(),
            content=HtmlManager.toHtml(edDescription.text)
        )
    }

    private fun getNote(){
        val serNote=intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if(serNote!=null){
            note=serNote as NoteItem
            fillNote()
        }
    }
    private fun fillNote()=with(binding){
            edTitle.setText(note?.title)
            edDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.id_save){
            setMainResult()
        }else if(item.itemId==android.R.id.home){
            finish()
        }else if(item.itemId==R.id.id_bold){
           setBoldForSelectedText()
        }else if(item.itemId==R.id.id_color){
            if(binding.colorPicker.isShown){
                closeColorPicker()
            }else{
                openColorPicker()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoldForSelectedText()=with(binding) {
        val startPos=edDescription.selectionStart
        val endPos=edDescription.selectionEnd

        val styles=edDescription.text.getSpans(startPos,endPos, StyleSpan::class.java)

        var boldStyle:StyleSpan?=null
        if(styles.isNotEmpty()){
            edDescription.text.removeSpan(styles[0])
        }else{
            boldStyle=StyleSpan(Typeface.BOLD)
        }
        edDescription.text.setSpan(boldStyle,startPos,endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        //edDescription.text.trim()
        edDescription.setSelection(startPos)
    }
    private fun setColorForSelectedText(colorId:Int)=with(binding) {
        val startPos=edDescription.selectionStart
        val endPos=edDescription.selectionEnd

        val styles=edDescription.text.getSpans(startPos,endPos, ForegroundColorSpan::class.java)

        if(styles.isNotEmpty())edDescription.text.removeSpan(styles[0])

        edDescription.text.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this@NewNoteActivity,colorId)),
            startPos,endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        //edDescription.text.trim()
        edDescription.setSelection(startPos)
    }

    private fun createNewNote(): NoteItem {
        return NoteItem(
            null,
            binding.edTitle.text.toString(),
            HtmlManager.toHtml(binding.edDescription.text),
            TimeManager.getCurrentTime(),
            "",
            )
    }



    private fun actionBarSettings(){
        val toolbar: Toolbar = binding.toolbar2
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openColorPicker(){
        binding.colorPicker.visibility= View.VISIBLE
        val openAnim=AnimationUtils.loadAnimation(this,R.anim.open_color_picker)
        binding.colorPicker.startAnimation(openAnim)
    }
    private fun closeColorPicker(){

        val openAnim=AnimationUtils.loadAnimation(this,R.anim.close_color_picker)
        openAnim.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                binding.colorPicker.visibility= View.GONE
            }
            override fun onAnimationRepeat(p0: Animation?) {}
        })
        binding.colorPicker.startAnimation(openAnim)
    }

    private fun actionMenuCallBack(){
        val actionCallBack=object: ActionMode.Callback{
            override fun onCreateActionMode(p0: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onPrepareActionMode(p0: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
                return true
            }

            override fun onDestroyActionMode(p0: ActionMode?) {

            }

        }
        //передаємо сюди свій власний щоб при виділенні слова не зявлялось меню
        binding.edDescription.customSelectionActionModeCallback=actionCallBack
    }
    //Extension function for edittext
    private fun EditText.setTextSize(size:String?){
        if(size!=null){
            this.textSize=size.toFloat()
        }
    }

    private fun setTextSize()=with(binding){
        Log.d("MyLog","Size = ${pref?.getString("title_size_key","16")}")
        edTitle.setTextSize(pref?.getString("title_size_key","16"))
        edDescription.setTextSize(pref?.getString("content_size_key","13"))

    }

    private fun getSelectedTheme():Int{
        return if(defPref?.getString("theme_key","blue")=="blue"){
            R.style.Theme_NewNoteBlue
        }else{
            R.style.Theme_NewNoteRed
        }
    }
}