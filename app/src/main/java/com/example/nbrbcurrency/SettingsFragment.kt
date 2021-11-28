package com.example.nbrbcurrency

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nbrbcurrency.db.CurrencySettingContainer
import com.example.nbrbcurrency.interfaces.HostInterface

class SettingsFragment : Fragment() {

    companion object {
        const val LOG = "NBRBCOMMONLOG"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var menu: Menu
    private lateinit var recycler: RecyclerView

    private var host: HostInterface? = null
    private lateinit var settingsListLiveData: LiveData<List<CurrencySettingContainer>>

    private val viewModel: CurrencyViewModel by lazy {
        ViewModelProvider(host as ViewModelStoreOwner)
            .get(CurrencyViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HostInterface) host = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_settings, container, false)
        recycler = v.findViewById(R.id.settings_recycler)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.settingsToolBar)

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { host?.returnToCourses() }

        recycler.layoutManager = LinearLayoutManager(context)

        viewModel.getSettingsList()
        settingsListLiveData = viewModel.getSettings()
        settingsListLiveData.observe(viewLifecycleOwner, { t1 ->
                val adapter = SettingsAdapter(t1)
                recycler.adapter = adapter
        })

        val itemTouchHelperCallback : ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Log.d(LOG, "onMove")
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.d(LOG, "onSwiped")
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycler)
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
            val settings = (recycler.adapter as SettingsAdapter).getList()
            Log.d(
                LOG,
                "SettingsFragment: onOptionsItemSelected(): OK: settings.size = ${settings.size}"
            )
            viewModel.saveSettings(settings)
            host?.returnToCourses()
        }
        return false
    }

    private inner class SettingHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val charCode: TextView = itemView.findViewById(R.id.setCharCode)
        private val scaleName: TextView = itemView.findViewById(R.id.setScaleName)

        private val switchCompat: SwitchCompat = itemView.findViewById(R.id.switchCompat)
        private val touch: ImageView = itemView.findViewById(R.id.touchImage)

        private lateinit var setting: CurrencySettingContainer

        fun bind(_setting: CurrencySettingContainer) {
            setting = _setting

            charCode.text = setting.charCode
            scaleName.text = setting.scale
            switchCompat.isChecked = setting.isChecked
            switchCompat.setOnCheckedChangeListener { _, p1 -> setting.isChecked = p1 }
        }
    }

    private inner class SettingsAdapter(private val settings: List<CurrencySettingContainer>) :
        RecyclerView.Adapter<SettingHolder>(){

        init {
            Log.d(LOG, "SettingsAdapter: init(): settings.size = ${settings.size}")
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingHolder {
            val view = layoutInflater.inflate(R.layout.settings_item, parent, false)
            return SettingHolder(view)
        }

        override fun onBindViewHolder(holder: SettingHolder, position: Int) {
            holder.bind(settings[position])
        }

        override fun getItemCount(): Int = settings.size

        fun getList() = settings
    }
}