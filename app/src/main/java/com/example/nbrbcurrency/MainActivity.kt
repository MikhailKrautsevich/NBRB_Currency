package com.example.nbrbcurrency

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager: FragmentManager = supportFragmentManager

        if (savedInstanceState == null) {
            manager.beginTransaction()
                .add(R.id.fragment_container, CurrencyCoursesFragment())
                .commit()
        }
    }
}
