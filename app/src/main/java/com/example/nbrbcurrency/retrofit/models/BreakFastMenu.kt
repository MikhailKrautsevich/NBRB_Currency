package com.example.nbrbcurrency.retrofit.models

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false, name = "breakfast_menu")
data class BreakFastMenu @JvmOverloads constructor(
    @field:ElementList(inline = true)
    var foodList : List<Food>
)
