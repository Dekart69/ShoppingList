package com.maksboss800.utils

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TimeManager {
    const val DEF_TIME_FORMAT="hh:mm:ss - yyyy/MM/dd"
    fun getCurrentTime():String{
        val formater= SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault())
        return formater.format(Calendar.getInstance().time)
    }
    fun getTimeFormat(time:String,defPrefernces:SharedPreferences):String{
        val deffaultFormater= SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault())
        val deffaultDate=deffaultFormater.parse(time)
        val newFormat=defPrefernces.getString("time_format_key", DEF_TIME_FORMAT)
        val newFormater= SimpleDateFormat(newFormat, Locale.getDefault())

        return if(deffaultDate!=null){
            newFormater.format(deffaultDate)
        }else{
            time
        }
    }

}