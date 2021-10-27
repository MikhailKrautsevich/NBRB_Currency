package com.example.nbrbcurrency

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nbrbcurrency.interfaces.HostInterface
import com.example.nbrbcurrency.retrofit.models.CurrencyData
import com.example.nbrbcurrency.utils.DateHelper
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*

class CurrencyCoursesFragment : Fragment() {

    companion object {
        private const val LOG = "NBRB_LOG"
    }

    private lateinit var currentDateTextView: TextView
    private lateinit var tomorrowDateTextView: TextView
    private lateinit var problemTextView: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var menu: Menu
    private lateinit var settings: MenuItem
    private lateinit var recycler: RecyclerView

    private var host: HostInterface? = null
    private var disposable: Disposable? = null

    private val viewModel: CurrencyViewModel by lazy { ViewModelProvider(this).get(CurrencyViewModel::class.java) }

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
        val v = inflater.inflate(R.layout.fragment_currency_courses, container, false)

        currentDateTextView = v.findViewById(R.id.current_date)
        tomorrowDateTextView = v.findViewById(R.id.tomorrow_date)
        problemTextView = v.findViewById(R.id.problem)
        recycler = v.findViewById(R.id.currency_recycler) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.setHasFixedSize(true)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolBar)

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }
        toolbar.setNavigationIcon(R.drawable.ic_back)
        setDate()

        val rates = viewModel.getCurrenciesData()
        rates.observe(viewLifecycleOwner,
            { t ->
                t?.let {
                    if (t.size == 2) {
                        showRecycler()
                        showSettingsMenuIcon()
                        recycler.adapter = CurrencyAdapter(it[0], it[1])
                    } else showProblemMessage()
                }
            })

//        val courses: Single<Array<List<CurrencyData>>>? = viewModel.getCurrencyData()
//        disposable = courses?.observeOn(AndroidSchedulers.mainThread())
//            ?.subscribeWith(object : DisposableSingleObserver<Array<List<CurrencyData>>>() {
//                override fun onSuccess(dataList: Array<List<CurrencyData>>) {
//                    if (dataList[0] != null && dataList[1] != null) {
//                        recycler.adapter = CurrencyAdapter(dataList[0], dataList[1])
//                        showRecycler()
//                        showSettingsMenuIcon()
//                    }
//                }
//
//                override fun onError(e: Throwable?) {
//                    showProblemMessage()
//                    if (e != null) {
//                        Log.d(LOG, "" + e.localizedMessage)
//                    }
//                }
//            })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun onDetach() {
        super.onDetach()
        host = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.currency_courses_menu, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            host?.showSettings()
        }
        return false
    }

    private fun setDate() {
        val date = Date()
        currentDateTextView.text = DateHelper.getDateForTextView(date)
        tomorrowDateTextView.text = DateHelper.getTomorrowDateForTextView(date)
    }

    private fun showRecycler() {
        recycler.visibility = View.VISIBLE
        problemTextView.visibility = View.INVISIBLE
    }

    private fun showProblemMessage() {
        recycler.visibility = View.INVISIBLE
        problemTextView.visibility = View.VISIBLE
    }

    private fun showSettingsMenuIcon() {
        settings = menu.findItem(R.id.settings)
        settings.isVisible = true
    }

    private inner class CurrencyHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val charCode: TextView = itemView.findViewById(R.id.charCode)
        private val scaleName: TextView = itemView.findViewById(R.id.scale_name)
        private val currentCourse: TextView = itemView.findViewById(R.id.current_course)
        private val tomorrowCourse: TextView = itemView.findViewById(R.id.tomorrow_course)

        private lateinit var currencyToday: CurrencyData
        private lateinit var currencyTomorrow: CurrencyData

        fun bind(currencyToday: CurrencyData, currencyTomorrow: CurrencyData) {
            this.currencyToday = currencyToday
            this.currencyTomorrow = currencyTomorrow
            charCode.text = this.currencyToday.charCode
            scaleName.text =
                String.format("%s %s", this.currencyToday.scale, this.currencyToday.name)
            currentCourse.text = this.currencyToday.rate
            tomorrowCourse.text = this.currencyTomorrow.rate
        }
    }

    private inner class CurrencyAdapter(
        var currenciesToday: List<CurrencyData>,
        var currenciesTomorrow: List<CurrencyData>
    ) :
        RecyclerView.Adapter<CurrencyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
            val view = layoutInflater.inflate(R.layout.currency_item, parent, false)
            return CurrencyHolder(view)
        }

        override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
            val currencyToday = currenciesToday[position]
            val currencyTomorrow = currenciesTomorrow[position]
            holder.bind(currencyToday, currencyTomorrow)
        }

        override fun getItemCount() = currenciesToday.size

    }
}
