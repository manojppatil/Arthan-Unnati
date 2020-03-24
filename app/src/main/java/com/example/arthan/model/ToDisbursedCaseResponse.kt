package com.example.arthan.model

data class ToDisbursedCaseResponse (val rmId: String,
                               val tobDisbList:List<ToDisbursedData>)

data class ToDisbursedData(val segment: String,
                         val docType: String,
                         val targetDate: String,
                         val disbursalDate: String,
                         val caseId: String,
                         val name: String,
                         val daysPassed: String)

