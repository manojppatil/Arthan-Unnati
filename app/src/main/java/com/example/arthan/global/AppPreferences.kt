package com.example.arthan.global

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.arthan.BuildConfig

class AppPreferences(context: Context) {

    private val mSharedPreference: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)

    fun addString(key: String, value: String?): Boolean =
        with(mSharedPreference.edit()) {
            putString(key, value)
            commit()
        }

    fun addInteger(key: String, value: Int?): Boolean =
        with(mSharedPreference.edit()) {
            value?.let {
                putInt(key, it)
            }
            commit()
        }

    fun addBoolean(key: String, value: Boolean?): Boolean =
        with(mSharedPreference.edit()) {
            value?.let {
                putBoolean(key, it)
            }
            commit()
        }

    fun addFLoat(key: String, value: Float?): Boolean =
        with(mSharedPreference.edit()) {
            value?.let {
                putFloat(key, it)
            }
            commit()
        }

    fun addStringSet(key: String, value: Set<String>?): Boolean =
        with(mSharedPreference.edit()) {
            putStringSet(key, value)
            commit()
        }

    fun getString(key: String, defaultValue: String?): String? = with(mSharedPreference) {
        this.getString(key, defaultValue)
    }

    fun getString(key: String): String? = getString(key, null)

    fun getInteger(key: String, defaultValue: Int?): Int = with(mSharedPreference) {
        this.getInt(key, defaultValue ?: 0)
    }

    fun getInteger(key: String): Int? = getInteger(key, 0)

    fun getFloat(key: String, defaultValue: Float?): Float = with(mSharedPreference) {
        this.getFloat(key, defaultValue ?: 0f)
    }

    fun getFloat(key: String): Float = getFloat(key, 0f)

    fun getBoolean(key: String, defaultValue: Boolean?): Boolean = with(mSharedPreference) {
        this.getBoolean(key, defaultValue ?: false)
    }

    fun getBoolean(key: String): Boolean = getBoolean(key, false)

    fun getStringSet(key: String, defaultValue: Set<String>?): Set<String>? =
        with(mSharedPreference) {
            this.getStringSet(key, defaultValue ?: setOf())
        }

    fun getStringSet(key: String): Set<String>? = getStringSet(key, setOf())

    fun clear() {
        with(mSharedPreference.edit()) {
            clear()
            commit()
        }
    }

    fun remove(key: String) = with(mSharedPreference.edit()) {
        remove(key)
        commit()
    }

    object Key {
        const val LoginType = "login_type"
        const val Pincode = "pin_code"
        const val City = "city"
        const val State = "state"
        const val AddressLine1 = "address_lin1_one"
        const val AddressLine2 = "address_lin1_two"
        const val LeadId = "lead_id"
        const val LoanId = "loan_id"
        const val CustomerId = "customer_id"
        const val PrincipleLoanAmount = "in_principle_lian_amount"
        const val BusinessId = "business_id"
    }

    companion object {
        private const val PREFERENCE_NAME = "${BuildConfig.APPLICATION_ID}.app_preferences"
        private var sInstance: AppPreferences? = null

        fun getInstance(): AppPreferences {
            if (sInstance == null) {
                getInstance(ArthanApp.getAppInstance())
            }
            return sInstance!!
        }

        fun getInstance(context: Context?): AppPreferences {
            if (sInstance == null) {
                sInstance = AppPreferences(context ?: ArthanApp.getAppInstance())
            }
            return sInstance!!
        }

        fun init(context: Context) {
            sInstance = AppPreferences(context)
        }
    }
}