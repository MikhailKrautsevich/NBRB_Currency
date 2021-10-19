package com.example.nbrbcurrency

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.nbrbcurrency.interfaces.HostInterface

class SettingsFragment : Fragment() {
    private lateinit var toolbar: Toolbar
    private lateinit var menu: Menu
    private lateinit var recycler: RecyclerView

    private var host: HostInterface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HostInterface) host = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.settingsToolBar)

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                host?.returnToCourses()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_settings, container, false)

        return v
    }

    override fun onDetach() {
        super.onDetach()
        host = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.settings_menu, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.ok) {
            host?.returnToCourses()
        }
        return false
    }
}