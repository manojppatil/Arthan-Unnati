package com.example.arthan.dashboard.am.model

data class ProfessionPostData(

    var educatiolevel: String = "",
    var grossannualIncome: String = "",
    var acNumber1: String = "",
    var acNumber2: String = "",
    var upiId: String = "",
    var checqueUrl: String = "",
    val profession: List<String>? = null,
    var amId: String = ""

)