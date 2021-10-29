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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nbrbcurrency.db.CurrencySettingContainer
import com.example.nbrbcurrency.interfaces.HostInterface
import com.example.nbrbcurrency.retrofit.models.CurrencyData

class SettingsFragment : Fragment() {

    companion object {
        const val RUB_CHARCODE = "RUB"
        const val EUR_CHARCODE = "EUR"
        const val USD_CHARCODE = "USD"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var menu: Menu
    private lateinit var recycler: RecyclerView

    private var host: HostInterface? = null
    private lateinit var settingsList: List<CurrencySettingContainer>

    private val viewModel: CurrencyViewModel by lazy { ViewModelProvider(host as ViewModelStoreOwner)
        .get(CurrencyViewModel::class.java) }
    private var currencies: List<CurrencyData>? = ArrayList()

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
        currencies = viewModel.getCurrenciesData().value?.get(0)
        recycler = v.findViewById(R.id.settings_recycler)
        viewModel.getCurrenciesData()

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
        settingsList = getDefaultList()
        recycler.adapter = SettingsAdapter(settingsList)
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

    private fun getDefaultList(): List<CurrencySettingContainer> {
        val list = ArrayList<CurrencySettingContainer>()
        list.add(CurrencySettingContainer("aa", "111", true, 0))
        val rub = viewModel.getCurrencyByCharCode(RUB_CHARCODE)
        val eur = viewModel.getCurrencyByCharCode(EUR_CHARCODE)
        val usd = viewModel.getCurrencyByCharCode(USD_CHARCODE)

        rub.let { list.add(CurrencySettingContainer(rub.charCode, rub.scale, true, 1)) }
        eur.let { list.add(CurrencySettingContainer(eur.charCode, eur.scale, true, 2)) }
        usd.let { list.add(CurrencySettingContainer(usd.charCode, usd.scale, true, 3)) }

        currencies?.let {
            var i = 3
            for (currency in currencies!!) {
                if (currency.charCode != RUB_CHARCODE
                    || currency.charCode != EUR_CHARCODE
                    || currency.charCode != USD_CHARCODE
                ) {
                    i++
                    list.add(
                        CurrencySettingContainer(
                            currency.charCode,
                            currency.scale,
                            false,
                            i
                        )
                    )
                }
            }
        }
        return list
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
        }
    }

    private inner class SettingsAdapter(val settings : List<CurrencySettingContainer>)
        : RecyclerView.Adapter<SettingHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingHolder {
            val view = layoutInflater.inflate(R.layout.settings_item, parent, false)
            return SettingHolder(view)
        }

        override fun onBindViewHolder(holder: SettingHolder, position: Int) {
            holder.bind(settings[position])
        }

        override fun getItemCount(): Int = settings.size
    }
}