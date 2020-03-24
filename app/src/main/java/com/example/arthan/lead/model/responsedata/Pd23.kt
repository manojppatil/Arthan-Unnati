package com.example.arthan.lead.model.responsedata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pd23(
    var adequateInventory: String?,
    var allowAdvCredit: String?,
    var businessCashCredit: String?,
    var businessSkillset: String?,
    var businessreferencePayontime: String?,
    var childrenMedium: String?,
    var collateralOwership: String?,
    var demographic: String?,
    var dependencyNoofProducts: String?,
    var dependencyNoofSuppliers: String?,
    var dpendencyNoofCustomers: String?,
    var fourWheeler: String?,
    var furnishedsemi: String?,
    var homeLocation: String?,
    var homeOwnership: String?,
    var impactofCompetition: String?,
    var ladyCoapplicant: String?,
    var maintenanceBusinessRecords: String?,
    var neighbourReferenceStability: String?,
    var networth: String?,
    var officepremisesownership: String?,
    var perishability: String?,
    val pd23_id: String? = "",
    var qualityofStock: String?,
    var referenceFromNeighboursAndAssociations: String?,
    var refrigerate: String?,
    var seasonality: String?,
    var successionPlan: String?,
    var timeCurrentLocation_yrs: String?,
    var tunoverToTotalExistingLoan: String?,
    var tv: String?,
    var twoWheeler: String?,
    var washingMachine: String?
) : Parcelable