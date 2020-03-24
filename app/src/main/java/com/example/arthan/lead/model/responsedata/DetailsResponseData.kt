package com.example.arthan.lead.model.responsedata

import com.example.arthan.lead.model.Data

data class DetailsResponseData(
    val data: List<Data>,
    val errorCode: String,
    val errorDesc: String
)