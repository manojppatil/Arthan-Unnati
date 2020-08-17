package com.example.arthan.lead.model.responsedata

data class BMAmDocnDataResponse(

    val docDetails: DocDetailsAM,
    val personalDetails: PersonalDetailsAM,
    val professionalDetails: ProfessionalDetailsAM,
    val otherDetails: OtherDetailsList
)

data class PersonalDetailsAM(

    val loanId: String,
    val leadId: String,
    val custId: String,
    val customerId: String,
    val ageofCustomer: String,
    val title: String,
    val fullName: String,
    val maidenName: String,
    val applicantPanNo: String,
    val applicantAadharNo: String,
    val fatherOrSpousename: String,
    val motherName: String,
    val dob: String,
    val email: String,
    val contactNo: String,
    val gender: String,
    val maritalStatus: String,
    val nationality: String,
    val educationlevel: String,
    val occupationType: String,
    val occupation: String,
    val sourceofIncome: String,
    val grossannualIncome: String,
    val addressLine1: String,
    val addressLine2: String,
    val landmark: String,
    val pinCode: String,
    val areaName: String,
    val city: String,
    val district: String,
    val state: String,
    val applicantType: String,
    val rshipWithApplicant: String,
    val customerType: String,
    val consent: String,
    val otpVerified: String,
    val appFeePaid: String,
    val category: String,
    val religion: String,
    val addrFlag: String,
    val addressLine1p: String,
    val addressLine2p: String,
    val landmarkp: String,
    val pinCodep: String,
    val areaNamep: String,
    val cityp: String,
    val districtp: String,
    val statep: String,
    val amId: String,
    val whatsappNo: String,
    val addressProofUrl: String
)

data class ProfessionalDetailsAM(
    val amId: String,
    val educationlevel: String,
    val profession: String,
    val grossannualIncome: String,
    val bankName: String,
    val acNumber1: String,
    val acNumber2: String,
    val upiId: String,
    val ifscCode: String,
    val chequeUrl: String,
    val prof: String
)

data class DocDetailsAM(
    val loanId: String?,
    val customerId: String?,
    val custId: String?,
    val applicantType: String?,
    val panUrl: String?,
    val aadharFrontUrl: String?,
    val aadharBackUrl: String?,
    val voterUrl: String?,
    val paApplicantPhoto: String?,
    val businessProof: String?,
    val businessAddrProof: String?,
    val incomeProof: String?,
    val docId: String?,
    val docName: String?,
    val docUrl: String?,
    val docStatus: String?,
    val chequeUrl: String?
)

data class OtherDetailsList(
    val smartphone: String,
    val twoWheeler: String,
    val languages: ArrayList<LanguagesAM>,
    val references: ArrayList<References>,
    val otherId:String

)

data class LanguagesAM(
    val lang: String,
    val speak: String,
    val read: String,
    val write: String
)

data class References(
    val name: String?,
    val mobNo: String?,
    val address: String?,
    val profession: String?,
    val comments: String?
)