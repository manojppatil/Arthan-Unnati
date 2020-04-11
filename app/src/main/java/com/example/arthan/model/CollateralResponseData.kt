package com.example.arthan.model

import com.example.arthan.lead.model.Data

data class CollateralResponseData(
    var errorDesc:String,
    var errorCode:String,
    var data:List<Data>
)
        data class ColateralSubData(
            var id:String,
            var value:String,
            var description:String,
            var impType:String
        )
