package com.example.arthan.dashboard.bm.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.example.arthan.R
import com.example.arthan.dashboard.bm.DocumentPreviewActivty
import com.example.arthan.dashboard.bm.DocumentVerificationFragmentNew
import com.example.arthan.global.ArthanApp
import com.example.arthan.lead.model.responsedata.RequireDocs
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.squareup.picasso.Picasso


class BMVerificationkycDocsAdapter(
    context: Context,
    detailsList: ArrayList<RequireDocs>
): RecyclerView.Adapter<BMVerificationkycDocsAdapter.BankingVH>() {
    private val mList: MutableList<RequireDocs> = detailsList
    private var context:Context = context
    private var textColor:Int=Color.parseColor("#09327a")

    inner class BankingVH(root: View): RecyclerView.ViewHolder(root){

        fun bind(position:Int){


            val btn_disapprove= itemView.findViewById<Button>(R.id.btn_disapprove)
            val btn_approve=  itemView.findViewById<Button>(R.id.btn_approve)
            val type=itemView.findViewById<TextView>(R.id.txt_card)

            type.text=mList[position].docName
            type.setOnClickListener {
                showPreview(mList[position].docUrl)
            }
            btn_approve.setOnClickListener {
                btn_approve.setBackgroundResource(R.drawable.ic_next_enabled)
                btn_disapprove.setBackgroundResource(R.drawable.ic_next_disable)
                btn_approve.setTextColor(Color.WHITE)
                btn_disapprove.setTextColor(textColor)
                DocumentVerificationFragmentNew.modifyDocAt(mList[position].docId,mList[position].docName,mList[position].docUrl,"true",position)

            }
            btn_disapprove.setOnClickListener {
                btn_disapprove.setBackgroundResource(R.drawable.ic_next_enabled)
                btn_approve.setBackgroundResource(R.drawable.ic_next_disable)
                btn_disapprove.setTextColor(Color.WHITE)
                btn_approve.setTextColor(textColor)
                DocumentVerificationFragmentNew.modifyDocAt(mList[position].docId,mList[position].docName,mList[position].docUrl,"false",position)

            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankingVH {
        val view= LayoutInflater.from(context).inflate(R.layout.list_item,parent,false)
        return BankingVH(view)
    }

    override fun getItemCount(): Int = mList!!.size

    override fun onBindViewHolder(holder: BankingVH, position: Int) {
        holder.bind(position)
    }

    fun showPreview(urlString:String?)
    {
        if((ArthanApp.getAppInstance().loginRole=="BM"||ArthanApp.getAppInstance().loginRole=="BCM")&&(urlString!=null&&urlString!="")) {

            context.startActivity(Intent(context,DocumentPreviewActivty::class.java).apply {
                putExtra("url",urlString)
            })
            var alert = AlertDialog.Builder(context)
            val view = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.shop_img_preview_dialog, null)
            alert.setView(view)
            val close = view?.findViewById<ImageView>(R.id.closePreview)
            val edit = view?.findViewById<ImageView>(R.id.edit)
            val preview = view?.findViewById<ImageView>(R.id.preview)
            edit?.visibility = View.GONE
//            Glide.with(context!!).load(urlString).into(preview!!)
            val dialog = alert.create()
          ///  dialog.show()
            close?.setOnClickListener { dialog.dismiss() }
//            Picasso..load(urlString).resize(600,600).into(preview)

            val myOptions = RequestOptions()
                .fitCenter() // or centerCrop
                .override(100, 100)

          /*  Glide.with(context)
                .asBitmap()
                .apply(myOptions)
                .load(urlString)
                .into(preview!!)*/
            /*Glide.with(context)
                .load(urlString)
                .skipMemoryCache(true)
                .override(200,200)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Error in loading image ---> ${e?.message}")
                        return false
                    }


                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e(TAG, "Success in loading image ")
                        return false
                    }
                })
                .into(preview!!)*/
          /*  val builder=ImageLoaderConfiguration.Builder(context)
            val imageLoader = ImageLoader.getInstance()
            imageLoader.init(builder.build())
            imageLoader.clearDiskCache()
            imageLoader.clearMemoryCache()
            imageLoader.displayImage(urlString, preview)*/


        }else
        {
            Toast.makeText(context,"no image url",Toast.LENGTH_LONG).show()
        }
    }
}