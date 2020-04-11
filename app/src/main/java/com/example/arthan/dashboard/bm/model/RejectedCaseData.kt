package com.example.arthan.dashboard.bm.model

class RejectedCaseData (val eid: String,
                        val cases: List<RejectedCaseResponse>)

data class RejectedCaseResponse(val caseId: String,
                                val cname: String,
                                val loanAmt: String,
                                val rejectReason: String,
                                val rejectDate: String,
                                val loginDate: String)
