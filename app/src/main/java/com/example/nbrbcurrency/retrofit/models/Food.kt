package com.example.nbrbcurrency.retrofit.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "food")
data class Food @JvmOverloads constructor(
    @field:Element(name = "name")
    var name:String,

    @field:Element(name = "price")
    var price:String,

    @field:Element(name = "description")
    var description:String,

    @field:Element(name = "calories")
    var calories:String
)