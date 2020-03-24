package com.example.arthan.model

data class RMDashboardData(val rmId: String,
val rmName: String,
val rmBranch: String,
val branchRank: String,
val airRank: String,
val earning: String,
val since: String,
val rmImage: String,
val leads:Data,
val screening: Data,
val approved: Data,
val rejected: Data,
val toDisburse: Data,
val reassign: Data)

data class Data(val count: String,
val total: String,
val label: String)

