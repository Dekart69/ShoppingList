package com.maksboss800.activities

import android.app.Application
import com.maksboss800.db.MainDataBase

class MainApp: Application() {
    val dataBase by lazy{MainDataBase.getDataBase(this)}




}