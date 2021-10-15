package com.example.nbrbcurrency.retrofit.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Currency")
data class CurrencyData constructor(
    @field:Attribute(name = "Id")
    var id: String = "",
    @field:Element(name = "NumCode")
    var numCode: String = "",
    @field:Element(name = "CharCode")
    var charCode: String = "",
    @field:Element(name = "Scale")
    var scale: String = "",
    @field:Element(name = "Name")
    var name: String = "",
    @field:Element(name = "Rate")
    var rate: String = "",
)