package com.example.nbrbcurrency

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nbrbcurrency.retrofit.models.CurrencyData
import com.example.nbrbcurrency.retrofit.models.CurrencyDataList
import com.example.nbrbcurrency.utils.DateHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import java.util.*

class CurrencyCoursesFragment : Fragment() {

    companion object {
        private const val LOG = "NBRB_LOG"
    }

    private lateinit var currentDateTextView: TextView
    private lateinit var tomorrowDateTextView: TextView
    private lateinit var problemTextView: TextView
    private lateinit var recycler: RecyclerView

    private var disposable: Disposable? = null

    private val viewModel: CurrencyViewModel by lazy { ViewModelProvider(this).get(CurrencyViewModel::class.java) }

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
        setDate()

        val courses : Single<CurrencyDataList>? = viewModel.getCurrencyData()
        disposable = courses?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeWith(object : DisposableSingleObserver<CurrencyDataList>(){
                override fun onSuccess(dataList: CurrencyDataList?) {
                    if (dataList != null) {
                        recycler.adapter = CurrencyAdapter(dataList.currencies)
                        showRecycler()
                    }
                }

                override fun onError(e: Throwable?) {
                    showProblemMessage()
                    if (e != null) {
                        Log.d(LOG, "" + e.localizedMessage)
                    }
                }
            })

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    private fun setDate(){
        val date = Date()
        currentDateTextView.text = DateHelper.getDateForTextView(date)
        tomorrowDateTextView.text = DateHelper.getTomorrowDateForTextView(date)
    }

    private fun showRecycler(){
        recycler.visibility = View.VISIBLE
        problemTextView.visibility = View.INVISIBLE
    }

    private fun showProblemMessage(){
        recycler.visibility = View.INVISIBLE
        problemTextView.visibility = View.VISIBLE
    }


    private inner class CurrencyHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val charCode: TextView = itemView.findViewById(R.id.charCode)
        private val scaleName: TextView = itemView.findViewById(R.id.scale_name)
        private val currentCourse: TextView = itemView.findViewById(R.id.current_course)
        private val tomorrowCourse: TextView = itemView.findViewById(R.id.tomorrow_course)

        private lateinit var currency: CurrencyData

        fun bind(currency: CurrencyData) {
            this.currency = currency
            charCode.text = this.currency.charCode
            scaleName.text = String.format("%s %s", this.currency.scale, this.currency.name)
            currentCourse.text = this.currency.rate

        }
    }

    private inner class CurrencyAdapter(var currencies: List<CurrencyData>) :
        RecyclerView.Adapter<CurrencyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
            val view = layoutInflater.inflate(R.layout.currency_item, parent, false)
            return CurrencyHolder(view)
        }

        override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
            val currency = currencies[position]
            holder.bind(currency)
        }

        override fun getItemCount() = currencies.size

    }
}