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
                           val reassign: Data,
                           val inProgress: Data,
                           val amCase: Data,
                           val myAm: Data,
                           val amName: String,
                           val amBranch: String,
                           val amRm: String,
                           val amRmContact: String,
                           val amApproved: String,
                           val amRmStatus: String,
                           val amRmImage: String
)

data class Data(val count: String,
                val total: String,
                val label: String)

