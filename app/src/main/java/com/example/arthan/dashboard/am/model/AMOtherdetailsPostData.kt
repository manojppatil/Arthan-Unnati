package com.example.arthan.dashboard.am.model

data class AMOtherdetailsPostData(
    val smartphone: String,
    val twoWheeler: String,
    val languages: List<Languages>,
    val references: List<References>,
    val amId: String = ""
)

data class Languages(
    val lang: String,
    val speak: String,
    val read: String,
    val write: String
)

data class References(
    val name: String,
    val mobNo: String,
    val address: String,
    val profession: String,
    val comments: String
)

