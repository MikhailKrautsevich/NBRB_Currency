package com.example.nbrbcurrency

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.nbrbcurrency.interfaces.HostInterface
import com.example.nbrbcurrency.retrofit.models.CurrencyData
import com.example.nbrbcurrency.utils.SharedPreferenceHelper

class SettingsFragment : Fragment() {

    companion object {
        const val RUB_CHARCODE = "RUB"
        const val EUR_CHARCODE = "EUR"
        const val USD_CHARCODE = "USD"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var menu: Menu
    private lateinit var recycler: RecyclerView
    private lateinit var settingsHelper: SharedPreferenceHelper

    private var host: HostInterface? = null
    private lateinit var settingsList: List<CurrencySettingContainer>

    private val viewModel: CurrencyViewModel by lazy { ViewModelProvider(this).get(CurrencyViewModel::class.java) }
    private val currencies: List<CurrencyData>? = viewModel.getCurrenciesData().value?.get(0)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        settingsHelper = SharedPreferenceHelper(context)
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
        toolbar.setNavigationOnClickListener { host?.returnToCourses() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_settings, container, false)
        recycler = v.findViewById(R.id.settings_recycler)

        if (settingsHelper.checkFirstLaunch()) {
            settingsList = initDefaultList()
        } else settingsList = initNonDefaultList()

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

    private fun initDefaultList(): List<CurrencySettingContainer> {
        val list = ArrayList<CurrencySettingContainer>()
        val rub = viewModel.getCurrencyByCharCode(RUB_CHARCODE)
        val eur = viewModel.getCurrencyByCharCode(EUR_CHARCODE)
        val usd = viewModel.getCurrencyByCharCode(USD_CHARCODE)

        rub?.let { list.add(CurrencySettingContainer(rub.charCode, rub.scale, true)) }
        eur?.let { list.add(CurrencySettingContainer(eur.charCode, eur.scale, true)) }
        usd?.let { list.add(CurrencySettingContainer(usd.charCode, usd.scale, true)) }

        currencies?.get(0)?.let {
            for (currency in currencies) {
                if (currency.charCode != RUB_CHARCODE
                    || currency.charCode != EUR_CHARCODE
                    || currency.charCode != USD_CHARCODE
                ) {
                    list.add(CurrencySettingContainer(currency.charCode, currency.scale, false))
                }
            }
        }

        return list
    }

    private fun initNonDefaultList(): List<CurrencySettingContainer>  {
        val list = ArrayList<CurrencySettingContainer>()

        for (i in 0..26) {

        }
        return list
    }

    data class CurrencySettingContainer constructor(
        val charCode: String,
        val scale: String,
        var isChecked: Boolean
    )
}