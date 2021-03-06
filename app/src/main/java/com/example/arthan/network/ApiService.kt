package com.example.arthan.network

import com.example.arthan.dashboard.am.model.AMOtherdetailsPostData
import com.example.arthan.dashboard.am.model.AMPersonalDetailsData
import com.example.arthan.dashboard.am.model.ProfessionPostData
import com.example.arthan.dashboard.bm.model.*
import com.example.arthan.dashboard.bm.model.RejectedCaseData
import com.example.arthan.lead.model.CheckRLTStatusResponse
import com.example.arthan.lead.model.postdata.*
import com.example.arthan.lead.model.responsedata.*
import com.example.arthan.liveness.VerifyCardResponse
import com.example.arthan.liveness.VerifyVoterCardResponse
import com.example.arthan.model.*
import com.example.arthan.model.RejectedCaseResponse
import com.example.arthan.ocr.CardResponse
import com.example.arthan.ocr.OcrRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("rest/GetBureau")
    suspend fun getBureau(
        @Field("name") name: String,
        @Field("panId") panId: String,
        @Field("address") address: String
    ): Response<BureauDetails>?

    @FormUrlEncoded
    @POST("rest/GetNetBankingReport")
    suspend fun getNetBankingReport(@Field("mobNo") mobileNo: String): Response<ResponseBody>?

    @POST("rest/GetReport")
    suspend fun getReport(@Body body: Map<String, String>): Response<StatementReportResponseData>?

    @Multipart
    @POST("rest/file/upload")
    suspend fun uploadStatement(
        @Part file: MultipartBody.Part,
        @Part("loanId") loanId: String?,
        @Part("customerId") customerId: String?
    ): UploadStatementResponse?

    @GET("rest/GetMstr/loanPurpose")
    suspend fun getPurposeOfLoan(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/title")
    suspend fun getTitle(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/propJurisdiction")
    suspend fun getPropertyJurisdiction(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/propType")
    suspend fun getPropertyType(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/propNature")
    suspend fun getNatureOfProperty(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/nationality")
    suspend fun getNationality(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/education")
    suspend fun getEducation(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/occupationType")
    suspend fun getOccupationType(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/occupation")
    suspend fun getOccupationName(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/incomeSource")
    suspend fun getIncomeSource(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/industrySegment")
    suspend fun getIndustrySegment(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/businessActivity/{industry_type}")
    suspend fun getBusinessActivity(@Path("industry_type") industryType: String): Response<DetailsResponseData>?

    @GET("rest/GetMstr/industry")
    suspend fun getIndustryType(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/constitution")
    suspend fun getConstitution(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/Collateralnature")
    suspend fun getCollateralNature(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/Collateralnature")
    suspend fun getCollateral(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/grossAnnualInc")
    suspend fun getGrossAnnualIncome(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/rshipWithApplicant")
    suspend fun getRelationshipWithApplicant(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/relationship")
    suspend fun getRelationship(): Response<DetailsResponseData>?

    @GET("getCollateralOwnership ")
    suspend fun getCollateralOwnership(): Response<DetailsResponseData>?

    //Done
    @POST("saveLead")
    suspend fun saveLead(@Body body: LeadPostData?): Response<LeadResponseData>?

    //Done
    @POST("saveLoanDetails")
    suspend fun saveLoanDetail(@Body body: LoanPostData?): Response<LoanResponseData>?

    //Done
    @POST("savePersonalDetails")
    suspend fun savePersonalDetail(@Body body: PersonalPostData?): Response<PersonalResponseData>?

    @POST("saveKycDetails")
    suspend fun saveKycDetail(@Body body: HashMap<String,String>?): Response<BaseResponseData>?

    @POST("updateKYCDocs")
    suspend fun updateKYCDocs(@Body body: HashMap<String,String>?): Response<BaseResponseData>?


    @POST("saveBusinessDetails")
    suspend fun saveBusinessDetail(@Body body: BusinessDetailsPostData?): Response<BusinessDetailsResponseData>?

    @POST("rmResubmitBusiness")
    suspend fun rmResubmitBusiness(@Body body: BusinessDetailsPostData?): Response<BusinessDetailsResponseData>?

    @POST("saveIncomeDetails")
    suspend fun saveIncomeDetail(@Body body: IncomeDetailsPostData?): Response<BaseResponseData>?

    @POST("rmResubmitIncome")
    suspend fun rmResubmitIncome(@Body body: IncomeDetailsPostData?): Response<BaseResponseData>?

    @POST("rmReSubmit")
    suspend fun rmReSubmit(@Body body: HashMap<String,String>?): Response<BaseResponseData>?

    @POST("saveTradeReference")
    suspend fun saveTradeReference(@Body body: TradeReferencePostData?): Response<BaseResponseData>?

    @POST("saveCollateralDetails")
    suspend fun saveCollateralDetail(@Body body: CollateralDetailsPostData?): Response<BaseResponseData>?

    @GET("rest/GetMstr/getBMQueue/{bm_type}")
    suspend fun getBMQueue(@Path("bm_type") bmType: String?): Response<BMQueueResponseData>?

    @GET("rest/GetMstr/getBCMQueue/{bm_type}")
    suspend fun getBCMQueue(@Path("bm_type") bmType: String?): Response<BMQueueResponseData>?

    @POST("pd1")
    suspend fun savePD1(@Body body: PD1PostData?): Response<ResponseBody>?

    @POST("submitAssets")
    suspend fun submitAssets(@Body body: PD4Data?): Response<BaseResponseData>?

    @POST("pd23")
    suspend fun savePD23(@Body body: PD23PostData?): Response<BaseResponseData>?

    @POST("cust360")
    suspend fun saveCustomer360(@Body body: Map<String, String>?): Response<Customer360ResponseData>?

    @POST("rest/GSTReport")
    suspend fun saveGSTDetail(@Body body: GSTReportPostData?): Response<BaseResponseData>?

    @GET("getBMDocnData")
    suspend fun getBMDocumentAndData(@Query("loanId") loanId: String?): Response<CustomerDocumentAndDataResponseData>?

    @GET("getBMAmDocnData")
    suspend fun getBMAmDocnData(@Query("amId") amId: String?): Response<BMAmDocnDataResponse>?

    @GET("sendAMNotification")
    suspend fun reSendAMLink(@Query("amId") amId: String?): Response<BaseResponseData>?

    @POST("saveGST")
    suspend fun saveGST(@Body body: GSTPostData?): Response<BaseResponseData>?

    @POST("saveBills")
    suspend fun saveBills(@Body body: BillsPostData?): Response<BaseResponseData>?

    @POST("saveObligations")
    suspend fun saveObligations(@Body body: ObligationPostData?): Response<BaseResponseData>?

    @POST("saveBanking")
    suspend fun saveBanking(@Body body: BankingInputPostData?): Response<BaseResponseData>?

    @GET("retriveCustDetails")
    suspend fun getCustomer360Details(@Query("loanId") loanId: String?): Response<Cust360ResponseData>?

    @POST("rest/ocrPan")
    suspend fun getPANCardInfo(@Body ocrRequest: OcrRequest): Response<CardResponse>

//     @POST("rest/verifyKYCDocs")
     @POST("verifyKYCDocs")
    suspend fun getVerifyKYCDocs(@Body body: HashMap<String,String>): Response<CardResponse>

    @POST("rest/ocrAdhrBack")
    suspend fun getAadharCardInfo(@Body ocrRequest: OcrRequest): Response<CardResponse>

    @POST("rest/ocrVoter")
    suspend fun getVoterCardInfo(@Body ocrRequest: OcrRequest): Response<CardResponse>

    @POST("rest/verifyACPan")
    suspend fun verifyPANCardInfo(@Body body: Map<String, String?>): Response<VerifyCardResponse>

    @POST("rest/verifyACPan")
    suspend fun verifyAAdharCardInfo(@Body pan: String?): Response<VerifyCardResponse>

    @POST("rest/verifyACPan")
    suspend fun verifyVoterIdCardInfo(@Body pan: String?): Response<VerifyVoterCardResponse>

    @POST("rmHome")
    suspend fun getRMDashboardData(@Body request: RMDashboardRequest): Response<RMDashboardData>

    @POST("rmScreeningList")
    suspend fun getScreeningList(@Body request: RMDashboardRequest): Response<ScreeningListResponse>

    @GET("getAMCases")
    suspend fun getAMCases(@Query("rmId") rmID: String): Response<ReAssignLeadListResponse>

    @POST("rmLeadList")
    suspend fun getLeadList(@Body request: RMDashboardRequest): Response<RMLeadListResponse>

    @POST("rmReassignList")
    suspend fun getReassignList(@Body request: RMDashboardRequest): Response<ReAssignLeadListResponse>

    @GET("getRMReassigned")
    suspend fun getRMReassigned(@Query("rmId") rmID: String): Response<ReAssignLeadListResponse>

    @POST("fetchApprovedList")
    suspend fun getApprovedList(@Body request: RMDashboardRequest): Response<RMApprovedCaseResponse>

    @POST("rejected")
    suspend fun getRejectedCases(@Body request: RMDashboardRequest): Response<RejectedCaseResponse>

    @POST("toDisburse")
    suspend fun getToDisbursedCases(@Body request: RMDashboardRequest): Response<ToDisbursedCaseResponse>

    @POST("rmReview")
    suspend fun getRMReviewList(@Body request: RMDashboardRequest): Response<RMReviewReponse>

    @POST("verifyOTP")
    suspend fun verifyOTP(@Body request: VerifyOTPRequest): Response<VerifyOTPResponse>

    @POST("verifyOTPAppFee")
    suspend fun verifyOTPAppFee(@Body request: VerifyOTPRequest): Response<VerifyOTPResponse>

    @POST("updateScreen")
    suspend fun updateEligibilityAndPaymentInitiate(@Body request: UpdateEligibilityAndPaymentReq): Response<BaseResponseData>

    @POST("paymentStatus")
    suspend fun updateEligibilityAndPayment(@Body request: UpdateEligibilityAndPaymentReq): Response<BaseResponseData>

    @POST("consent")
    suspend fun markConsent(@Body request: MarkConsentRequest): Response<BaseResponseData>

     @POST("acceptInPrincipalAmt")
    suspend fun acceptInPrincipalAmt(@Body body: Map<String, String?>): Response<BaseResponseData>

    @POST("payAppFee")
    suspend fun proceedToPay(@Body request: PaymentRequest): Response<BaseResponseData>

    @POST("saveNeighborReference")
    suspend fun saveNeighborReference(@Body postData: NeighborReferencePostData): Response<BaseResponseData>?

    @POST("getScreenDetails")
    suspend fun getScreenDetails(@Body body: Map<String, String>): Response<ScreenDetailsToNavigateData>

    @POST("submitPresanctionDocs")
    suspend fun submitPresanctionDocs(@Body body: PresanctionDocsRequestData): Response<BaseResponseData>

    @POST("amDocScreeningStatus")
    suspend fun amDocScreeningStatus(@Body body: DocScreeningStatusPost): Response<BaseResponseData>

    @POST("amDocScreeningStatus")
    suspend fun amDocScreeningStatus(@Body body: DocScreeningStatusPostNew): Response<BaseResponseData>

     @POST("docScreeningStatus")
    suspend fun docScreeningStatus(@Body body: DocScreeningStatusPost): Response<BaseResponseData>

     @POST("docScreeningStatus")
    suspend fun docScreeningStatus(@Body body: DocScreeningStatusPostNew): Response<BaseResponseData>

    @POST("updateAMOtherDetails")
    suspend fun updateAMOtherDetails(@Body body: Map<String, String?>): Response<BaseResponseData>

    @POST("updateProfessionalDetails")
    suspend fun updateProfessionalDetails(@Body body: Map<String, String?>): Response<BaseResponseData>

    @POST("updateOtherDetails")
    suspend fun updateOtherDetails(@Body body: TradeReferencePostData): Response<BaseResponseData>
/*

    @POST("updateOtherDetails")
    suspend fun updateOtherDetails(@Body body: Map<String, String>): Response<BaseResponseData>
*/

    @POST("updateCollateralDetails")
    suspend fun updateCollateralDetails(@Body body: CollateralDetailsPostData): Response<BaseResponseData>
/*

    @POST("updateCollateralDetails")
    suspend fun updateCollateralDetails(@Body body: Map<String, String>): Response<BaseResponseData>
*/

    @POST("bmSubmit")
    suspend fun bmSubmit(@Body body: FinalReportPostData): Response<BaseResponseData>

    @POST("bmAmSubmit")
    suspend fun bmAmSubmit(@Body body: FinalReportPostData): Response<BaseResponseData>

    @POST("bcmSubmit")
    suspend fun bcmSubmit(@Body body: FinalReportPostData): Response<BaseResponseData>

    @GET("getDeviations")
    suspend fun getDeviations(@Query("loanId") loanId: String?): Response<DeviationsResponseData>?

    @GET("getBankingC360")
    suspend fun getBankingC360(@Query("loanId") loanId: String?): Response<Banking360DetailsResponseData>?

    @GET("getBusinessData")
    suspend fun getBusinessData(@Query("loanId") loanId: String?): Response<BusinessDetails>?

    @GET("getOtherData")
    suspend fun getOtherData(@Query("loanId") loanId: String?): Response<CustomerDocumentAndDataResponseData>?

    @GET("getIncomeData")
    suspend fun getIncomeData(@Query("loanId") loanId: String?): Response<IncomeDetails>?

    @GET("getBMRejected")
    suspend fun getBMRejected(@Query("bmId") bmId: String?): Response<RejectedCaseData>?

    @GET("getRMRejected")
    suspend fun getRMRejected(@Query("rmId") bmId: String?): Response<RejectedCaseData>?

    @GET("getBMReassignedTo")
    suspend fun getBMReassignedTo(@Query("bmId") bmId: String?): Response<BMReassignCaseData>?

    @GET("getBMReassignedBy")
    suspend fun getBMReassignedBy(@Query("bmId") bmId: String?): Response<BMReassignCaseData>?

  @GET("getBMDisburesed")
    suspend fun getBMDisburesed(@Query("bmId") bmId: String?): Response<BMReassignCaseData>?


    @GET("saveBMReopen")
    suspend fun saveBMReopen(@Query("loanId") loanId: String?): Response<BaseResponseData>?

    @GET("getRMApproved")
    suspend fun getRMApproved(@Query("rmId") rmId: String?): Response<RMApprovedCaseResponse>?

    @GET("bmDecision")
    suspend fun bmDecision(@Query("loanId") loanId: String?): Response<BMDecisionResponse>?

    @GET("bcmDecision")
    suspend fun bcmDecision(@Query("loanId") loanId: String?): Response<BMDecisionResponse>?

    @GET("getCust360Status")
    suspend fun getCust360Status(@Query("loanId") loanId: String?): Response<BMDecisionResponse>?

    @GET("getBMApproved")
    suspend fun getBMApproved(@Query("bmId") bmId: String?): Response<RMApprovedCaseResponse>?

    @GET("getBCMApproved")
    suspend fun getBCMApproved(@Query("bcmId") bcmId: String?): Response<RMApprovedCaseResponse>?

    @GET("getCollateralMstr")
    suspend fun getCollateralMstr(@Query("mstrId") mstrId: String?): Response<CollateralResponseData>?

    @GET("getAssetType")
    suspend fun getAssetType(): Response<CollateralResponseData>?

    @GET("getDocMstr")
    suspend fun getDocMstr(@Query("mstrId") mstrId: String?): Response<CollateralResponseData>?

    @POST("getBMDashboard")
    suspend fun getBMDashboard(@Body map: HashMap<String, String>): Response<BMDashboardResponseData>?

    @POST("getBCMDashboard")
    suspend fun getBCMDashboard(@Body map: HashMap<String, String>): Response<BMDashboardResponseData>?

    @GET("checkRLTStatus")
    suspend fun checkRLTStatus(@Query("loanId") loanId: String?): Response<CheckRLTStatusResponse>?

    @GET("getLegalBcmData")
    suspend fun getLegalBcmData(@Query("loanId") loanId: String?): Response<GetLegalnTechBCMResponse>?

     @GET("getDocCategories")
    suspend fun getDocCategories(@Query("loanId") loanId: String?): Response<DocCategoriesList>?

    @GET("getTechBcmData")
    suspend fun getTechBcmData(@Query("loanId") loanId: String?): Response<GetLegalnTechBCMResponse>?

    @GET("sendPaymentLink")
    suspend fun sendPaymentLink(@Query("loanId") loanId: String?): Response<BaseResponseData>?

    @POST("rmRequestWaiver")
    suspend fun rmRequestWaiver(@Body map: HashMap<String, String>): Response<BaseResponseData>?

    @POST("bmRequestWaiver")
    suspend fun bmRequestWaiver(@Body map: HashMap<String, String>): Response<BaseResponseData>?

    @POST("updateUserProfile")
    suspend fun updateUserProfile(@Body map: HashMap<String, String>): Response<BaseResponseData>?

    @POST("sendToken")
    suspend fun sendToken(@Body map: HashMap<String, String>): Response<BaseResponseData>?

    @POST("rmLeadListByTime")
    suspend fun rmLeadListByTime(@Body map: HashMap<String, String>): Response<RMLeadListResponse>?

    @POST("updateBusinessDetails")
    suspend fun updateBusinessDetails(@Body body: BusinessDetailsPostData?): Response<BaseResponseData>?
/*

    @POST("updateBusinessDetails")
    suspend fun updateBusinessDetails(@Body map: HashMap<String, String>): Response<BaseResponseData>?
*/

    @POST("updateIncomeDetails")
    suspend fun updateIncomeDetails(@Body body: IncomeDetailsPostData?): Response<BaseResponseData>?
/*
 @POST("updateIncomeDetails")
    suspend fun updateIncomeDetails(@Body map: HashMap<String, String>): Response<BaseResponseData>?
*/

    @POST("bcmRLTSubmit")
    suspend fun bcmRLTSubmit(@Body map: HashMap<String, String>): Response<BaseResponseData>?

    @POST("submitLPC")
    suspend fun submitLPC(@Body map: HashMap<String, String>): Response<BaseResponseData>?

    @POST("updateDeviations")
    suspend fun updateDeviations(@Body body: DeviationsResponseData): Response<BaseResponseData>


    @POST("getUserRole")
    suspend fun getUserRole(@Body body: HashMap<String, String>): Response<RoleResponse>

    @POST("moveToScreening")
    suspend fun moveToScreening(@Body body: HashMap<String, String>): Response<RoleResponse>

    @POST("getScreenData")
    suspend fun getScreenData(@Body body: HashMap<String, String>): Response<CustomerDocumentAndDataResponseData>

     @POST("bcmLegalSubmit")
    suspend fun bcmLegalSubmit(@Body body: HashMap<String, String>): Response<BaseResponseData>

    @POST("bcmTechSubmit")
    suspend fun bcmTechSubmit(@Body body: HashMap<String, String>): Response<BaseResponseData>

    @POST("submitRMPendingDocs")
    suspend fun submitRMPendingDocs(@Body body: HashMap<String, Any>): Response<BaseResponseData>

    @GET("getIncSrcMstr")
    suspend fun getIncSrcMstr(): Response<CollateralResponseData>?

    @GET("getRMPendingDocs")
    suspend fun getRMPendingDocs(@Query("loanId") loanId: String?): Response<RMPendingDocs>?


    @GET("getLoanDataStatus")
    suspend fun getLoanDataStatus(@Query("loanId") loanId: String?): Response<ScreeningNavDataResponse>?

    @GET("getRMReAssignedStatus")
    suspend fun getRMReAssignedStatus(@Query("loanId") loanId: String?): Response<RmReAssignNavResponse>?

    @GET("getAMScreenStatus")
    suspend fun getAMScreenStatus(@Query("loanId") loanId: String?): Response<RmReAssignNavResponse>?


    @GET("getRMInprogress")
    suspend fun getRMInprogress(@Query("rmId") rmId: String?): Response<RMInProgressResponse>?

    @GET("getBMInprogress")
    suspend fun getBMInprogress(@Query("bmId") bmId: String?): Response<RMInProgressResponse>?

    @GET("submitAMCase")
    suspend fun submitAMCase(@Query("loanId") loanId: String?): Response<BaseResponseData>?

    @GET("getBCMInprogress")
    suspend fun getBCMInprogress(@Query("bcmId") bcmId: String?): Response<RMInProgressResponse>?

    @GET("getOTPforEmp")
    suspend fun getOTPforEmp(@Query("empCode") empCode: String?): Response<BaseResponseData>

    @GET("getAMs")
    suspend fun getAMs(@Query("rmId") rmId: String?): List<AmListModel>

    @GET("getExceptionRpt")
    suspend fun getExceptionRpt(@Query("loanId") loanId: String?): Response<EXceptionReportResponse>

    @GET("getBCMReAssigned")
    suspend fun getBCMReAssigned(@Query("bcmId") bcmId: String?): Response<GetBCMReAssignedResponse>

    @GET("getRMOpsCases")
    suspend fun getRMOpsCases(@Query("loanId") loanId: String?): Response<GetRMOpsCasesResponse>

    @POST("submitRMOpsCases")
    suspend fun submitRMOpsCases(@Body body: HashMap<String, Any?>): Response<BaseResponseData>

    @POST("verifyOTPforEmp")
    suspend fun verifyOTPforEmp(@Body body: Map<String, String>): Response<BaseResponseData>

    @POST("getCustomerId")
    suspend fun getCustomerId(@Body body: Map<String, String>): Response<BaseResponseData>

    @POST("getCustomerId2")
    suspend fun getCustomerId2(@Body body: Map<String, String>): Response<BaseResponseData>

    @POST("storeMpin")
    suspend fun storeMpin(@Body body: Map<String, String>): Response<BaseResponseData>

    @POST("addAM")
    suspend fun addAM(@Body body: Map<String, String>): Response<BaseResponseData>

    @POST("submitExceptionRpt")
    suspend fun submitExceptionRpt(@Body body: Map<String, String>): Response<BaseResponseData>

    @POST("payRLTFee")
    suspend fun payRLTFee(@Body body: Map<String, String>): Response<BaseResponseData>

    @POST("getDocSubCategories")
    suspend fun getDocSubCategories(@Body body: Map<String, String>): Response<DocSubCategories>


    /*AM APis start*/

    @POST("sendOTP")
    suspend fun sendOTP(@Body body: Map<String, String>): Response<AMSendOtpResponse>

    @POST("saveAMKycDetails")
    suspend fun saveAMKycDetail(@Body body: KYCPostData?): Response<BaseResponseData>?

    @POST("saveAMPersonalDetails")
    suspend fun saveAMPersonalDetail(@Body body: AMPersonalDetailsData?): Response<PersonalResponseData>?

    @POST("saveAMProfessionalDetails")
    suspend fun saveAMProfessionalDetails(@Body body: ProfessionPostData?): Response<PersonalResponseData>?

    @POST("saveAMOtherDetails")
    suspend fun saveAMOtherDetails(@Body body: AMOtherdetailsPostData?): Response<PersonalResponseData>?

    @GET("rest/GetMstr/amProfession")
    suspend fun getamOccupationName(): Response<DetailsResponseData>?

    @GET("rest/GetMstr/amStates")
    suspend fun getamStates(): Response<DetailsResponseData>?

    @GET("bmAMReason")
    suspend fun bmAMReason(): Response<DetailsResponseData>?

    @GET("getAMRejectStatus")
    suspend fun getAMRejectStatus(@Query("amId") amId: String?): Response<AmCompletedScreens>

    @GET("getCoc")
    suspend fun getCoc(): Response<BaseResponseData>

    @GET("getTCA")
    suspend fun getTCA(): Response<BaseResponseData>


    @POST("getAMScreenData")
    suspend fun getAMScreenData(@Body body: HashMap<String,String?>): Response<BMAmDocnDataResponse>?

    @POST("getLegalQueue")
    suspend fun getLegalQueue(@Body body: HashMap<String,String>): Response<BaseResponseData>?

    @POST("bcmReAssignSubmit")
    suspend fun bcmReAssignSubmit(@Body body: HashMap<String,String>): Response<BaseResponseData>?

    @POST("getBMRMStatus")
    suspend fun getBMRMStatus(@Body body: HashMap<String,String>): Response<BmRmStatusModel>?

    @POST("submitMultipleDocs")
        suspend fun submitMultipleDocs(@Body body: SubmitMultipleDocsRequest): Response<BaseResponseData>?




    /* @POST("getScreenDetails")
     Request:
     {
         "loanId":"R1234"
     }

     Response:
     {
         "screenId":"businessId",
         "leadId":"LE1234",
         "loanId":"LO1234",
         "customerId":"c1234",
         "businessId":"b123",
         "incomeId":"b123"
     }*/

}