package com.example.nbrbcurrency

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.nbrbcurrency.interfaces.HostInterface

class MainActivity : AppCompatActivity(), HostInterface {

    private val manager: FragmentManager by lazy { supportFragmentManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNoTitle()

        if (savedInstanceState == null) {
            manager.beginTransaction()
                .add(R.id.fragment_container, CurrencyRatesFragment())
                .commit()
        }
    }

    override fun showSettings() {
        manager.beginTransaction()
            .replace(R.id.fragment_container, SettingsFragment())
            .addToBackStack("tag")
            .commit()
    }

    private fun setNoTitle(){                                                                       // костыль
        val s = ""
        this.title = s
    }

    override fun returnToCourses() {
        manager.popBackStack()
    }
}
