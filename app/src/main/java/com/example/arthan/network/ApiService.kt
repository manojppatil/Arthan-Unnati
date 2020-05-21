package com.example.arthan.network

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
import org.json.JSONObject
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
    suspend fun getCollateralOwnership (): Response<DetailsResponseData>?

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
    suspend fun saveKycDetail(@Body body: KYCPostData?): Response<BaseResponseData>?

    @POST("saveBusinessDetails")
    suspend fun saveBusinessDetail(@Body body: BusinessDetailsPostData?): Response<BusinessDetailsResponseData>?

    @POST("rmResubmitBusiness")
    suspend fun rmResubmitBusiness(@Body body: BusinessDetailsPostData?): Response<BusinessDetailsResponseData>?

    @POST("saveIncomeDetails")
    suspend fun saveIncomeDetail(@Body body: IncomeDetailsPostData?): Response<BaseResponseData>?

    @POST("rmResubmitIncome")
    suspend fun rmResubmitIncome(@Body body: IncomeDetailsPostData?): Response<BaseResponseData>?

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

    @POST("rest/ocrAdhrBack")
    suspend fun getAadharCardInfo(@Body ocrRequest: OcrRequest): Response<CardResponse>

    @POST("rest/ocrVoter")
    suspend fun getVoterCardInfo(@Body ocrRequest: OcrRequest): Response<CardResponse>

    @POST("rest/verifyACPan")
    suspend fun verifyPANCardInfo(@Body body: Map<String,String?>): Response<VerifyCardResponse>

    @POST("rest/verifyACPan")
    suspend fun verifyAAdharCardInfo(@Body pan: String?): Response<VerifyCardResponse>

    @POST("rest/verifyACPan")
    suspend fun verifyVoterIdCardInfo(@Body pan: String?): Response<VerifyVoterCardResponse>

    @POST("rmHome")
    suspend fun getRMDashboardData(@Body request: RMDashboardRequest): Response<RMDashboardData>

    @POST("rmScreeningList")
    suspend fun getScreeningList(@Body request: RMDashboardRequest): Response<ScreeningListResponse>

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
    suspend fun getRMReviewList(@Body request: RMDashboardRequest):Response<RMReviewReponse>

    @POST("verifyOTP")
    suspend fun verifyOTP(@Body request: VerifyOTPRequest): Response<VerifyOTPResponse>

    @POST("updateScreen")
    suspend fun updateEligibilityAndPaymentInitiate(@Body request: UpdateEligibilityAndPaymentReq) : Response<BaseResponseData>

     @POST("paymentStatus")
    suspend fun updateEligibilityAndPayment(@Body request: UpdateEligibilityAndPaymentReq) : Response<BaseResponseData>

    @POST("consent")
    suspend fun markConsent(@Body request: MarkConsentRequest): Response<BaseResponseData>

    @POST("payAppFee")
    suspend fun proceedToPay(@Body request: PaymentRequest): Response<BaseResponseData>

    @POST("saveNeighborReference")
    suspend fun saveNeighborReference(@Body postData: NeighborReferencePostData): Response<BaseResponseData>?

    @POST("getScreenDetails")
    suspend fun getScreenDetails(@Body body: Map<String,String>):Response<ScreenDetailsToNavigateData>

    @POST("submitPresanctionDocs")
    suspend fun submitPresanctionDocs(@Body body: PresanctionDocsRequestData):Response<BaseResponseData>

    @POST("docScreeningStatus")
    suspend fun docScreeningStatus(@Body body: DocScreeningStatusPost):Response<BaseResponseData>

    @POST("updateOtherDetails")
    suspend fun updateOtherDetails(@Body body: Map<String, String>):Response<BaseResponseData>

    @POST("bmSubmit")
    suspend fun bmSubmit(@Body body: FinalReportPostData):Response<BaseResponseData>

    @POST("bcmSubmit")
    suspend fun bcmSubmit(@Body body: FinalReportPostData):Response<BaseResponseData>

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


    @GET("saveBMReopen")
    suspend fun saveBMReopen(@Query("loanId") loanId: String?): Response<BaseResponseData>?

    @GET("getRMApproved")
    suspend fun getRMApproved(@Query("rmId") rmId: String?): Response<RMApprovedCaseResponse>?

    @GET("bmDecision")
    suspend fun bmDecision(@Query("loanId") loanId: String?): Response<BMDecisionResponse>?

    @GET("bcmDecision")
    suspend fun bcmDecision(@Query("loanId") loanId: String?): Response<BMDecisionResponse>?

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

    @GET("sendPaymentLink")
    suspend fun sendPaymentLink(@Query("loanId") loanId: String?): Response<BaseResponseData>?

    @POST("rmRequestWaiver")
    suspend fun rmRequestWaiver(@Body map: HashMap<String,String>): Response<BaseResponseData>?

    @POST("bmRequestWaiver")
    suspend fun bmRequestWaiver(@Body map: HashMap<String,String>): Response<BaseResponseData>?

    @POST("sendToken")
    suspend fun sendToken(@Body map: HashMap<String,String>): Response<BaseResponseData>?

    @POST("updateBusinessDetails")
    suspend fun updateBusinessDetails(@Body map: HashMap<String,String>): Response<BaseResponseData>?

    @POST("updateIncomeDetails")
    suspend fun updateIncomeDetails(@Body map: HashMap<String,String>): Response<BaseResponseData>?

    @POST("bcmRLTSubmit")
    suspend fun bcmRLTSubmit(@Body map: HashMap<String,String>): Response<BaseResponseData>?

 @POST("submitLPC")
    suspend fun submitLPC(@Body map: HashMap<String,String>): Response<BaseResponseData>?

    @POST("updateDeviations")
    suspend fun updateDeviations(@Body body: DeviationsResponseData):Response<BaseResponseData>

 @GET("getIncSrcMstr")
 suspend fun getIncSrcMstr(): Response<CollateralResponseData>?



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