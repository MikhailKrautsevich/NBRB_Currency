package com.example.nbrbcurrency.retrofit.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false, name = "DailyExRates")
data class CurrencyDataList constructor(
    @field:Attribute(name = "Date")
    var date: String = "",
    @field:ElementList(inline = true)
    var currencies: List<CurrencyData> = ArrayList()
)
