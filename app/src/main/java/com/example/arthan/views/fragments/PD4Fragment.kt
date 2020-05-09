package com.example.arthan.views.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.arthan.R
import com.example.arthan.lead.adapter.DataSpinnerAdapter
import com.example.arthan.lead.model.Data
import com.example.arthan.lead.model.responsedata.Asset
import com.example.arthan.model.Assets
import com.example.arthan.model.PD4Data
import com.example.arthan.network.RetrofitFactory
import com.example.arthan.utils.ArgumentKey
import com.example.arthan.utils.getRupeeSymbol
import kotlinx.android.synthetic.main.assets_section.*
import kotlinx.android.synthetic.main.fragment_business_information.*
import kotlinx.android.synthetic.main.fragment_income_information.*
import kotlinx.android.synthetic.main.fragment_pd4.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class PD4Fragment : Fragment() {

    private var mPdFragmentClicklistener: PDFragmentSaveClickListener? = null
    private var assetsList:ArrayList<Assets>?=null
            private var assetsAdapter: DataSpinnerAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pd4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* if(activity?.intent?.extras?.getString("FROM").equals("BCM"))
        {

        }else
        {


        }


    */
        /*  if ((ll_income_source?.childCount ?: 0) > 1) {
            for (childCount in 1 until (ll_income_source?.childCount ?: 0)) {
                val sourceOfIncome = ll_income_source?.getChildAt(childCount)
                sourceOfIncomeList.add(
                    Income(
                        incomePerMonth = sourceOfIncome?.findViewById<TextInputEditText?>(R.id.income_per_month_input)?.text?.toString(),
                        incomeSource = source_of_income_input?.selectedItem.toString()
                    )
                )
            }
        }*/
        CoroutineScope(Dispatchers.IO).launch {
            var res=RetrofitFactory.getApiService().getAssetType()
            if(res?.body()!=null)
            {
                withContext(Dispatchers.Main) {
                    assetsAdapter=getAdapter(res.body()?.data)
                    sp_assetType.adapter=assetsAdapter
                }

            }
        }
        et_purchaseYear.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(
                activity!!,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val date =
                        dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    et_purchaseYear.setText(date)
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        if(dynamicAssets.childCount==1)
        {
         remove_button.visibility=View.GONE

        }
        addAsset.setOnClickListener {

            val partnerView =
                LayoutInflater.from(activity!!).inflate(R.layout.assets_section, null, false)
            partnerView?.findViewById<View?>(R.id.remove_button)?.setOnClickListener {

                if(dynamicAssets.childCount>1) {
                    dynamicAssets?.removeView(partnerView)
                    partnerView.findViewById<ImageView>(R.id.remove_button).visibility=View.VISIBLE
                }else
                {
                    partnerView.findViewById<ImageView>(R.id.remove_button).visibility=View.GONE

                }

            }
            partnerView?.findViewById<Spinner>(R.id.sp_assetType)?.adapter=assetsAdapter

            dynamicAssets.addView(partnerView)
            partnerView.findViewById<ImageView>(R.id.remove_button).visibility=View.VISIBLE
            partnerView?.findViewById<EditText>(R.id.et_purchaseYear)?.let { view ->
                view.setOnClickListener {
                    val c = Calendar.getInstance()
                    DatePickerDialog(
                        activity!!,
                        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                            val date =
                                dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                            view.setText(date)
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }

        }
        submitpd4.setOnClickListener {

            assetsList= ArrayList()
            assetsList!!.add(
                Assets(
                    assetType = (sp_assetType.selectedItem as Data).description,
                    ownerName = et_OwnerName.text.toString(),
                    purchaseYear = et_purchaseYear.text.toString(),
                    purchaseValue = et_purchaseValue.text.toString(),
                    currentValue = et_CurrentValue.text.toString(),
                    assetAddress = et_Adress.text.toString()
                )

            )
             if ((dynamicAssets?.childCount ?: 0) > 0) {
                   for (childCount in 1 until (dynamicAssets?.childCount ?: 0)) {
                       val assetView = dynamicAssets?.getChildAt(childCount)
                       assetsList!!.add(
                           Assets(
                               assetType = (assetView?.findViewById<Spinner>(R.id.sp_assetType)?.selectedItem as Data).description,
                               ownerName =  assetView?.findViewById<EditText>(R.id.et_OwnerName)?.text.toString(),
                               purchaseYear = assetView?.findViewById<EditText>(R.id.et_purchaseYear)?.text.toString(),
                               purchaseValue = assetView?.findViewById<EditText>(R.id.et_purchaseValue)?.text.toString(),
                               currentValue =assetView?.findViewById<EditText>(R.id. et_CurrentValue)?.text.toString(),
                               assetAddress = assetView?.findViewById<EditText>(R.id.et_Adress)?.text.toString()
                           )
                       )
                   }
               }

            val postData=PD4Data(

                loanId = activity?.intent?.getStringExtra(ArgumentKey.LoanId),
                assets =assetsList


            )
            mPdFragmentClicklistener?.onPD4Fragment(postData)
        }


    }
    private fun getAdapter(list: List<Data>?): DataSpinnerAdapter =
        DataSpinnerAdapter(context!!, list?.toMutableList() ?: mutableListOf()).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mPdFragmentClicklistener = parentFragment as? PDFragmentSaveClickListener
    }
}