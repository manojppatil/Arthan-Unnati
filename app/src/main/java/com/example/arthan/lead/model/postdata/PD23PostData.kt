package com.example.arthan.lead.model.postdata

import com.example.arthan.model.PD2Data
import com.example.arthan.model.PD3Data
import com.google.gson.annotations.SerializedName

data class PD23PostData(
    var businessCashCredit: String? = "",
    var dependencyNoofProducts: String? = "",
    var dpendencyNoofCustomers: String? = "",
    var dependencyNoofSuppliers: String? = "",
    var qualityofStock: String? = "",
    var furnishedsemi: String? = "",
    var fourWheeler: String? = "",
    var loanId: String? = "",
    var customerId: String? = "",
    var refrigerate: String? = "",
    var tv: String? = "",
    var twoWheeler: String? = "",
    var washingMachine: String? = "",
    var childrenMedium: String? = "",
    var homeLocation: String? = "",
    var adequateInventory: String? = "",
    var neighbourReferenceStability: String? = "",
    var maintenanceBusinessRecords: String? = "",
    var homeOwnership: String? = "",
    var officepremisesownership: String? = "",
    @SerializedName("timeCurrentLocation(years)")
    var timeCurrentLocation_years: String? = "",
    var ladyCoapplicant: String? = "",
    var allowAdvCredit: String? = "",
    var businessreferencePayontime: String? = "",
    var collateralOwership: String? = "",
    var successionPlan: String? = "",
    var businessSkillset: String? = "",
    var seasonality: String? = "",
    var perishability: String? = "",
    var tunoverToTotalExistingLoan: String? = "",
    var networth: String? = "",
    var impactofCompetition: String? = "",
    var referenceFromNeighboursAndAssociations: String? = "",


    var demographic: String? = ""
) {
    fun add(pd2Data: PD2Data) {
        businessCashCredit = pd2Data.businessCashCredit
        dependencyNoofProducts = pd2Data.dependencyNoofProducts
        dpendencyNoofCustomers = pd2Data.dpendencyNoofCustomers
        dependencyNoofSuppliers = pd2Data.dependencyNoofSuppliers
        qualityofStock = pd2Data.qualityofStock
        impactofCompetition = pd2Data.impactofCompetition
        networth = pd2Data.networth
        tunoverToTotalExistingLoan = pd2Data.tunoverToTotalExistingLoan
        seasonality = pd2Data.seasonality
        perishability = pd2Data.perishability
        businessSkillset = pd2Data.businessSkillset
        successionPlan = pd2Data.successionPlan
        collateralOwership = pd2Data.collateralOwership
        businessreferencePayontime = pd2Data.businessreferencePayontime
        allowAdvCredit = pd2Data.allowAdvCredit
        ladyCoapplicant = pd2Data.ladyCoapplicant
        timeCurrentLocation_years = pd2Data.timeCurrentLocation_years
        officepremisesownership = pd2Data.officepremisesownership
        homeOwnership = pd2Data.homeOwnership
        maintenanceBusinessRecords = pd2Data.maintenanceBusinessRecords
        referenceFromNeighboursAndAssociations = pd2Data.referenceFromNeighboursAndAssociations
        adequateInventory = pd2Data.adequateInventory
        neighbourReferenceStability = pd2Data.neighbourReferenceStability
        homeLocation = pd2Data.homeLocation
    }

    fun add(pd3Data: PD3Data) {
        furnishedsemi = pd3Data.furnishedsemi
        tv = pd3Data.tv
        refrigerate = pd3Data.refrigerate
        washingMachine = pd3Data.washingMachine
        twoWheeler = pd3Data.twoWheeler
        fourWheeler = pd3Data.fourWheeler
        childrenMedium = pd3Data.childrenMedium
    }
}