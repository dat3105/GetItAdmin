package com.dinhconghien.getitadmin.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Model.Laptop
import com.dinhconghien.getitadmin.R

class BillAccepted_Adapter( var listInvoice : ArrayList<Laptop>)
    : RecyclerView.Adapter<BillAccepted_Adapter.ViewHolder>(){
    var listener : OnItemClickedListener? = null
    fun setListBillAccep(listInvoice: ArrayList<Laptop>){
        this.listInvoice = listInvoice
        notifyDataSetChanged()
    }


    fun setOnItemClickedListener(listener: OnItemClickedListener) {
        this.listener = listener
    }

    interface OnItemClickedListener {
        fun onClicked(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):ViewHolder {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.item_invoice_accepted, null)
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return listInvoice.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BillAccepted_Adapter.ViewHolder, position: Int) {
        val lapModel = listInvoice.get(position)
        holder.tv_nameLap.text = lapModel.nameLap
        var price = lapModel.priceLap.toString()
        if (price.length == 7) {
            val firstChar = price.substring(0, 1)
            val middleChar = price.substring(1, 4)
            val lastChar = price.substring(4, 7)
            price = "$firstChar.$middleChar.$lastChar"
        } else if (price.length == 8) {
            val firstChar = price.substring(0, 2)
            val middleChar = price.substring(2, 5)
            val lastChar = price.substring(5, 8)
            price = "$firstChar.$middleChar.$lastChar"
        }
        holder.tv_priceLap.text = "$price VNĐ"
        Glide.with(holder.itemView).load(lapModel.avaLap).fitCenter().into(holder.imv_avaLap)
        holder.tv_nameBrand.text = lapModel.nameBrand
        holder.tv_amountInCart.text = "x ${lapModel.amountInCart}"
        if (lapModel.wasRated == false){
            holder.tv_statusRatimg.text = "(Chưa đánh giá)"
        }else{
            holder.tv_statusRatimg.text = "(Đã đánh giá)"
        }
        holder.itemView.setOnClickListener {
            listener?.onClicked(position)
        }

    }
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val imv_avaLap : ImageView = itemView.findViewById(R.id.imv_avaLap_itemBillAccept)
        val tv_nameLap : TextView = itemView.findViewById(R.id.tv_nameLapTop_itemBillAccept)
        val tv_priceLap : TextView = itemView.findViewById(R.id.tv_priceLap_paymentItem)
        val tv_nameBrand  : TextView = itemView.findViewById(R.id.tv_nameBrand_itemBillAccept)
        val tv_amountInCart : TextView = itemView.findViewById(R.id.tv_amountInCart_itemBillAccept)
        val tv_statusRatimg : TextView = itemView.findViewById(R.id.tv_statusRating_itemBillAccept)

    }
}