package com.example.arthan.lead.model.responsedata

import com.example.arthan.dashboard.bm.model.BureauDetails

data class Customer360ResponseData(
    val assets: String?,
    val banking: Banking?,
    val bureau: BureauDetails?,
    val bureauData: BureauDetails?,
    val collateral: Collateral?,
    val finances: Finances?,
    val idAddress: IdAddress?,
    val pd: Pd?,
    val rcu: String?,
    val scoreCard: ScoreCard?
)