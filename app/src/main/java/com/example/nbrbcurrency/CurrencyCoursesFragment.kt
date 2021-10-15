package com.example.nbrbcurrency

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nbrbcurrency.retrofit.CurrencyApi
import com.example.nbrbcurrency.retrofit.RetrofitHelper
import com.example.nbrbcurrency.retrofit.models.CurrencyData
import com.example.nbrbcurrency.retrofit.models.CurrencyDataList
import com.example.nbrbcurrency.utils.DateHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class CurrencyCoursesFragment : Fragment() {

    companion object {
        private const val LOG = "NBRB_LOG"
    }

    private lateinit var currentDateTextView: TextView
    private lateinit var tomorrowDateTextView: TextView
    private lateinit var problemTextView: TextView
    private lateinit var recycler: RecyclerView

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

        val date = Date()
        currentDateTextView.text = DateHelper.getDateForTextView(date)
        tomorrowDateTextView.text = DateHelper.getTomorrowDateForTextView(date)

        val currentDate = DateHelper.getDateForRetrofit(date)
        val tomorrowDate = DateHelper.getTomorrowDateForRetrofit(date)

        val retrofit: Retrofit = RetrofitHelper.getRetrofit()
        val api: CurrencyApi? = RetrofitHelper.getApi(retrofit)
        val call = api?.getCurrencyList(currentDate)

        call?.enqueue(object : Callback<CurrencyDataList> {
            override fun onResponse(
                call: Call<CurrencyDataList>,
                response: Response<CurrencyDataList>
            ) {
                Log.d(LOG, response.body()?.currencies?.size.toString())
                if (response.body() != null && response.body()!!.currencies != null) {
                    recycler.adapter = CurrencyAdapter(response.body()!!.currencies)

                    showRecycler()
                }
            }

            override fun onFailure(call: Call<CurrencyDataList>, t: Throwable) {
                Log.d(LOG, "" + t.localizedMessage)
                showProblemMessage()
            }
        })

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