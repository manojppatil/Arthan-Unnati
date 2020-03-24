package com.example.arthan.model

data class BankindDocUploadRequest(var bank: String,
                                   var name: String,
                                   var password: String,
                                   var file: String)