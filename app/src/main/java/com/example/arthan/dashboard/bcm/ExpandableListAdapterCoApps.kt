package com.example.arthan.dashboard.bcm

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.arthan.R
import com.example.arthan.dashboard.bm.adapter.BMCoAppKycDocsAdapter
import com.example.arthan.lead.model.responsedata.RequireDocs
import kotlinx.android.synthetic.main.document_verification_fragment_new.*


class ExpandableListAdapterCoApps(
    private val _context: Context, // header titles
    private val coAppKycDocs:ArrayList<RequireDocs>,

    private val _listDataHeader: ArrayList<String> = ArrayList(),
    private val _listDataChild: HashMap<String, RequireDocs> = HashMap()
) : BaseExpandableListAdapter() {

  /*  init {

        for(i in 0 until coAppKycDocs.size )
        {
            _listDataHeader.add(coAppKycDocs[i].loanId)
            _listDataChild[coAppKycDocs[i].loanId] = coAppKycDocs[i]
        }

//    // child data in format of header title, child title
//    private val _listDataChild: HashMap<String, List<String>>

    }
*/
    override fun getChild(groupPosition: Int, childPosititon: Int): Any {
    //  Toast.makeText(_context,"child size -${_listDataChild[this._listDataHeader[groupPosition]]!!}",Toast.LENGTH_LONG).show()
            return _listDataChild[this._listDataHeader[groupPosition]]!!
//        return _listDataChild[_listDataHeader[groupPosition]]!!

    }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return childPosition.toLong()
        }

        override fun getChildView(
            groupPosition: Int, childPosition: Int,
            isLastChild: Boolean, convertView: View?, parent: ViewGroup?
        ): View? {
         //   val headerTitle = getGroup(groupPosition) as String
            //    if (convertView == null) {
       /*     val infalInflater = _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val   myView = infalInflater.inflate(R.layout.list_item_coapp, parent,false)
            // }
            val lblListHeader = myView
                ?.findViewById<View>(R.id.lblListHeader) as TextView
            lblListHeader.setTypeface(null, Typeface.BOLD)
            lblListHeader.text = "headerTitle"*/
          //  var convertView: View? = convertView
            //if (convertView == null) {
                val infalInflater = _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
               val  myView = infalInflater.inflate(R.layout.list_item_coapp, parent,false)
        //    }
            val rvCoApps = myView
                ?.findViewById(R.id.rvCoAppsKycNew) as RecyclerView
            rvCoApps.adapter=
                BMCoAppKycDocsAdapter(_context,coAppKycDocs)

            return myView
        }

        override fun getChildrenCount(groupPosition: Int): Int {
//            Toast.makeText(_context,"child size -${_listDataChild.size}",Toast.LENGTH_LONG).show()

            return _listDataChild.size
//            return 1
        }

        override fun getGroup(groupPosition: Int): Any {
            return _listDataHeader[groupPosition]
        }

        override fun getGroupCount(): Int {
//            Toast.makeText(_context,"parent size -${_listDataHeader.size}",Toast.LENGTH_LONG).show()

            return _listDataHeader.size
        }

        override fun getGroupId(groupPosition: Int): Long {
            return groupPosition.toLong()
        }

        override fun getGroupView(
            groupPosition: Int, isExpanded: Boolean,
            convertView: View?, parent: ViewGroup?
        ): View? {
       //     var convertView = convertView
            val headerTitle = getGroup(0) as String
        //    if (convertView == null) {
                val infalInflater = _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
             val   myView = infalInflater.inflate(R.layout.coapp_kyc_list_group, parent,false)
           // }
            val lblListHeader = myView
                ?.findViewById<View>(R.id.lblListHeader) as TextView
            lblListHeader.setTypeface(null, Typeface.BOLD)
            lblListHeader.text = headerTitle
            return myView
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun isChildSelectable(
            groupPosition: Int,
            childPosition: Int
        ): Boolean {
            return true
        }

}
