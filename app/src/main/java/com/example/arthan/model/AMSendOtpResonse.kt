package com.example.arthan.model

data class AMSendOtpResponse(
    val amId:String,
    val onboarded:String,
    val validAM : String

//{"response":[{"destination":"AM","id":"0","segment":"0","status":"failed"}],"userId":"0005"}
// {"response":[{"destination":"919483938833","id":"0","mrid":"8007162047502195598","segment":"0","status":"success"}],"amId":"","onboarded":""}
) {

}
