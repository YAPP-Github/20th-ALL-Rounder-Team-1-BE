package com.yapp.weekand.graphql

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class WeekandResolver {
    private val defaultWeekand = Weekand("world")

    @DgsQuery
    fun weekand(@InputArgument customField : String?): Weekand {
        if(customField === null){
            return defaultWeekand;
        }
        return Weekand(customField);
    }

    data class Weekand(val hello: String)
}